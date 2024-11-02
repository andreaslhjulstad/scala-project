import scala.collection.mutable.ListBuffer
object TransactionStatus extends Enumeration {
  val SUCCESS, PENDING, FAILED = Value
}

class TransactionPool {

  val transactions = ListBuffer.empty[Transaction]

  // Remove and the transaction from the pool
  def remove(t: Transaction): Boolean = this.synchronized {
    val index = transactions.indexOf(t)
    if (index >= 0) {
      transactions.remove(index)
      true
    } else {
      false
    }
  }

  // Return whether the queue is empty
  def isEmpty: Boolean = {
    transactions.isEmpty
  }

  // Return the size of the pool
  def size: Integer = {
    transactions.size
  }

  // Add new element to the back of the queue
  def add(t: Transaction): Boolean = this.synchronized {
    transactions.append(t)
    return true
  }

  // Return an iterator to allow you to iterate over the queue
  def iterator: Iterator[Transaction] = {
    transactions.iterator
  }

}

class Transaction(
    val from: String,
    val to: String,
    val amount: Double,
    val retries: Int = 3
) {

  private var status: TransactionStatus.Value = TransactionStatus.PENDING
  private var attempts = 0

  def getStatus() = status

  def setStatus(newStatus: TransactionStatus.Value) = {
    status = newStatus
  }

  def incrementAttempts = {
    attempts += 1
  }

  def getAttempts() = attempts

  // TODO: Implement methods that change the status of the transaction

}
