import json
import os
import io
import logging
from typing import List
from telegram import Bot
from telegram.error import TelegramError
from telegram.request import HTTPXRequest
import mplfinance as mpf
import pandas as pd
import matplotlib.pyplot as plt

import httpx

from models import CandleDto, CandleAnomalyDto
from config import TELEGRAM_TOKEN, CHAT_IDS_FILE

logger = logging.getLogger(__name__)


class TelegramService:
    def __init__(self):
        limits = httpx.Limits(max_connections=20, max_keepalive_connections=20)
        request = HTTPXRequest(
            connect_timeout=30.0,
            read_timeout=30.0,
        )
        self.bot = Bot(token=TELEGRAM_TOKEN, request=request)

    def load_chat_ids(self) -> List[str]:
        """Загружает список chat_id из файла"""
        if not os.path.exists(CHAT_IDS_FILE):
            return []
        try:
            with open(CHAT_IDS_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except (json.JSONDecodeError, UnicodeDecodeError):
            logger.warning(f"Файл {CHAT_IDS_FILE} поврежден, создаем новый")
            self.save_chat_ids([])
            return []

    def save_chat_ids(self, chat_ids: List[str]):
        """Сохраняет список chat_id в файл"""
        with open(CHAT_IDS_FILE, "w", encoding="utf-8") as f:
            json.dump(chat_ids, f, ensure_ascii=False, indent=2)

    def add_chat_id(self, chat_id: str):
        """Добавляет chat_id в список, если его там нет"""
        chat_ids = self.load_chat_ids()
        if chat_id not in chat_ids:
            chat_ids.append(chat_id)
            self.save_chat_ids(chat_ids)
            logger.info(f"Добавлен новый chat_id: {chat_id}")

    def create_candlestick_chart(self, candles: List[CandleDto], stock_name: str) -> bytes:
        """Создает график свечей и возвращает байты"""
        try:
            df = pd.DataFrame([{
                'Date': candle.time,
                'Open': candle.open,
                'High': candle.high,
                'Low': candle.low,
                'Close': candle.close,
                'Volume': candle.volume
            } for candle in candles])

            df.set_index('Date', inplace=True)
            df.sort_index(inplace=True)

            fig, _ = mpf.plot(
                df,
                type='candle',
                style='charles',
                title=f'{stock_name}',
                ylabel='Price',
                volume=True,
                returnfig=True,
                figsize=(12, 8)
            )

            buf = io.BytesIO()
            fig.savefig(buf, format='png', bbox_inches='tight', dpi=150)
            plt.close(fig)
            return buf.getvalue()
        except Exception as e:
            logger.error(f"Ошибка при создании графика для {stock_name}: {e}")
            raise

    async def send_anomaly_notification(self, data: CandleAnomalyDto):
        """Отправляет уведомление об аномалии всем подписанным чатам"""
        try:
            message = (
                f"🔔 Аномальный объем!\n"
                f"Актив: <i>{data.name}</i>\n"
                f"Тикер: <i>{data.ticker if data.ticker else 'N/A'}</i>\n"
                f"Объем: {data.volume:,}\n"
                f"Изменение за день: {data.priceDailyChangeAsPercentage:.2f}%\n"
                f"<b>Изменение за минуту: {data.priceMinuteChangeAsPercentage:.2f}%</b>\n"
                f"Текущая цена: {data.priceCurrent:.4f}\n"
                f"Время: {data.time.strftime('%Y-%m-%d %H:%M:%S')}"
            )

            chart_bytes = self.create_candlestick_chart(data.candlesLastHour, data.name)
            chat_ids = self.load_chat_ids()

            results = []
            for chat_id in chat_ids:
                try:
                    await self.bot.send_photo(
                        chat_id=chat_id,
                        photo=io.BytesIO(chart_bytes),
                        caption=message,
                        parse_mode='HTML'
                    )
                    results.append({"chat_id": chat_id, "status": "success"})
                    logger.info(f"Сообщение отправлено в чат {chat_id}")
                except TelegramError as e:
                    error_msg = f"Ошибка отправки в чат {chat_id}: {e}"
                    logger.error(error_msg)
                    results.append({"chat_id": chat_id, "status": "error", "error": str(e)})

            return results
        except Exception as e:
            logger.error(f"Критическая ошибка при отправке уведомления: {e}")
            raise

    async def send_message(self, chat_id: str, message: str):
        """Отправляет простое текстовое сообщение"""
        try:
            await self.bot.send_message(chat_id=chat_id, text=message, parse_mode='HTML')
            return True
        except TelegramError as e:
            logger.error(f"Ошибка отправки сообщения в чат {chat_id}: {e}")
            return False
