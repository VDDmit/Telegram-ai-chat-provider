#!/bin/sh

# Выходим, если любая команда завершится с ошибкой
set -e

DOMAIN="ai-chat-prov.duckdns.org"
EMAIL="danvoropaeff@yandex.ru"
DATA_PATH="./certbot/conf"
WEBROOT_PATH="./certbot/www"

echo "### Проверка сертификата для $DOMAIN..."

if [ -d "$DATA_PATH/live/$DOMAIN" ]; then
  echo "### Сертификат уже существует. Пропускаем."
  exit 0
fi

echo "### Создание временных папок для получения сертификата..."
mkdir -p "$WEBROOT_PATH"
mkdir -p "$DATA_PATH"

echo "### Запрос на получение сертификата для $DOMAIN..."

# Запускаем временный Nginx ТОЛЬКО для прохождения проверки
docker compose run --rm --entrypoint "\
  nginx -g 'daemon off;'" nginx &

# Ждем, пока временный Nginx запустится
sleep 5

# Запускаем Certbot для получения сертификата
docker compose run --rm --entrypoint "\
  certbot certonly --webroot -w /var/www/certbot \
    --non-interactive \
    --agree-tos \
    -m $EMAIL \
    -d $DOMAIN" certbot

docker compose down