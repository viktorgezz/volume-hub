import asyncio
import json
import logging
import signal
import sys
import aio_pika

from models import CandleAnomalyDto
from telegram_service import TelegramService
from config import (
    RABBITMQ_HOST, RABBITMQ_PORT, RABBITMQ_USERNAME, RABBITMQ_PASSWORD,
    RABBITMQ_TEMPLATE_ANOMALY_EXCHANGE, RABBITMQ_TEMPLATE_ANOMALY_QUEUE,
    RABBITMQ_TEMPLATE_ANOMALY_ROUTING_KEY
)

logger = logging.getLogger(__name__)


class RabbitMQConsumer:
    def __init__(self):
        self.telegram_service = TelegramService()
        self.should_stop = False
        self.connection = None
        self.channel = None

    async def connect(self):
        """Устанавливает асинхронное соединение с RabbitMQ"""
        try:
            self.connection = await aio_pika.connect_robust(
                host=RABBITMQ_HOST,
                port=RABBITMQ_PORT,
                login=RABBITMQ_USERNAME,
                password=RABBITMQ_PASSWORD
            )
            self.channel = await self.connection.channel()
            await self.channel.set_qos(prefetch_count=1)

            exchange = await self.channel.declare_exchange(
                RABBITMQ_TEMPLATE_ANOMALY_EXCHANGE,
                aio_pika.ExchangeType.TOPIC,
                durable=True
            )

            queue = await self.channel.declare_queue(
                RABBITMQ_TEMPLATE_ANOMALY_QUEUE,
                durable=False
            )

            await queue.bind(
                exchange=exchange,
                routing_key=RABBITMQ_TEMPLATE_ANOMALY_ROUTING_KEY
            )

            logger.info(f"Соединение с RabbitMQ установлено. Host: {RABBITMQ_HOST}:{RABBITMQ_PORT}")
            return queue
        except Exception as e:
            logger.error(f"Ошибка подключения к RabbitMQ: {e}")
            raise

    async def process_message(self, message: aio_pika.IncomingMessage):
        """Обрабатывает сообщение из RabbitMQ"""
        async with message.process():
            try:
                logger.info(f"Получено сообщение: {len(message.body)} bytes")
                data = json.loads(message.body.decode("utf-8"))
                anomaly_data = CandleAnomalyDto(**data)

                results = await self.telegram_service.send_anomaly_notification(anomaly_data)
                logger.info(f"Результат отправки: {results}")

            except json.JSONDecodeError as e:
                logger.error(f"Ошибка парсинга JSON: {e}")
                logger.error(f"Содержимое сообщения: {message.body}")
                await message.reject(requeue=False)

            except Exception as e:
                logger.error(f"Ошибка обработки сообщения: {e}")
                logger.error(f"Содержимое сообщения: {message.body}")
                await message.reject(requeue=True)

    async def start_consuming(self):
        """Запускает потребление сообщений"""
        queue = await self.connect()
        await queue.consume(self.process_message)
        logger.info(f"Ожидание сообщений из очереди '{RABBITMQ_TEMPLATE_ANOMALY_QUEUE}'")

        while not self.should_stop:
            await asyncio.sleep(1)

    async def stop(self):
        """Останавливает потребление"""
        self.should_stop = True
        if self.connection:
            await self.connection.close()
            logger.info("Соединение с RabbitMQ закрыто")


def signal_handler(signum, frame):
    """Обработчик сигналов для graceful shutdown"""
    logger.info(f"Получен сигнал {signum}, завершаем работу...")
    if 'consumer' in globals():
        asyncio.create_task(consumer.stop())
    sys.exit(0)


if __name__ == "__main__":
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[
            logging.StreamHandler(),
            logging.FileHandler("rabbitmq_consumer.log")
        ]
    )

    loop = asyncio.get_event_loop()
    consumer = RabbitMQConsumer()

    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    try:
        loop.run_until_complete(consumer.start_consuming())
    except Exception as e:
        logger.error(f"Фатальная ошибка: {e}")
        sys.exit(1)
