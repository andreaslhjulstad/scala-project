import collection.mutable.Map

class Bank(val allowedAttempts: Integer = 3) {

  private val accountsRegistry: Map[String, Account] = Map()

  val transactionsPool: TransactionPool = new TransactionPool()
  val completedTransactions: TransactionPool = new TransactionPool()

  def processing: Boolean = !transactionsPool.isEmpty

  // Adds a new transaction for the transfer to the transaction pool
  def transfer(from: String, to: String, amount: Double): Unit = {
    val fromAcc = getAccount(from)
    val toAcc = getAccount(to)

    (getAccount(from), getAccount(to)) match {
      case (Some(_), Some(_)) =>
        transactionsPool.add(new Transaction(from, to, amount))
    }
  }

  // TODO
  // Process the transactions in the transaction pool
  // The implementation needs to be completed and possibly fixed
  def processTransactions: Unit = {

    val workers: List[Thread] = transactionsPool.iterator.toList
      .filter(_.getStatus() == TransactionStatus.PENDING)
      .map(processSingleTransaction)

    workers.map(element => element.start())
    workers.map(element => element.join())

    /* TODO: change to select only transactions that succeeded */
    val succeded: List[Transaction] =
      transactionsPool.transactions.filter((t: Transaction) =>
        t.getStatus() == TransactionStatus.SUCCESS
      )

    /* TODO: change to select only transactions that failed */
    val failed: List[Transaction] =
      transactionsPool.transactions.filter((t: Transaction) =>
        t.getStatus() == TransactionStatus.FAILED
      )

    succeded.map( /* remove transactions from the transaction pool */ )
    succeded.map( /* add transactions to the completed transactions queue */ )

    failed.map(t => {
      /*  transactions that failed need to be set as pending again;
                if the number of retry has exceeded they also need to be removed from
                the transaction pool and to be added to the queue of completed transactions */
    })

    if (!transactionsPool.isEmpty) {
      processTransactions
    }
  }

  // TODO
  // The function creates a new thread ready to process
  // the transaction, and returns it as a return value
  private def processSingleTransaction(t: Transaction): Thread = {
    val thread = new Thread {
      override def run(): Unit = {
        while (
          t.getStatus() == TransactionStatus.PENDING && t.retries > t
            .getAttempts()
        ) {
          t.incrementAttempts

          val (fromAcc, toAcc) = (getAccount(t.from), getAccount(t.to)) match {
            case (Some(fromAcc), Some(toAcc)) => (fromAcc, toAcc)
            case _                            => return
          }

          val newFrom = fromAcc.withdraw(t.amount)
          val newTo = toAcc.deposit(t.amount)

          (newFrom, newTo) match {
            case (Right(newFrom), Right(newTo)) =>
              accountsRegistry += (newFrom.code -> newFrom)
              accountsRegistry += (newTo.code -> newTo)
              t.setStatus(TransactionStatus.SUCCESS)
            case _ => t.setStatus(TransactionStatus.FAILED)
          }
        }
      }
    }

    thread.start
    thread
  }

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
