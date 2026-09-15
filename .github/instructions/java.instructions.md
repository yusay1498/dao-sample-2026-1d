---
applyTo: "**/*.java"
---

# Java実装パターン レビュー観点

## Optional
- `Optional#get()`は使用していないか（Null安全が損なわれる）。`orElseThrow`/`ifPresentOrElse`/`map`/`flatMap`を使っているか
- `findById`のように呼び出し元で存在保証済みの場合、不要に`Optional`でラップし直していないか
- 保存系メソッド（`save`）は成功時に必ずエンティティを返す契約のため、戻り値を`Optional`で包んでいないか

## record/不変性
- 値を保持するだけのデータクラスは`record`型で表現しているか
- 再代入されない変数・フィールドには`final`を付けているか
- マジックナンバー/マジックストリングを`record`型や定数で意味づけしているか

## Clock DI（テスト容易性）
- `LocalDateTime.now()`を直接使わず、`Clock`を引数/DIで受け取り`LocalDateTime.now(clock)`としているか（テスト時に時刻を固定できるようにするため）
- 時刻の解決は呼び出し元1箇所で行い、下位メソッドには解決済みの値を渡しているか（複数箇所で取得することによるズレを防止）

## 型選択
- 古い`Date`型でなく`java.time`パッケージ（`LocalDate`/`LocalDateTime`/`YearMonth`）を意図に応じて使い分けているか
- 変数名にも時間的な意味を含めているか（`LocalDateTime`を使うのに日付のみを表す名前になっていないか）
- IDはDB互換性・ポータビリティの観点で`String`（UUID文字列）またはDB自動採番のいずれかに統一されているか

## 乱数生成
- `Random`でなく`SecureRandom`、変数宣言は`RandomGenerator`インターフェース（Java 17+）を使っているか
- 桁数分の乱数を一度に取得しているか（1桁ずつ取得すると分散に不利）

## Stream API
- Streamは「1つの入力→1つの出力」の単純な変換に留めているか（中間操作で外部変数を書き換えていないか）
- 単にインデックスを得るためだけの`IntStream.range`より、通常の`for`ループの方が読みやすい場合がないか
- 複数の入力（i, answer, userInput等）にまたがるループ処理は、Streamでなく`for`ループの方が適切な場合がある
- `Collectors.groupingBy`等で宣言的に書ける手続き的な集計処理を、手動でのMap構築で代替していないか

## 例外処理
- 例外の再スロー時は原因例外（`Throwable`）を保持しているか（`new XxxException(msg, e)`）
- 例外メッセージ・クラス名等の内部実装情報をクライアントへのレスポンスに含めていないか
- 汎用的すぎる例外（`IllegalArgumentException`等）でなく、意味の伝わる専用例外クラスを用意しているか
- 空の`catch`ブロックで例外を握りつぶしていないか

## リソース管理
- `Scanner`等のリソースはループ内で毎回`new`せず使い回しているか、または`try-with-resources`で管理しているか

## アクセス修飾子
- アクセス修飾子未指定（package private）が意図した可視性か確認しているか
