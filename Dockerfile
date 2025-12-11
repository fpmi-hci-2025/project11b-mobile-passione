FROM circleci/android:api-33

WORKDIR /app

USER root

RUN apt-get update && apt-get install -y \
    git \
    && rm -rf /var/lib/apt/lists/*

COPY . .

RUN chmod +x ./gradlew

CMD ["./gradlew", "assembleDebug", "test"]

