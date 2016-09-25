# free-task

## これはなに？

free-task は、IO処理に関するトランザクションを表すクラス、`TxnT` を提供するライブラリです。
処理そのものはほとんど提供しない1ファイルだけのものですが、その考え方の提供のためにライブラリ化して公開してあります。
これを利用することによって、安全かつ、（Freeを利用した）強力なトランザクションの合成を実現することができます。

## どう使う？

`example` 以下にサンプルコードが置いてありますので、そちらで利用方法を確認できます。
`example/test` 下に置かれている `UserRepositorySpec` が実際の利用コードですので、
そこから遡ってコードを確認すると分かりやすいでしょう。

```
val w = for {
  id   <- UserRepository.create("findall", age=99)
  user <- UserRepository.find(id)
} yield user.map(e => e.name + ":" + e.age) getOrElse ""
```

この例では、書き込み処理である `UserRepository.create` と、`UserRepository.find` とを合成しています。
ここで注目すべきは、それぞれのメソッドの型が以下のようになっていることです。

```
def find(id: UserId): Txn[MainStore.R, Option[User]] = Find(id)
def create(name: String, age: Int): Txn[MainStore.W, UserId] = Create(name, age)
```

リソースを表す型(以下で説明します)が `MainStore.R`, `MainStore.W` と異なるにも関わらず、
ここで合成が安全に成功していることがポイントです。

上記で登場する `Txn` は、このライブラリが提供する `TxnT` を実際に利用するために作成された型で、
以下のような実装になっています。

```
type EntityIO[A] = FreeC[EntityOp, A]
type Txn[-R, A] = TxnT[EntityIO, R, A]
```

`R` はリソースを示す型、`A` はオペレーションが返す結果の型です。
今回の例では、`R` には `MainStore.R`, `MainStore.W` というものを与えており、
これはごくシンプルな継承関係のみを持つクラスです。

```
object MainStore {
  trait R
  trait W extends R
}
```

この継承関係がある場合にのみ、合成が許容され、より上位のリソースが選択されるようになっています。
今回では、 `UserRepository.create` と`UserRepository.find` の合成の結果、
利用されるリソースは `MainStore.W`, つまり書き込み権限を持つデータベースコネクションになります。 
ここに、もし全く無関係のリソースが与えられた場合は、コンパイルエラーが発生します。
これによって、コードの実行前に自動的に適切なリソースが選択され、
また型安全か否かがコンパイル時に保証されるようになっています。

## 注意点

`TxnT` は `recover`, `recoverWith` メソッドを提供しません。
この理由は、`TxnT` はトランザクションそのものを示すものであり、
データベースへのコミットの失敗に伴う復旧処理はトランザクション内部で行うものではなく、
トランザクションの境界の外側でハンドリングされるべきであるという考えからです。
