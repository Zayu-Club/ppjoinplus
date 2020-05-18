package cn.edu.kust.komi.ppjoinplus

import cn.edu.kust.komi.ppjoinplus.models.Record

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class PPJoinPlus(t: Double = 0.8, depth: Int = 1) {
  val records: mutable.ListBuffer[Record] = new mutable.ListBuffer[Record]
  var threshold: Double = t
  var maxDepth: Int = depth
  var tokenCount: Map[String, Int] = Map[String, Int]()

  var debug: Boolean = false

  def addRecord(lines: String*): Unit = lines.foreach(records += new Record(_))

  def init(): Unit = {
    println("> Initialization <")
    tokenCount = records.flatMap(record => record.tokens).groupBy(identity).mapValues(_.length)
    records.foreach(_.order(tokenCount))
    if (debug) println(this)
  }

  def ppjoin(): Set[(Int, Int)] = {
    println("> PPJoin Threshold: %f <".format(threshold))

    var S: Set[(Int, Int)] = Set[(Int, Int)]()

    // Inverted indices token in prefix map: I
    val I: mutable.HashMap[String, ListBuffer[(Int, Int)]] = new mutable.HashMap[String, ListBuffer[(Int, Int)]]()

    records.zipWithIndex.foreach {
      case (record, recordID) =>
        // Candidates List
        val A: mutable.Set[Int] = mutable.Set[Int]()
        record.tokens.take(record.prefix(t)).zipWithIndex.foreach {
          case (token, tokenIndex) =>
            I.getOrElseUpdate(token, new ListBuffer[(Int, Int)]())
              .filter {
                case (recordId, _) => records(recordId).tokens.length >= threshold * record.tokens.length
              }
              .foreach {
                case (invertedRecordId, _) =>
                  if (record.prefixTokens(t).toSet.&(records(invertedRecordId).prefixTokens(t).toSet).nonEmpty)
                    A += invertedRecordId
              }
            I(token).append((recordID, tokenIndex))
        }
        // verify(record, A, alpha)
        for (relevantRecordID <- A) {
          val a = alpha(record, records(relevantRecordID))
          val overlap = record.tokens.toSet.&(records(relevantRecordID).tokens.toSet).size
          if (overlap >= a)
            S += ((recordID, relevantRecordID))
        }

        if (debug) printf("Candidates: %d -> %s\n", recordID, A.toList.mkString(" "))
    }

    // - Result Log ---------
    if (debug) debugLogout(I, S)
    // ----------------------

    S
  }

  def ppjoinplus(): Set[(Int, Int)] = {
    println("> PPJoin+ Threshold: %f <".format(threshold))

    var S: Set[(Int, Int)] = Set[(Int, Int)]()
    // Inverted indices token in prefix map: I
    val I: mutable.HashMap[String, ListBuffer[(Int, Int)]] = new mutable.HashMap[String, ListBuffer[(Int, Int)]]()

    records.zipWithIndex.foreach {
      case (record, recordID) =>
        val A: mutable.Set[Int] = mutable.Set[Int]()

        record.prefixTokens(t).zipWithIndex.foreach {
          case (token, tokenIndex) =>
            I.getOrElseUpdate(token, new ListBuffer[(Int, Int)]())
              .filter {
                case (recordID, _) => records(recordID).tokens.length >= threshold * record.tokens.length
              }
              .foreach {
                case (docID, wordID) =>
                  val xp = record.prefixTokens(t)
                  val yp = records(docID).prefixTokens(t)
                  val xs = record.suffixTokens(t)
                  val ys = records(docID).suffixTokens(t)
                  if (xp.toSet.&(yp.toSet).nonEmpty) {
                    val hMax = hammingMax(record, records(docID), tokenIndex, wordID)
                    if (suffixFiltering(xs, ys, hMax, 1) <= hMax) A += docID
                  }
              }
            I(token).append((recordID, tokenIndex))
        }

        // verify(record, A, alpha)
        for (relevantRecordID <- A) {
          val a = alpha(record, records(relevantRecordID))
          val overlap = record.tokens.toSet.&(records(relevantRecordID).tokens.toSet).size
          if (overlap >= a)
            S += ((recordID, relevantRecordID))
        }

        if (debug) printf("Candidates: %d -> %s\n", recordID, A.toList.mkString(" "))
    }

    // - Result Log ---------
    if (debug) debugLogout(I, S)
    // ----------------------

    def suffixFiltering(x: List[String], y: List[String], hMax: Int, d: Int): Int = {
      if (d >= maxDepth) {
        return (x.length - y.length).abs
      }
      val w = y((y.length / 2.0).ceil.toInt - 1)

      val (xl, xr) = partition(x, w)
      val (yl, yr) = partition(y, w)

      var h = (xl.length - yl.length).abs + (xr.length - yr.length).abs
      if (h > hMax) return h
      else {
        val hl = suffixFiltering(xl, yl, hMax - (xr.length - yr.length).abs, d + 1)
        h = hl + (xr.length - yr.length).abs
        if (h <= hMax)
          hl + suffixFiltering(xr, xr, hMax - hl, d + 1)
        else
          h
      }
    }

    def partition(s: List[String], w: String): (List[String], List[String]) = {
      s.indexOf(w) match {
        case -1 =>
          (List[String](), List[String]())
        case index =>
          (s.take(index), s.takeRight(s.length - index - 1))
      }
    }

    S
  }

  def alpha(x: Record, y: Record): Int =
    ((threshold / (1 + threshold)) * (x.tokens.length + y.tokens.length)).ceil.toInt

  def hammingMax(x: Record, y: Record, i: Int, j: Int): Int =
    x.tokens.length + y.tokens.length - 2 * ((threshold / (1 + threshold)) * (x.tokens.length + y.tokens.length)).ceil.toInt - (i + j)

  def debugLogout(I: mutable.HashMap[String, ListBuffer[(Int, Int)]], S: Set[(Int, Int)]): Unit = {
    println("> Inverted indices <")
    for ((k, l) <- I) println("%s -> %s".format(k, l.mkString(" ")))
    println("> Verify Result <")
    for ((x, y) <- S) printf("%d <-> %d => %1.3f\n", x, y, jaccard(records(x).tokens.toSet, records(y).tokens.toSet))
  }

  def checkAll(): Unit = {
    println("> Check  All <")
    for (x <- records.indices)
      for (y <- x + 1 until records.length)
        println("%d <-> %d => %1.3f".format(x, y, jaccard(records(x).tokens.toSet, records(y).tokens.toSet)))
  }

  def jaccard(x: Set[Any], y: Set[Any]): Double = {
    val a: Double = x.&(y).size
    val b: Double = (x ++ y).size
    a / b
  }

  override def toString: String = records.zipWithIndex.map { case (record, index) => "%05d ｜ [%-30s] <= [%-30s]".format(index, record.tokens.mkString(" "), record.original) }.mkString("\n")
}
