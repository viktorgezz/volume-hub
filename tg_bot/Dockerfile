# Используем официальный образ Python
FROM python:3.10-slim

# Устанавливаем системные зависимости
RUN apt-get update && apt-get install -y \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

# Установка рабочей директории
WORKDIR /app

# Копирование файлов зависимостей и установка
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копирование исходного кода
COPY config.py .
COPY models.py .
COPY telegram_service.py .
COPY rabbitmq_consumer.py .
COPY tg_bot.py .
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Создание файла для chat_ids, если его нет
RUN touch chat_ids.json && echo "[]" > chat_ids.json

# Создание директории для логов
RUN mkdir -p /app/logs

# Экспозиция портов (если понадобится)
EXPOSE 8000

# Запуск supervisord для управления процессами
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]