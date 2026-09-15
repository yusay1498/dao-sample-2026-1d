---
applyTo: "**/*.java"
---

# Spring Boot固有 レビュー観点

## 設定管理
- 環境変数から値を受け取る設定は`@Value`でなく`@ConfigurationProperties`を使っているか（`@Value`はRelaxed Bindingが効かず、List等の複数値受け取りにも弱い）
- ローカル専用の設定は本番にパッケージングされる`application.yml`に含めず、`compose.yml`の`environment`やREADMEの起動手順側に分離しているか

## DI/コンポーネント設計
- コンストラクタインジェクションを使用しているか（`@Autowired`フィールドインジェクション、テストでの`@InjectMocks`を避ける）
- 複数の同型Beanを`@Primary`で単純に優先度づけていないか（優劣のない複数Beanには合成アノテーション等での明示的な使い分けを検討）

## トランザクション管理
- `@Transactional`はApplication層に設定されているか
- 同一クラス内の別メソッド呼び出しでは、Spring AOPのプロキシを経由せず`@Transactional`が効かない「自己呼び出し問題」に注意しているか（別Beanに切り出す必要がある）
- ループ内で外部システムへの送信とDB更新を行う場合、トランザクション境界の粒度（全体一括か1件ごとか）を誤ると、送信済み処理の重複実行につながらないか

## Repository設計
- `save`は新規・更新の両方を1メソッドで扱い、保存後のエンティティを返しているか（自動採番IDを呼び出し元に伝えるため）
- `save`後の戻り値は、引数をそのまま返すのでなく、実際に永続化された結果（`findById`の再取得等）を返しているか
- `findById`は`Optional`で返しているか
- バッチ更新（`batchUpdate`）の戻り値（更新件数）を検証し、想定件数と一致するか確認しているか

## エラーハンドリング
- `@RestControllerAdvice`でグローバルな例外ハンドリングを行っているか
- エラーレスポンスは[RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457.html)に準拠し、Springの`ProblemDetail`を活用しているか
- `@ExceptionHandler`メソッド名は例外クラス名でなくレスポンス内容に基づいた命名にしているか

## セキュリティ
- `@PreAuthorize`をクラス・メソッド両方に設定した場合、両方のSpEL式が評価されAND結合される点を理解しているか（クラス→メソッドの順に早期リターンされるわけではない）
- `isAuthenticated()`は匿名ユーザーを除外する。remember-me認証も除外して完全な再認証を要求する場合は`isFullyAuthenticated()`を使っているか

## 参考
- [Externalized Configuration (Spring Boot Reference)](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Problem Details for HTTP APIs (RFC 9457)](https://www.rfc-editor.org/rfc/rfc9457.html)
- [Spring Security - Authorization Architecture](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html)
