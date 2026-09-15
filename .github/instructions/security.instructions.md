---
applyTo: "**/*.java"
---

# セキュリティ横断レビュー観点

このファイルは言語・レイヤーを問わず横断的に注意すべきセキュリティ観点を扱う。詳細な個別ルールは [spring.instructions.md](./spring.instructions.md) も参照。

## 情報漏洩防止
- エラーレスポンス・例外メッセージにクラス名、スタックトレース、内部実装の詳細を含めていないか
- ログに個人情報（PII）・機密情報（パスワード、トークン）を出力していないか。ネストしたMap構造や`Authentication`引数（JWTクレーム等）からの漏洩経路も考慮しているか
- ユーザー入力をそのままログに出力していないか（ログインジェクション対策）

## 認証・認可
- `@PreAuthorize`をクラス・メソッド両方に設定した場合、両方のSpEL式がAND結合される仕様を理解しているか
- `isAuthenticated()`は匿名ユーザーを除外する。remember-me認証も除外して完全な再認証を要求する場合は`isFullyAuthenticated()`を使っているか
- パスワードは`BCryptPasswordEncoder`等で適切にハッシュ化されているか

## 依存関係
- 依存ライブラリに既知の脆弱性がないか
- 保守が停止しているGitHub Actions・OSSライブラリを利用していないか

## Web
- CSRF/CORS設定が必要最小限か（API専用構成での無効化には根拠があるか）
- SQLインジェクション・XSSの攻撃ベクトルがないか

## 参考
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
