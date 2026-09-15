---
applyTo: "**/Dockerfile,**/compose*.yml,**/docker-compose*.yml,**/compose*.yaml,**/docker-compose*.yaml"
---

# Docker / docker-compose レビュー観点

## Dockerfile
- マルチステージビルドを活用しているか。ステージの区切りをコメントで視覚的に明示しているか
- `ARG`と`ENV`に同名を使って混乱を招いていないか
- `pom.xml`等に依存関係が定義済みなら、Dockerfile内で重複した依存ダウンロードを行っていないか
- root ユーザーで実行していないか
- Dockerイメージは本番同様に扱われるため、開発環境固有の名称を含めていないか

## docker-compose
- サービス間の依存関係（`depends_on`）はヘルスチェック条件と組み合わせているか（DBの起動を待つ等）
- ヘルスチェックコマンドは対象サービスに適したものか（例: PostgreSQLは`pg_isready -d <DB名>`）
- ローカル専用の設定には`# local only`等のコメントを明記しているか
- コンテナ名の別名設定は紛らわしくないか、不要なら省略しているか

## セキュリティ
- 環境変数に機密情報を直書きしていないか（`.env`ファイルやシークレット管理を利用しているか）
- 機密情報（パスワード、キー）がイメージに含まれていないか
