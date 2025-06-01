#!/bin/bash
set -e

ENV=$1
echo "⚠️ Deployment failed! Starting rollback in $ENV environment"

# إيقاف وحذف الحاوية الفاشلة لو كانت شغالة
docker stop my-app-container || true
docker rm my-app-container || true

# تشغيل الحاوية السابقة (my-app:previous)
docker run -d --name my-app-container \
  -e ENV=$ENV \
  -e MAIL_USERNAME="$MAIL_USERNAME" \
  -e MAIL_PASSWORD="$MAIL_PASSWORD" \
  -e MAIL_HOST="$MAIL_HOST" \
  -e MAIL_PORT="$MAIL_PORT" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  my-app:previous

echo "🌀 Rollback completed to previous version on $ENV"
