/**
  * TimeSeriesSpec:
  *
  * Run coverage report with sbt using command:
  * sbt ';coverageEnabled;test'
  */

import org.apache.spark.sql.{DataFrame, SQLContext, SQLImplicits, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, IntegerType, DoubleType, StructField, StructType}
import org.apache.spark.sql.Row

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Outcome}
import org.scalatest.funsuite.AnyFunSuite

import io.github.sandeep_sandhu.purvanuman._

// TimeSeriesSpec: tests the time series fitting methods
class TimeSeriesSpec
    extends AnyFunSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  self =>
  @transient var ss: SparkSession = null
  @transient var testDF: DataFrame = null
  @transient var ts_schema: StructType = null
  @transient var ts1: TimeSeries = null

  private object testImplicits extends SQLImplicits {
    protected override def _sqlContext: SQLContext = self.ss.sqlContext
  }

  override def beforeAll(): Unit = {

    val sparkConfig = new SparkConf().setAppName("TimeSeries Test")
    sparkConfig.set("spark.broadcast.compress", "false")
    sparkConfig.set("spark.shuffle.compress", "false")
    sparkConfig.set("spark.sql.shuffle.partitions", "4")
    sparkConfig.set("spark.shuffle.spill.compress", "false")
    sparkConfig.set("spark.master", "local")

    ss = SparkSession.builder().config(sparkConfig).getOrCreate();

    val rawData: Seq[(String, String, Double)] = Seq(
      ("T1", "X", 1.0),
      ("T2", "X", 2.0),
      ("T3", "X", 3.0),
      ("T4", "X", 4.0),
      ("T5", "X", 5.0),
      ("T6", "Y", 1.0),
      ("T7", "Y", 2.0),
      ("T6", "Y", 3.0)
    );

    ts_schema = StructType(Array(
      StructField("id", StringType, true),
      StructField("category", StringType, true),
      StructField("ts", DoubleType, true)
    ))

    val testrdd:RDD[(String, String, Double)] = ss.sparkContext.parallelize(rawData);
    val rowRDD = testrdd.map(attributes => Row(attributes._1, attributes._2, attributes._3))

    testDF = ss.createDataFrame(rowRDD,ts_schema)

  }

  override def afterAll(): Unit =
    ss.stop()

  test("create new timeseries") {

    ts1 = new TimeSeries(
      "milliseconds",
      1,
      testDF.filter("category == 'X'"),
      "ts",
      "id",
      0
    )

    assert(ts1.TimeValues.count() == 5)

    ts1.keepOnlyCovariates();
    // check column count
    assert(ts1.TimeValues.columns.length == 2)

  }

}
