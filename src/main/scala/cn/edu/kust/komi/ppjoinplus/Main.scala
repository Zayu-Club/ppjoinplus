package cn.edu.kust.komi.ppjoinplus

object main {
  def main(args: Array[String]): Unit = {
    val agent = new PPJoinPlus(0.8, 2)
    agent.debug = true
    agent.addRecord(
      "C D F",
      "A B E F G",
      "A B C D E",
      "B C D E F")
    agent.init()
    agent.ppjoin()
    agent.ppjoinplus()
    println("-" * 50)
    agent.checkAll()
  }
}
