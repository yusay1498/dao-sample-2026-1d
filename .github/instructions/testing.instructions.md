---
applyTo: "**/*Test.java,**/*Tests.java"
---

# テスト設計 レビュー観点

## テスト名・DisplayName
- テストメソッド名/`@DisplayName`はGiven（前提条件）/When（操作）/Then（期待結果）の観点を具体的に表現しているか
- 「正しい」「正常」「異常」「期待通り」といった曖昧な表現を避け、具体的な状態・結果を説明しているか
- 疑問形でなく言い切りの形で期待結果を表現しているか

## モックの扱い
- コンストラクタインジェクションが可能な場合、`@InjectMocks`は避けモックをテスト内部で明示的に用意しているか
- 通常のモックでは`when().thenReturn()`を使い、spyの実メソッド呼び出しを避ける必要がある場合などに`doReturn().when()`を使い分けているか
- モック呼び出しは、対象の振る舞いを保証するために重要なinteractionに絞って`verify`しているか
- `ArgumentCaptor`は本当に必要か（引数マッチャー`Mockito.argThat`で代替できないか）

## 永続化テストの妥当性
- `save`の戻り値検証だけでなく、DBに実際に永続化されていることを`JdbcClient`等で直接確認しているか（テスト対象クラス自身の参照メソッドで検証すると、保存をスキップしていても通ってしまう）
- テストデータは`schema.sql`でなく`data.sql`かテストコード側（`@Sql`）に用意しているか

## テスト構造・可読性
- Arrange-Act-Assert（Given-When-Then）パターンに従っているか。インスタンス生成はGiven、メソッド呼び出し時のパラメータはWhenに属するという整理ができているか
- マジックナンバーは変数化し意味の分かる名前を付けているか
- 過剰なインラインコメント・冗長な検証行を削除しているか

## 網羅性
- 正常系だけでなく異常系（データなし、境界値、未来日付等）のテストも用意されているか
- ページネーションは10件超のデータや複数ページにまたがるケースを検証しているか

## 非同期・結合テスト
- 非同期処理のテストは固定時間待機でなく`CountDownLatch`等で実際の処理完了を待っているか
- `@SpringBootTest`の濫用を避け、目的に応じて`@WebMvcTest`（単体）や`@DataJdbcTest`（Repository）を使い分けているか

## 参考
- [Mockito Javadoc (best practices)](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
