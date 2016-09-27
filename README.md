# free-task

日本語訳は下の方にあります。

## What is free-task?

free-task is a library which provides `TxnT` class and `TxnT` is a class which handles transactions.
This library includes only 1 file and provide a few APIs, but it is published as a library to share the concept and methodology.
With free-task, you can achieve type-safe and powerful transaction composition (using Free Monad).

## How to use

All examples are placed under `example` directory and you can learn the usage.
`UserRepositorySpec` under `example/test` directory uses `TxnT` directly, so it may be a good entry point to learn.

```scala
val w = for {
  id   <- UserRepository.create("findall", age=99)
  user <- UserRepository.find(id)
} yield user.map(e => e.name + ":" + e.age) getOrElse ""
```

In the above example, a write operation `UserRepository.create` and `UserRepository.find` are composed.
The notable point here is that the signature of each method is the following:

```scala
def find(id: UserId): Txn[MainStore.R, Option[User]] = Find(id)
def create(name: String, age: Int): Txn[MainStore.W, UserId] = Create(name, age)
```

The key point is that the composition of `Txn[MainStore.R, Option[User]]` and `Txn[MainStore.W, UserId]` succeeds safely
even though the type of `MainStore.R` (represents a resource) differs from that of `MainStore.W`.

`Txn` type in the above code snippet is just an alias type for using `TxnT` and defined as the following:

```scala
type EntityIO[A] = FreeC[EntityOp, A]
type Txn[-R, A] = TxnT[EntityIO, R, A]
```

Type `R` means a resource and type `A` means the result type of an operation.
In the above example, `MainStore.R` and `MainStore.W` are passed to `R`.
These types are constructed with simple hierarchical inheritance.

```scala
object MainStore {
  trait R
  trait W extends R
}
```

Only in the case that `MainStore.R` and `MainStore.W` has the inheritance relationship,
the composition is allowed and the upper resource type in the inheritance hierarchy is selected.
In this example, the `MainStore.W` resource type is used as the result of the composition between `UserRepository.create` and `UserRepository.find`,
which represents a resource for Database connection with a write privilege.
A compilation error occurs when you pass a resource type which doesn't have any inheritance relationship with `MainStore.R`.
An appropriate resource type is selected automatically before executing your code and that ensures that your code is type-safe at compile-time.

## Notice

`TxnT` doesn't provide `recover` and `recoverWith`.
The reason is that `TxnT` represents a transaction itself and any failures occurred within commit operations
to DB should not be recovered inside of a transaction but outside of a transaction boundary, we think.

---------------------------------------
---------------------------------------
---------------------------------------

## これはなに？

free-task は、IO処理に関するトランザクションを表すクラス、`TxnT` を提供するライブラリです。
処理そのものはほとんど提供しない1ファイルだけのものですが、その考え方の提供のためにライブラリ化して公開してあります。
これを利用することによって、安全かつ、（Freeを利用した）強力なトランザクションの合成を実現することができます。

## どう使う？

`example` 以下にサンプルコードが置いてありますので、そちらで利用方法を確認できます。
`example/test` 下に置かれている `UserRepositorySpec` が実際の利用コードですので、
そこから遡ってコードを確認すると分かりやすいでしょう。

```scala
val w = for {
  id   <- UserRepository.create("findall", age=99)
  user <- UserRepository.find(id)
} yield user.map(e => e.name + ":" + e.age) getOrElse ""
```

この例では、書き込み処理である `UserRepository.create` と、`UserRepository.find` とを合成しています。
ここで注目すべきは、それぞれのメソッドの型が以下のようになっていることです。

```scala
def find(id: UserId): Txn[MainStore.R, Option[User]] = Find(id)
def create(name: String, age: Int): Txn[MainStore.W, UserId] = Create(name, age)
```

リソースを表す型(以下で説明します)が `MainStore.R`, `MainStore.W` と異なるにも関わらず、
ここで合成が安全に成功していることがポイントです。

上記で登場する `Txn` は、このライブラリが提供する `TxnT` を実際に利用するために作成された型で、
以下のような実装になっています。

```scala
type EntityIO[A] = FreeC[EntityOp, A]
type Txn[-R, A] = TxnT[EntityIO, R, A]
```

`R` はリソースを示す型、`A` はオペレーションが返す結果の型です。
今回の例では、`R` には `MainStore.R`, `MainStore.W` というものを与えており、
これはごくシンプルな継承関係のみを持つクラスです。

```scala
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
