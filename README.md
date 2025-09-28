# ShareIt — сервис шеринга вещей

**Коротко:** ShareIt — это учебный проект по созданию сервиса, где пользователи могут делиться вещами, бронировать их, оставлять отзывы и создавать запросы на новые вещи. Проект покрыт тестами, разделён на модули (gateway/server) и использует PostgreSQL + Spring Boot + JPA.

---

## Почему стоит посмотреть этот проект (для рекрутера)

* Проект показывает практическую реализацию полного бэкенда: REST API, бизнес-слой, хранение в БД (JPA/Hibernate), схему БД `schema.sql`.
* Есть архитектурное решение: **многомодульный проект** (shareIt-gateway и shareIt-server), что демонстрирует понимание модульных подходов и интеграции между модулями через REST.
* Реализовано покрытие тестами: unit (MockMVC), интеграционные тесты с базой, JSON-тесты для DTO — навыки написания тестов параллельно с функциональностью.
* Проект упакован так, чтобы его было легко запустить локально и проверить через Postman-коллекцию.

---

## Что реализовано (по sprint'ам)

**Sprint 1 — каркас и контроллеры**

* Фичевая структура пакетов: `item`, `booking`, `request`, `user`.
* REST-контроллеры для работы с пользователями (`UserController`) и вещами (`ItemController`).
* DTO-слой и мапперы для `User` и `Item`.
* Хранилище in-memory (DAO) для быстрого старта и тестирования.

**Sprint 2 — база данных и бронирования**

* Добавлены зависимости JPA и PostgreSQL.
* `schema.sql` с `CREATE TABLE IF NOT EXISTS` для безопасного многократного старта.
* Сущности JPA: `User`, `Item`, `Booking` (+ `Comment`).
* Репозитории Spring Data JPA для `User`, `Item`, `Booking`, `Comment`.
* Контроллер `BookingController` и логика подтверждения/отклонения брони.
* При выводе вещей владельцу — поле с датами последнего и ближайшего бронирования.
* Добавлены проверки: комментарий можно оставить только если пользователь действительно арендовал вещь и период аренды завершился.

**Sprint 3 — Item Requests и Gateway**

* Реализованы request'ы: `ItemRequest` и эндпоинты

  * `POST /requests` — создать запрос
  * `GET /requests` — свои запросы
  * `GET /requests/all` — все запросы других пользователей
  * `GET /requests/{requestId}` — конкретный запрос
* При создании `Item` можно указать `requestId` (ответ на запрос).
* Разделение на два приложения в одном репозитории: `shareIt-gateway` (валидация, контроллеры пользователей, BaseClient) и `shareIt-server` (core, BLL, репозитории). Gateway использует REST-клиент к серверу.
* Порт по умолчанию: gateway `8080`, server `9090`.

---

## Основные сущности (коротко)

* **User**: `id`, `name`, `email`.
* **Item**: `id`, `name`, `description`, `available`(boolean), `owner`(User), `requestId`(nullable).
* **Booking**: `id`, `start`, `end`, `item`(Item), `booker`(User), `status`(WAITING/APPROVED/REJECTED/CANCELED).
* **ItemRequest**: `id`, `description`, `requester`(User), `created`(timestamp).
* **Comment**: `id`, `text`, `item`(Item), `author`(User), `created`.

(Полный список DTO/поля — в коде проекта.)

---

## REST API — краткая выжимка (ключевые эндпоинты)

> Все запросы, связанные с пользователем, требуют заголовка `X-Sharer-User-Id: {userId}` (идентификация).

* `POST /users` — создать пользователя.

* `GET /users/{id}` — получить пользователя.

* `PATCH /users/{id}` — обновить пользователя.

* `POST /items` — добавить вещь (в теле `ItemDto`, опционально `requestId`).

* `PATCH /items/{itemId}` — редактировать (только владелец).

* `GET /items/{itemId}` — получить инфо о вещи (включая комментарии и, если владелец, даты last/next booking).

* `GET /items` — получить список вещей текущего владельца.

* `GET /items/search?text={text}` — поиск доступных вещей.

* `POST /items/{itemId}/comment` — добавить отзыв (только после завершённой аренды).

* `POST /bookings` — создать запрос на бронирование (статус WAITING).

* `PATCH /bookings/{bookingId}?approved={true|false}` — подтвердить/отклонить (только владелец).

* `GET /bookings/{bookingId}` — посмотреть бронирование (владелец или автор).

* `GET /bookings?state={state}` — список бронирований текущего пользователя.

* `GET /bookings/owner?state={state}` — бронирования для вещей текущего владельца.

* `POST /requests` — создать запрос на вещь.

* `GET /requests` — свои запросы + ответы (вещи в ответах: id, name, ownerId).

* `GET /requests/all` — чужие запросы.

* `GET /requests/{requestId}` — один запрос с ответами.

---

## Технологии

* Java 17
* Spring Boot (Web, Data JPA)
* PostgreSQL
* Maven (multi-module)
* JUnit + Mockito + MockMVC для тестов
* Lombok, MapStruct (для мапперов) — опционально
* Postman коллекция для ручного тестирования

---

## Как запустить проект локально

**Требования:** Java 17, Maven, PostgreSQL (локально или в Docker).

1. Настройте БД (пример `.env`/`application.yml`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shareit
spring.datasource.username=shareit
spring.datasource.password=shareit
spring.jpa.hibernate.ddl-auto=none
```

2. Создайте базу и пользователя в Postgres (или используйте docker-compose).

3. Сборка всего репозитория:

```bash
mvn clean install
```

4. Запуск модулей:

* Запустите `shareIt-server` (порт 9090):

```bash
mvn -pl shareIt-server spring-boot:run
```

* Запустите `shareIt-gateway` (порт 8080):

```bash
mvn -pl shareIt-gateway spring-boot:run
```

5. Используйте Postman коллекцию (в репозитории) для тестирования эндпоинтов. В коллекции оставлены переменные окружения для портов и header `X-Sharer-User-Id`.

---

## Тестирование

* Unit + MockMVC:

```bash
mvn -DskipTests=false test
```

* Интеграционные тесты используют реальную БД (может быть поднята в Docker при тестах).

В репозитории есть примеры тестов:

* MockMVC тесты контроллеров (с моками сервисов).
* Интеграционные тесты для сервисов (с in-memory/поднятой БД).
* `@JsonTest` для DTO.

---

## Архитектурные решения и пояснения (для тех, кто оценивает код)

* **Feature package layout** — каждое доменное поведение (item, booking, request, user) в своём пакете, что облегчает масштабирование и навигацию.
* **DTO + Mapper** — отделение модели БД от внешнего API; мапперы (MapStruct/ручные) повышают читаемость и тестируемость.
* **Gateway / Server split** — демонстрация навыка разделения ответственности: gateway — валидация и оптимизации (кеш), server — бизнес-логика и БД.
* **Валидация** — вынесена в gateway: проверка форматов, размеров полей, обязательных значений (без прямого обращения к БД).
* **Idempotency / защитa от дублей** — частично реализована на уровне gateway (кеш / короткий TTL) и на уровне сервисов — бизнес-валидации.

---

Спасибо за внимание! Этот README — краткая навигация по проекту и по мышлению автора. Если нужно, могу подготовить краткий пояс для интервьюера: список задач/PR, которые демонстрируют ключевые навыки, или экспорт тест-кейсов из Postman.
