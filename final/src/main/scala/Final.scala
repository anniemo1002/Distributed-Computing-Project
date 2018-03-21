import org.apache.spark.SparkContext._
import scala.io._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.rdd._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import scala.collection._

object Final {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("lab6").setMaster("local[4]")
    val sc = new SparkContext(conf)
    //zip, lan, alt, state, county
    val zips = sc.textFile("/Users/annie/IdeaProjects/final/zipcode.csv").
      map(line => (line.split(",")(0).replaceAll("\"","").trim().toInt, (line.split(",")(1).replaceAll("\"","").trim(), line.split(",")(2).replaceAll("\"","").trim()), line.split(",")(4).replaceAll("\"","").trim(), line.split(",")(5).replaceAll("\"","").trim()))
      .filter { case (zip, (lan, alt), state, county) => (state == "MD" && county == "Montgomery") }.filter { case (zip, (lan, alt), state, county) => (lan!="" && alt !="") }
        .map{case (zip, (lan, alt), state, county)=> (zip, (lan.toDouble, alt.toDouble))}.collect()
    //zip, (lan, alt)
    //zips.collect().foreach(println(_))
    val violations = sc.textFile("/Users/annie/IdeaProjects/final/violations.csv")
      .map(line => (line.split(",")(4).trim(),line.split(",")(5).trim())).filter(x=>x._1!="" && x._2!="")
        .map(x=>(x._1.toDouble,x._2.toDouble))
    violations.foreach(println(_))
    println("---")
    val violation_zips = violations.map(p=>zips.minBy(zp=>math.pow((p._1-zp._2._1),2)+math.pow((p._2-zp._2._2),2)))
      .map{case(zip,p)=>zip}.countByValue()
    violation_zips.foreach(println(_))
    println("---")

    val crashes = sc.textFile("/Users/annie/IdeaProjects/final/crashes.csv")
      .map(line => (line.split(",")(0).trim(),line.split(",")(1).trim())).filter(x=>x._1!="" && x._2!="")
      .map(x=>(x._1.toDouble,x._2.toDouble))
    crashes.foreach(println(_))
    println("---")
    val crash_zips = crashes.map(p=>zips.minBy(zp=>math.pow((p._1-zp._2._1),2)+math.pow((p._2-zp._2._2),2)))
      .map{case(zip,p)=>zip}.countByValue()
    crash_zips.foreach(println(_))
  }
}
