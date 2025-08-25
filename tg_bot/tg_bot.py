import logging
from telegram import Update
from telegram.ext import ApplicationBuilder, CommandHandler, ContextTypes
import signal
import sys

from telegram_service import TelegramService
from config import TELEGRAM_TOKEN

logger = logging.getLogger(__name__)


class TelegramBot:
    def __init__(self):
        self.telegram_service = TelegramService()
        self.application = ApplicationBuilder().token(TELEGRAM_TOKEN).build()
        print("token: ", TELEGRAM_TOKEN)
        self._setup_handlers()

    def _setup_handlers(self):
        """Настройка обработчиков команд"""
        self.application.add_handler(CommandHandler("start", self.start_command))
        self.application.add_handler(CommandHandler("help", self.help_command))
        self.application.add_handler(CommandHandler("getchatid", self.get_chat_id_command))
        self.application.add_handler(CommandHandler("status", self.status_command))

    async def start_command(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """Обработчик команды /start"""
        try:
            chat_id = str(update.effective_chat.id)
            self.telegram_service.add_chat_id(chat_id)

            welcome_message = (
                "🤖 <b>Добро пожаловать в бота уведомлений о торговых аномалиях!</b>\n\n"
                "Я буду присылать вам уведомления об аномальных объемах торгов.\n\n"
                "📋 <b>Доступные команды:</b>\n"
                "/start - Запуск бота и подписка на уведомления\n"
                "/help - Справка\n"
                "/getchatid - Получить ID чата\n"
                "/status - Статус подписки\n\n"
                "✅ Вы успешно подписались на уведомления!"
            )

            await update.message.reply_text(welcome_message, parse_mode='HTML')
            logger.info(f"Новый пользователь подписался: {chat_id}")

        except Exception as e:
            logger.error(f"Ошибка в команде /start: {e}")
            await update.message.reply_text(
                "❌ Произошла ошибка при обработке команды. Попробуйте позже."
            )

    async def help_command(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """Обработчик команды /help"""
        help_text = (
            "🤖 <b>Бот уведомлений о торговых аномалиях</b>\n\n"
            "📋 <b>Доступные команды:</b>\n"
            "/start - Запуск бота и подписка на уведомления\n"
            "/help - Показать эту справку\n"
            "/getchatid - Получить ID текущего чата\n"
            "/status - Проверить статус подписки\n\n"
            "ℹ️ <b>Что делает бот:</b>\n"
            "• Получает данные о торговых аномалиях\n"
            "• Создает графики свечей за последний час\n"
            "• Отправляет уведомления с аналитикой\n\n"
            "📊 <b>Информация в уведомлениях:</b>\n"
            "• Название актива и тикер\n"
            "• Текущая цена и объем\n"
            "• Изменения за день и минуту\n"
            "• График свечей за час\n"
        )
        await update.message.reply_text(help_text, parse_mode='HTML')

    async def get_chat_id_command(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """Обработчик команды /getchatid"""
        chat_id = update.effective_chat.id
        chat_type = update.effective_chat.type

        message = (
            f"🆔 <b>Информация о чате:</b>\n"
            f"ID чата: <code>{chat_id}</code>\n"
            f"Тип чата: {chat_type}\n\n"
            f"💡 Скопируйте ID для использования в настройках."
        )

        await update.message.reply_text(message, parse_mode='HTML')
        logger.info(f"Запрошен chat_id: {chat_id} (тип: {chat_type})")

    async def status_command(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """Обработчик команды /status"""
        chat_id = str(update.effective_chat.id)
        chat_ids = self.telegram_service.load_chat_ids()

        if chat_id in chat_ids:
            status_message = (
                "✅ <b>Статус подписки: Активна</b>\n\n"
                f"🆔 Ваш Chat ID: <code>{chat_id}</code>\n"
                f"📊 Всего подписчиков: {len(chat_ids)}\n"
                "🔔 Вы будете получать уведомления об аномалиях"
            )
        else:
            status_message = (
                "❌ <b>Статус подписки: Неактивна</b>\n\n"
                "Используйте команду /start для подписки на уведомления"
            )

        await update.message.reply_text(status_message, parse_mode='HTML')

    async def error_handler(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """Обработчик ошибок"""
        logger.error(f"Ошибка в боте: {context.error}")
        if update and update.message:
            await update.message.reply_text(
                "❌ Произошла ошибка при обработке вашего запроса. "
                "Пожалуйста, попробуйте позже."
            )

    def run(self):
        """Запуск бота"""
        logger.info("Запуск Telegram бота...")

        # Добавляем обработчик ошибок
        self.application.add_error_handler(self.error_handler)

        # Запускаем polling
        self.application.run_polling(
            drop_pending_updates=True,
            stop_signals=[signal.SIGINT, signal.SIGTERM]
        )


def signal_handler(signum, frame):
    """Обработчик сигналов для graceful shutdown"""
    logger.info(f"Получен сигнал {signum}, завершаем работу бота...")
    sys.exit(0)


if __name__ == "__main__":
    # Настройка логирования
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.StreamHandler(),
            logging.FileHandler('telegram_bot.log')
        ]
    )

    # Регистрация обработчиков сигналов
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    # Запуск бота
    bot = TelegramBot()
    try:
        bot.run()
    except Exception as e:
        logger.error(f"Фатальная ошибка в боте: {e}")
        sys.exit(1)