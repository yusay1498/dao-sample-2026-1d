front
================================================================================

event-management APIと連携する、イベント管理のフロントエンドアプリケーション。


環境要件
--------------------------------------------------------------------------------

- Node.js 22


技術スタック
--------------------------------------------------------------------------------

- Next.js (App Router) / React / TypeScript
- Tailwind CSS
- [motion](https://motion.dev/)（アニメーション）
- [swr](https://swr.vercel.app/)（データフェッチ・キャッシュ）
- [sonner](https://sonner.emilkowal.ski/)（トースト通知）


起動手順
--------------------------------------------------------------------------------

事前に`event-management`（[../README.md](../README.md)参照）をポート8080で起動しておいてください。

```bash
npm install
npm run dev
```

[http://localhost:3000](http://localhost:3000)でアクセスできます。

- 接続先APIのベースURLは`NEXT_PUBLIC_API_BASE_URL`環境変数で指定します（`.env.local`、既定値は`http://localhost:8080`）。ブラウザから直接APIへアクセスするため、APIサーバー側でCORSの許可が必要です（`event-management`の`app.cors.allowed-origins`で設定済み）。


使い方
--------------------------------------------------------------------------------

- `/events`: イベント一覧（検索・区分での絞り込みが可能）
- `/events/new`: イベントの新規作成
- `/events/{eventId}`: イベント詳細
- `/events/{eventId}/edit`: イベントの編集

会場ID・区分IDはAPI側に一覧取得エンドポイントがないため、フォーム内の候補チップ（`db/02_data.sql`投入済みデータ）を参考に直接入力してください。


その他のコマンド
--------------------------------------------------------------------------------

```bash
npm run build   # 本番ビルド
npm run lint    # ESLint
```

