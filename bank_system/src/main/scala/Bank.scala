import collection.mutable.Map
import TransactionStatus.PENDING
import scala.collection.concurrent.TrieMap

class Bank(val allowedAttempts: Integer = 3) {

  private val accountsRegistry = new TrieMap[String, Account]()

  val transactionsPool: TransactionPool = new TransactionPool()
  val completedTransactions: TransactionPool = new TransactionPool()

  def processing: Boolean = !transactionsPool.isEmpty

  // Adds a new transaction for the transfer to the transaction pool
  def transfer(from: String, to: String, amount: Double): Unit =
    this.synchronized {
      val fromAcc = getAccount(from)
      val toAcc = getAccount(to)

      (getAccount(from), getAccount(to)) match {
        case (Some(_), Some(_)) =>
          transactionsPool.add(new Transaction(from, to, amount))
        case _ => return
      }
    }

  // TODO
  // Process the transactions in the transaction pool
  // The implementation needs to be completed and possibly fixed
  def processTransactions: Unit = {
    val workers: List[Thread] = transactionsPool.synchronized {
      transactionsPool.iterator.toList
        .filter(_.getStatus() == TransactionStatus.PENDING)
        .map(processSingleTransaction)
    }

    workers.map(element => element.start())
    workers.map(element => element.join())

    val succeded: List[Transaction] =
      transactionsPool.transactions
        .filter((t: Transaction) => t.getStatus() == TransactionStatus.SUCCESS)
        .toList

    val failed: List[Transaction] =
      transactionsPool.transactions
        .filter((t: Transaction) => t.getStatus() == TransactionStatus.FAILED)
        .toList

    succeded.map(t => transactionsPool.remove(t))
    succeded.map(t => completedTransactions.add(t))

    failed.map(t => {
      if (t.getAttempts() >= t.retries) {
        transactionsPool.remove(t)
        completedTransactions.add(t)
      } else {
        t.setStatus(TransactionStatus.PENDING)
      }

      /*  transactions that failed need to be set as pending again;
                  if the number of retry has exceeded they also need to be removed from
                  the transaction pool and to be added to the queue of completed transactions */
    })

    if (!transactionsPool.isEmpty) {
      processTransactions
    }
  }

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

          transactionsPool.synchronized {
            val (fromAcc, toAcc) =
              (getAccount(t.from), getAccount(t.to)) match {
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
              case _ => {
                t.setStatus(TransactionStatus.FAILED)
              }
            }
          }
        }
      }
    }

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
