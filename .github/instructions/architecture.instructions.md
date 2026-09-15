---
applyTo: "**/*.java"
---

# アーキテクチャ/レイヤー構成レビュー観点

レイヤードアーキテクチャに関する指摘を整理する。

## 4層構成と依存方向
- Presentation（Controller/Scheduler）→ Application（ユースケース、トランザクション境界）→ Domain（Entity/ValueObject/DomainService/Repositoryインターフェース）← Infrastructure（Repository実装、外部API呼び出し）の依存方向を守っているか
- Domain層が他層に依存していないか（Repositoryインターフェースはdomainに定義し、実装はinfrastructureに置く。依存性逆転の原則）
- Infrastructure層のクラスがDomain層のクラスを継承する「逆継承」で層をまたいでいないか
- Application層からInfrastructure層への直接依存がないか
- Presentation層からDomain層への直接依存は一律禁止ではなく、DTOを挟むかどうかの判断とセットで都度妥当性を検討しているか

## ドメイン層の独立性
- Domain層のクラス名・パッケージ名に技術固有の名前（JPA等）が含まれていないか（その技術自体を開発する場合を除く）
- Repositoryのメソッド名・戻り値型にテーブル名やデータストアの実装詳細を想起させる語（`AttendanceRecord`等）が使われていないか
- インフラ層固有の関心事（JPAの`Projection`等）をドメイン層に配置していないか

## Application層
- ApplicationServiceは薄く保ち、複数のDomainService/Entityを調整する役割に徹しているか
- メソッド粒度を細分化しすぎず、処理の流れが把握しやすい状態を保っているか
- トランザクション境界はApplicationServiceに設定されているか（Repository層側で例外をスローする設計になっていないか）
- バリデーションの置き場所は「どのユースケースでも常に成り立つルール＝Domain層」「ユースケース固有の条件＝Application層」「単純なフォーマットチェック＝Presentation層」で切り分けられているか
- 複数のsave処理にまたがる整合性（例: 残高更新と履歴保存）は、トランザクション境界を意識して一体化されているか
- クラス名に`Application`を含めて役割を明示しているか（`XxxService`→`XxxApplicationService`）

## Repositoryパターン
- Repositoryは「エンティティに対する永続化操作」に限定されているか。業務ルール（重複禁止、更新可否の判定等）がRepositoryに滲み出していないか
- 外部API呼び出しやメール送信等、エンティティに直接紐づかない操作は、Repositoryでなく`port`/`Operations`/`Template`等の専用インターフェースに切り出しているか
- Repositoryの戻り値（`Optional`等）に応じた業務ハンドリングは、Application層の責務にしているか（Presentation層やRepository自身に持たせていないか）

## 横断的関心事（AOP・キャッシュ）
- ロギング等のAOP実装は`aop`/`aspect`のような専用パッケージに切り出し、特定層に紐づけていないか
- キャッシュ等、ユースケース図に現れない横断的関心事に安易に`Service`サフィックスを付けていないか（実態に即した命名、`@Component`等を検討する）

## 参考
- [PresentationDomainDataLayering (Martin Fowler's Bliki)](https://martinfowler.com/bliki/PresentationDomainDataLayering.html)
- [Dependency inversion principle (Wikipedia)](https://en.wikipedia.org/wiki/Dependency_inversion_principle)

上記に判断基準が明記されていない場合は、断定を避けて「要確認」と明記した上でレビューコメントを記述すること。
