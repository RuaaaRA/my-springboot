#!/bin/bash
set -e

ENV=$1
echo "Deployment failed! Starting rollback in $ENV environment"

# إيقاف وحذف الحاوية الفاشلة لو شغالة
docker stop my-app-container || true
docker rm my-app-container || true

# تشغيل الحاوية السابقة (تأكد أنك تبني الصورة السابقة بالوسم my-app:previous)
docker run -d --name my-app-container -e ENV=$ENV my-app:previous

echo "Rollback completed."
