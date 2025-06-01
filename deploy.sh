#!/bin/bash
set -e

ENV=$1
echo "Starting deployment to $ENV environment"

# طباعة القيم المستخدمة (أو استخدامها في الأوامر)
echo "Using mail username: $MAIL_USERNAME"
echo "Using DB user: $DB_USERNAME"

# مثال لنشر Docker container مع تمرير المتغيرات
docker run -d \
  -e MAIL_USERNAME=$MAIL_USERNAME \
  -e MAIL_PASSWORD=$MAIL_PASSWORD \
  -e MAIL_HOST=$MAIL_HOST \
  -e MAIL_PORT=$MAIL_PORT \
  -e DB_USERNAME=$DB_USERNAME \
  -e DB_PASSWORD=$DB_PASSWORD \
  my-app:latest
