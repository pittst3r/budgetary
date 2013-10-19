package Helpers

object Numbers {

  def calculatePercentage(numerator: Double, denominator: Double): String = {
    "%.1f".format(numerator / denominator * 100)
  }

  def amountInCurrency(amount: Double): String = "%.2f".format(amount)

}
