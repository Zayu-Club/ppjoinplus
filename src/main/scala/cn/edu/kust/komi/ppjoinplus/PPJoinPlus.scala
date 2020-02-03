package cn.edu.kust.komi.ppjoinplus

import cn.edu.kust.komi.ppjoinplus.models.Record

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class PPJoinPlus(t:Double = 0.8) {
  val records: mutable.ListBuffer[Record] = new mutable.ListBuffer[Record]
  var threshold: Double = t
  var tokenCount: Map[String, Int] = Map[String, Int]()

  def addRecord(lines: String*): Unit = lines.foreach(records += new Record(_))

  def init(): Unit = {
    println("> Initialization <")
    tokenCount = records.flatMap(record => record.tokens).groupBy(identity).mapValues(_.length)
    records.foreach(_.init(tokenCount, threshold))
    println(this)
  }

  def ppjoin(): Set[(Int, Int)] = {
    println("> PPJoin <")

    var S: Set[(Int, Int)] = Set[(Int, Int)]()

    // Inverted indices token in prefix map: I
    val I: mutable.HashMap[String, ListBuffer[(Int, Int)]] = new mutable.HashMap[String, ListBuffer[(Int, Int)]]()

    records.zipWithIndex.foreach {
      case (record, recordID) =>
        // Candidates map: A [recordID, same token count in prefix]
        val A: mutable.HashMap[Int,Int] = mutable.HashMap[Int,Int]()
        record.tokens.take(record.prefix).zipWithIndex.foreach {
          case (token, tokenIndex) =>
            I.getOrElseUpdate(token, new ListBuffer[(Int, Int)]())
              .filter {
                case (recordId, _) => records(recordId).tokens.length >= threshold * record.tokens.length
              }
              .foreach {
                case (invertedRecordId, _) =>
                  val prefixSameTokensCount = record.prefixTokens().toSet.&(records(invertedRecordId).prefixTokens().toSet).size
                  if (prefixSameTokensCount > 0)
                    A(invertedRecordId) = prefixSameTokensCount
              }
            I(token).append((recordID, tokenIndex))
        }
        // verify(record, A, alpha)
        for (relevantRecordID <- A.keySet) {
          val alpha = record.alphaWith(records(relevantRecordID), threshold)
          val overlap = record.tokens.toSet.&(records(relevantRecordID).tokens.toSet).size
          if (overlap >= alpha)
            S += ((recordID, relevantRecordID))
        }
        printf("A[%d - %s|%s]: %s\n", recordID, record.tokens.take(record.prefix).mkString(" "), record.tokens.takeRight(record.tokens.length - record.prefix).mkString(" "), A.toList.mkString(" "))
    }

    // Result Log
    println("\n###### Result ######")
    println("Inverted indices:")
    for ((k, l) <- I) printf("%s -> %s\n", k, l.mkString(" "))
    printf("Threshold: %f. Verify:\n", threshold)
    for ((x, y) <- S) printf("%d - %d => %1.3f\n", x, y, jaccard(records(x).tokens.toSet, records(y).tokens.toSet))
    print("#### All Result ####")
    for (x <- records.indices) {
      print("\n")
      for (y <- x+1 until records.length) {
        val sim = jaccard(records(x).tokens.toSet, records(y).tokens.toSet)
        if (sim < threshold) print("×")
        else print("√")
        printf("%d->%d:%1.3f  ",x, y,sim)
      }
    }
    println("####################")

    S
  }

  def jaccard(x: Set[Any], y: Set[Any]): Double = {
    val a: Double = x.&(y).size
    val b: Double = (x ++ y).size
    a / b
  }

  override def toString: String = records.zipWithIndex.map { case (record, index) => "%05d ｜ [%-30s] <= [%-30s]".format(index, record.tokens.mkString(" "), record.original) }.mkString("\n")
}
