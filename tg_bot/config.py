import os
from typing import List

# Telegram настройки
TELEGRAM_TOKEN = os.getenv("TELEGRAM_TOKEN", "8045810367:AAFg_MwqTCX-qJTgJFhKFf8H4HsBPFH417A")
CHAT_IDS_FILE = "chat_ids.json"

print(TELEGRAM_TOKEN)

# RabbitMQ настройки
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", "5672"))
RABBITMQ_USERNAME = os.getenv("RABBITMQ_USERNAME", "admin")
RABBITMQ_PASSWORD = os.getenv("RABBITMQ_PASSWORD", "admin")
RABBITMQ_TEMPLATE_ANOMALY_EXCHANGE = os.getenv("RABBITMQ_TEMPLATE_ANOMALY_EXCHANGE", "anomaly.exchange")
RABBITMQ_TEMPLATE_ANOMALY_QUEUE = os.getenv("RABBITMQ_TEMPLATE_ANOMALY_QUEUE", "anomaly.queue")
RABBITMQ_TEMPLATE_ANOMALY_ROUTING_KEY = os.getenv("RABBITMQ_TEMPLATE_ANOMALY_ROUTING_KEY", "anomaly.routing.key")

# Общие настройки
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")