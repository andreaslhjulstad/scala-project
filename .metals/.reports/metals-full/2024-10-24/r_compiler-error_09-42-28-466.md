file:///C:/Users/andre/Documents/dev/scala/scala_project_2024/bank_system/src/main/scala/Bank.scala
### java.lang.AssertionError: NoDenotation.owner

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.3
Classpath:
<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala3-library_3\3.3.3\scala3-library_3-3.3.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.12\scala-library-2.13.12.jar [exists ]
Options:



action parameters:
offset: 591
uri: file:///C:/Users/andre/Documents/dev/scala/scala_project_2024/bank_system/src/main/scala/Bank.scala
text:
```scala
import collection.mutable.Map

class Bank(val allowedAttempts: Integer = 3) {

  private val accountsRegistry: Map[String, Account] = Map()

  val transactionsPool: TransactionPool = new TransactionPool()
  val completedTransactions: TransactionPool = new TransactionPool()

  def processing: Boolean = !transactionsPool.isEmpty

  // TODO
  // Adds a new transaction for the transfer to the transaction pool
  def transfer(from: String, to: String, amount: Double): Unit = {
    val fromAcc = getAccount(from)
    val toAcc = getAccount(to)

    (fromAcc, toAcc) match {
        case (Some[@@_], Some(_))
    }

    transactionsPool.add(new Transaction(from, to, amount))
  }

  // TODO
  // Process the transactions in the transaction pool
  // The implementation needs to be completed and possibly fixed
  def processTransactions: Unit = {

    // val workers : List[Thread] = transactionsPool.iterator.toList
    //                                        .filter(/* select only pending transactions */)
    //                                        .map(processSingleTransaction)

    // workers.map( element => element.start() )
    // workers.map( element => element.join() )

    /* TODO: change to select only transactions that succeeded */
    // val succeded : List[Transaction] = transactionsPool

    /* TODO: change to select only transactions that failed */
    // val failed : List[Transaction] = transactionsPool

    // succeded.map(/* remove transactions from the transaction pool */)
    // succeded.map(/* add transactions to the completed transactions queue */)

    // failed.map(t => {
    /*  transactions that failed need to be set as pending again;
                if the number of retry has exceeded they also need to be removed from
                the transaction pool and to be added to the queue of completed transactions */
    // })

    if (!transactionsPool.isEmpty) {
      processTransactions
    }
  }

  // TODO
  // The function creates a new thread ready to process
  // the transaction, and returns it as a return value
  private def processSingleTransaction(t: Transaction): Thread = ???

  // Creates a new account and returns its code to the user.
  // The account is stored in the local registry of bank accounts.
  def createAccount(initialBalance: Double): String = {
    val code = (1000 + scala.util.Random.nextInt(9000)).toString
    val acc = new Account(code, initialBalance)

    accountsRegistry += (code -> acc)

    return code
  }

  // Return information about a certain account based on its code.
  // Remember to handle the case in which the account does not exist
  def getAccount(code: String): Option[Account] = {
    return accountsRegistry.get(code)
  }
}

```



#### Error stacktrace:

```
dotty.tools.dotc.core.SymDenotations$NoDenotation$.owner(SymDenotations.scala:2607)
	scala.meta.internal.pc.SignatureHelpProvider$.isValid(SignatureHelpProvider.scala:83)
	scala.meta.internal.pc.SignatureHelpProvider$.notCurrentApply(SignatureHelpProvider.scala:96)
	scala.meta.internal.pc.SignatureHelpProvider$.$anonfun$1(SignatureHelpProvider.scala:48)
	scala.collection.StrictOptimizedLinearSeqOps.dropWhile(LinearSeq.scala:280)
	scala.collection.StrictOptimizedLinearSeqOps.dropWhile$(LinearSeq.scala:278)
	scala.collection.immutable.List.dropWhile(List.scala:79)
	scala.meta.internal.pc.SignatureHelpProvider$.signatureHelp(SignatureHelpProvider.scala:48)
	scala.meta.internal.pc.ScalaPresentationCompiler.signatureHelp$$anonfun$1(ScalaPresentationCompiler.scala:435)
```
#### Short summary: 

java.lang.AssertionError: NoDenotation.owner