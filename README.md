# Passione Mobile

Android-приложение для клиентов ресторана Passione. Позволяет просматривать меню, оформлять заказы и отслеживать их статус.

## Требования

- Android Studio Hedgehog (2023.1.1) или новее
- JDK 8+
- Android SDK 24+ (Android 7.0)
- Эмулятор или физическое устройство

## Открытие проекта

1. Откройте Android Studio
2. File -> Open -> выберите папку `project11b-mobile-passione`
3. Дождитесь синхронизации Gradle

## Запуск

1. Запустите backend на хост-машине (http://localhost:8000)
2. Запустите эмулятор или подключите устройство
3. Нажмите Run в Android Studio

Для эмулятора backend доступен по адресу `10.0.2.2:8000`.

## Сборка APK

```bash
./gradlew assembleDebug
```

APK будет в `app/build/outputs/apk/debug/app-debug.apk`

## Структура проекта

```
app/src/main/java/com/passione/
├── PassioneApp.kt              # Application class
├── MainActivity.kt             # Главная Activity с навигацией
├── data/
│   ├── model/
│   │   └── Models.kt           # Модели данных (Dish, Cart, Order)
│   ├── api/
│   │   ├── ApiService.kt       # Retrofit интерфейс
│   │   └── RetrofitClient.kt   # HTTP клиент
│   └── repository/
│       └── PassioneRepository.kt  # Репозиторий с бизнес-логикой
└── ui/
    ├── MainViewModel.kt        # Общий ViewModel
    ├── menu/
    │   ├── MenuFragment.kt     # Экран меню
    │   └── MenuAdapter.kt      # Адаптер списка блюд
    ├── cart/
    │   ├── CartFragment.kt     # Экран корзины
    │   └── CartAdapter.kt      # Адаптер корзины
    └── order/
        └── OrderFragment.kt    # Экран статуса заказа
```

## Архитектура

- MVVM (Model-View-ViewModel)
- Repository pattern для данных
- Coroutines для асинхронных операций
- LiveData для реактивного UI

## Экраны

### Меню
- Список блюд по категориям
- Pull-to-refresh для обновления
- Кнопка добавления в корзину
- Загрузка изображений через Glide

### Корзина
- Список добавленных блюд
- Изменение количества (+/-)
- Удаление позиций
- Общая сумма и кнопка оформления

### Заказ
- Текущий статус заказа
- Timeline прогресса
- Автообновление каждые 5 секунд
- Кнопка нового заказа после завершения

## Зависимости

```kotlin
// Networking
Retrofit 2.9.0
OkHttp 4.12.0
Gson

// UI
Material Components 1.11.0
Glide 4.16.0
RecyclerView
SwipeRefreshLayout

// Architecture
ViewModel
LiveData
Coroutines
```

## Конфигурация API

Настройки в `RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/api/"
```

Для физического устройства замените `10.0.2.2` на IP-адрес хост-машины в локальной сети.

## Сессии

Сессия клиента создается автоматически при первом запуске. Данные сохраняются в SharedPreferences:
- `session_id` - ID текущей сессии
- `device_id` - уникальный ID устройства

При ошибке 404 (сессия не найдена) автоматически создается новая сессия.

## Разрешения

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

Для HTTP (не HTTPS) трафика включен `android:usesCleartextTraffic="true"`.

## Тестирование на физическом устройстве

1. Подключите устройство к той же сети, что и компьютер с backend
2. Узнайте IP компьютера (например, 192.168.1.100)
3. Измените BASE_URL в RetrofitClient.kt:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.100:8000/api/"
   ```
4. Пересоберите и запустите приложение
