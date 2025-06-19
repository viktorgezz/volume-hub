# Используем официальный образ Python как базовый
FROM python:3.10-slim

# Установка рабочей директории
WORKDIR /app

# Копирование файлов зависимостей
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копирование остального кода
COPY api_server.py .
COPY tg_bot.py .

# Установка supervisord для управления процессами
RUN apt-get update && apt-get install -y supervisor && rm -rf /var/lib/apt/lists/*
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

COPY chat_ids.json .

# Установка переменных окружения (можно переопределить при запуске)

# Экспозиция порта для FastAPI
EXPOSE 8000

# Запуск supervisord
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]