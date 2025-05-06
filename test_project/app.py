from flask import Flask, request, jsonify
import logging

logging.basicConfig(level=logging.DEBUG)
log = logging.getLogger(__name__)

app = Flask(__name__)

@app.route('/log-anomaly', methods=['POST'])
def log_anomaly():
    try:
        data = request.get_json()
        log.info("Parsed JSON: %s", data)  # Вывод распарсенного JSON
    except Exception as e:
        log.info("Error parsing JSON: %s", str(e))
        return {"status": "bad"}, 500

    return {"status": "success"}, 200  # Возвращаем успешный ответ

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)  # Запускаем сервер на порту 5000