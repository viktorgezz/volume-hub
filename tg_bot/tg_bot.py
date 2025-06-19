from telegram import Update
from telegram.ext import ApplicationBuilder, CommandHandler, ContextTypes
import requests  # Добавляем requests, так как он уже используется
import json
import os

API_URL = "http://localhost:8000/data"
TELEGRAM_TOKEN = "8045810367:AAE85Oqz9mSZR0Vm6W_qkN-w53kY4yEKGmw"  # Замените на ваш токен бота
CHAT_IDS_FILE = "chat_ids.json"

def load_chat_ids():
    if not os.path.exists(CHAT_IDS_FILE):
        return []
    try:
        with open(CHAT_IDS_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except (json.JSONDecodeError, UnicodeDecodeError):
        # Файл повреждён или невалиден — пересоздаём
        with open(CHAT_IDS_FILE, "w", encoding="utf-8") as f:
            f.write("[]")
        return []

def save_chat_ids(chat_ids):
    with open(CHAT_IDS_FILE, "w", encoding="utf-8") as f:
        json.dump(chat_ids, f)

# Команда /start
async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    chat_id = str(update.effective_chat.id)
    chat_ids = load_chat_ids()
    if chat_id not in chat_ids:
        chat_ids.append(chat_id)
        save_chat_ids(chat_ids)
    response = requests.get(API_URL)
    data = response.json()
    message = data.get("message", "Нет данных")
    await update.message.reply_text(message)

# Новая команда /getchatid для логирования CHAT_ID
async def get_chat_id(update: Update, context: ContextTypes.DEFAULT_TYPE):
    chat_id = update.effective_chat.id  # Извлекаем CHAT_ID из чата
    print(f"CHAT_ID: {chat_id}")  # Логируем в консоль
    await update.message.reply_text(f"CHAT_ID: {chat_id}")  # Отправляем CHAT_ID пользователю

if __name__ == "__main__":
    app_telegram = ApplicationBuilder().token(TELEGRAM_TOKEN).build()
    app_telegram.add_handler(CommandHandler("start", start))
    app_telegram.add_handler(CommandHandler("getchatid", get_chat_id))  # Добавляем новый обработчик
    app_telegram.run_polling()