FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# نسخ ملفات pom.xml و checkstyle.xml من جذر المشروع
COPY pom.xml .
COPY checkstyle.xml .

# تنزيل الاعتمادات بدون بناء المشروع
RUN mvn dependency:go-offline

# نسخ كامل محتوى مجلد src
COPY src ./src

# بناء المشروع بدون اختباراته
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

# نسخ الـ jar المنتج من مرحلة البناء
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
