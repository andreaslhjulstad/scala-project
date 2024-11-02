class Account(val code: String, val balance: Double) {

  // Implement functions. Account should be immutable.
  // Change return type to the appropriate one
  def withdraw(amount: Double): Either[String, Account] = {
    val newBalance = balance - amount
    if (amount < 0) {
      return Left("Amount can not be negative!")
    } else if (newBalance < 0) {
      return Left("Insufficient balance!")
    }

    return Right(new Account(code, newBalance))
  }

  def deposit(amount: Double): Either[String, Account] = {
    if (amount < 0) {
      return Left("Amount can not be negative!")
    }
    return Right(new Account(code, balance + amount))
  }

}
