import requests
from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from typing import List
from telegram import Bot
from telegram.error import TelegramError
import mplfinance as mpf
import pandas as pd
import io
import base64
import os
import json

app = FastAPI()

# Telegram настройки
TELEGRAM_TOKEN = "8045810367:AAE85Oqz9mSZR0Vm6W_qkN-w53kY4yEKGmw"
CHAT_IDS_FILE = "chat_ids.json"


# Модель для CandleDto
class CandleDto(BaseModel):
    open: float
    close: float
    high: float
    low: float
    volume: int
    time: datetime


# Модель для CandleAnomalyDto
class CandleAnomalyDto(BaseModel):
    name: str
    priceCurrent: float
    volume: int
    priceDailyChangeAsPercentage: float
    priceMinuteChangeAsPercentage: float
    time: datetime
    candlesLastHour: List[CandleDto]


def load_chat_ids():
    return ["908270204"]
    # if not os.path.exists(CHAT_IDS_FILE):
    #     return []
    # with open(CHAT_IDS_FILE, "r", encoding="utf-8") as f:
    #     return json.load(f)


# Функция для отправки сообщения в Telegram всем чатам из списка
async def send_telegram_message(message: str):
    bot = Bot(token=TELEGRAM_TOKEN)
    results = []
    chat_ids = load_chat_ids()
    for chat_id in chat_ids:
        try:
            await bot.send_message(chat_id=chat_id, text=message)
            results.append({"chat_id": chat_id, "status": "Message sent to Telegram"})
        except TelegramError as e:
            results.append({"chat_id": chat_id, "status": "Failed to send message", "error": str(e)})
    return results


def create_candlestick_chart(candles: List[CandleDto], stock_name: str) -> io.BytesIO:
    df = pd.DataFrame([{
        'Date': candle.time,
        'Open': candle.open,
        'High': candle.high,
        'Low': candle.low,
        'Close': candle.close,
        'Volume': candle.volume
    } for candle in candles])
    df.set_index('Date', inplace=True)
    # В заголовке указываем название акции
    fig, axes = mpf.plot(
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
    fig.savefig(buf, format='png', bbox_inches='tight')
    buf.seek(0)
    return buf


# POST-запрос для приема JSON и отправки в Telegram
@app.post("/data")
async def receive_candle_anomaly(data: CandleAnomalyDto):
    message = (
        f"🔔 Аномальный объем!\n"
        f"Актив: <i>{data.name}</i>\n"
        f"Объем: {data.volume}\n"
        f"Изменение за день: {data.priceDailyChangeAsPercentage}%\n"
        f"<b>Изменение за минуту: {data.priceMinuteChangeAsPercentage}%</b>\n"
        f"Текущая цена: {data.priceCurrent}\n"
        f"Время: {data.time.strftime('%Y-%m-%d %H:%M:%S')}"
    )
    chart_image = create_candlestick_chart(data.candlesLastHour, data.name)
    bot = Bot(token=TELEGRAM_TOKEN)
    telegram_results = []
    chat_ids = load_chat_ids()
    for chat_id in chat_ids:
        try:
            await bot.send_photo(
                chat_id=chat_id,
                photo=chart_image,
                caption=message,
                parse_mode='HTML'
            )
            telegram_results.append({"chat_id": chat_id, "status": "Message and chart sent to Telegram"})
        except TelegramError as e:
            telegram_results.append({"chat_id": chat_id, "status": "Failed to send message", "error": str(e)})
    return {
        "received": data.dict(),
        "status": "ok",
        "telegram": telegram_results
    }