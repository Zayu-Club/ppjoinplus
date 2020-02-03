package cn.edu.kust.komi.ppjoinplus

object main {
  def main(args: Array[String]): Unit = {
    val agent = new PPJoinPlus()
    agent.addRecord(
      "C D F",
      "A B E F G",
      "A B C D E",
      "B C D E F")
    agent.init(0.6)
    agent.ppjoin()
  }
}
