#!/bin/bash
set -e  # إيقاف السكربت عند أول خطأ

ENV=$1
echo "🚀 Starting deployment to $ENV environment"

# نحفظ ID الحاوية القديمة إذا كانت موجودة
OLD_CONTAINER_ID=$(docker ps -q -f name=my-app-container || true)

# إذا فيه حاوية قديمة شغالة، نوقفها ونحذفها
if [ -n "$OLD_CONTAINER_ID" ]; then
  echo "🛑 Stopping and removing old container $OLD_CONTAINER_ID"
  docker stop my-app-container
  docker rm my-app-container
fi

# تشغيل الحاوية الجديدة بالصورة الجديدة
docker run -d --name my-app-container \
  -e ENV="$ENV" \
  -e MAIL_USERNAME="$MAIL_USERNAME" \
  -e MAIL_PASSWORD="$MAIL_PASSWORD" \
  -e MAIL_HOST="$MAIL_HOST" \
  -e MAIL_PORT="$MAIL_PORT" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  my-app:latest

echo "✅ Deployment completed successfully to $ENV"
