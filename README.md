event-management
================================================================================

イベント管理API


環境要件
--------------------------------------------------------------------------------

- Java 25
- Docker


技術スタック
--------------------------------------------------------------------------------

- APIサーバー: Java, Spring Boot, Spring Data JDBC (JdbcClient)


ビルド手順
--------------------------------------------------------------------------------

```bash
cd ./event-management/
./mvnw clean package
```


開発向け起動手順
--------------------------------------------------------------------------------

［手順 1］［手順 2］は、必ず順番通りに実行してください。

手順 1. DBを起動

```bash
docker run --rm -d --name event-management-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:latest
```

手順 2. スキーマ・初期データを投入

```bash
docker exec -i event-management-postgres psql -U postgres < ../db/01_schema.sql
docker exec -i event-management-postgres psql -U postgres < ../db/02_data.sql
```

手順 3. APIサーバーを起動

```bash
./mvnw clean spring-boot:run \
-Dspring-boot.run.arguments="
  --spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
  --spring.datasource.username=postgres
  --spring.datasource.password=postgres
"
```


［デモ］APIの使い方
--------------------------------------------------------------------------------

`db/02_data.sql`に投入済みのデータ（会場「星降るドーム」・区分「ライブ」・イベント「星屑ミッドナイトパレード」）を例に使用します。

- イベント一覧を取得

```bash
curl -v localhost:8080/events
```

- イベントを1件取得

```bash
curl -v localhost:8080/events/9b5c3e30-3ca1-4d03-8e33-000000001001
```

- イベントを新規作成

```bash
curl -v -X POST localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{
    "venueId": "7f3a1c20-1a8e-4b01-9c11-000000000001",
    "eventCategoryId": "8a4b2d10-2b9f-4c02-8d22-000000000101",
    "eventName": "サンプルライブ",
    "performer": "サンプルアーティスト",
    "description": "説明文",
    "startTime": "2026-12-01T18:00:00+09:00",
    "endTime": "2026-12-01T21:00:00+09:00",
    "availableSeats": 100,
    "reservedSeats": 0
  }'
```

- イベントを全更新（`PUT`）

```bash
curl -v -X PUT localhost:8080/events/9b5c3e30-3ca1-4d03-8e33-000000001001 \
  -H 'Content-Type: application/json' \
  -d '{
    "venueId": "7f3a1c20-1a8e-4b01-9c11-000000000001",
    "eventCategoryId": "8a4b2d10-2b9f-4c02-8d22-000000000101",
    "eventName": "星屑ミッドナイトパレード（改訂版）",
    "performer": "Luna Caravan",
    "description": "きらめく夜空を駆け抜けるポップライブ。",
    "startTime": "2026-10-03T18:30:00+09:00",
    "endTime": "2026-10-03T21:00:00+09:00",
    "availableSeats": 3000,
    "reservedSeats": 0
  }'
```

- イベントを一部更新（`PATCH`、[JSON Merge Patch](https://www.rfc-editor.org/rfc/rfc7396)）

```bash
curl -v -X PATCH localhost:8080/events/9b5c3e30-3ca1-4d03-8e33-000000001001 \
  -H 'Content-Type: application/merge-patch+json' \
  -d '{
    "eventName": "星屑ミッドナイトパレード（一部更新）"
  }'
```

- イベントを削除

```bash
curl -v -X DELETE localhost:8080/events/9b5c3e30-3ca1-4d03-8e33-000000001001
```

エラー時のレスポンスは[RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457.html)（`application/problem+json`）に準拠します。
