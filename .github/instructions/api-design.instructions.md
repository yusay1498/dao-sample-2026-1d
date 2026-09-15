---
applyTo: "**/*Controller.java,**/*RestController.java"
---

# REST API設計 レビュー観点

## リソース設計（URI）
- URIにはリソース名（名詞・複数形）を設定し、システム自明語（`App`等）や抽出条件を表す語（`all`/`search`）を含めていないか
- `/dailySalesSummaries/all`のような抽出条件をリソース名に含める設計を避け、`GET /リソース名`で全件・検索を表現しているか
- サブリソースは階層構造で表現し、他リソースとの整合を取っているか（例: `/kintai/{employeeId}/today`）
- 複数の異なるエンティティを同一URIで扱っていないか
- PathVariableはエンティティIDのみに使用し、検索条件はRequestParamで表現しているか
- RequestParamの引数には明示的に名前を付けているか（ビルド時に引数名情報が失われる場合の対策）

## HTTPステータス・レスポンス設計
- リソース作成時（POST）は`201 Created`+`Location`ヘッダ、更新時は`200 OK`を返しているか
- 二重登録エラーには`409 Conflict`を活用しているか
- 「正しい条件で検索した0件」は404でなく空のリスト（`Page.empty()`）で表現し、「不正な条件・存在しないリソース」との違いを区別しているか（1件リソース取得の場合は404が自然）
- ページネーションはSpring標準の`Pageable`/`Page`を利用し、独自の`meta`フィールドを作っていないか
- 戻り値の型に`ResponseEntity`を使い、Locationヘッダ等を柔軟に扱えるようにしているか

## エラーレスポンス
- エラーレスポンスは[RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457.html)に準拠しているか
- `detail`フィールドに具体的な不正内容を記載し、フロントのリトライ・表示に活用できる情報を含めているか
- クライアントへのレスポンスにクラス名やスタックトレース等の内部実装情報を含めていないか

## CORS
- CORSの許可メソッドは、現在cross-originクライアントが使用するものだけに限定しているか
- PreflightリクエストはCORS filterまたはsecurity chainで正しく処理され、要求された実メソッドに必要な`Access-Control-Allow-Methods`を返しているか

## 参考
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)
- [Problem Details for HTTP APIs (RFC 9457)](https://www.rfc-editor.org/rfc/rfc9457.html)
