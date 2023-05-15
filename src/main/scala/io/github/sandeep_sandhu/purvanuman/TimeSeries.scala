/**
  * File: TimeSeries.scala
  * Purpose: Implements a time series object
  * with useful methods such as interpolation and duplicate time value checks.
  */
package io.github.sandeep_sandhu.purvanuman

import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, lag}
import java.io.{IOException, ObjectStreamException}

/**
  * This class encapsulates a time series.
  * @param TimeAnchor time in milliseconds since epoch (1-Jan-1970), or date days since epoch, date months since epoch, years since epoch, etc.
  * @param TimeCycle length = 7 implies a weekly series, length = 1 implies daily, etc.
  * @param TimeValues dataframe with the timeseries data
  * @param timeValueCol the name of the time series column y
  * @param timeIndexCol the name of the column indicating the timestamp
  * @param InterpolationMethod InterpolationMethod: none=0, linear=1, exponential spline=2, cubic spline=3
  */
@SerialVersionUID(100L)
class TimeSeries(
  val TimeAnchor: String,
  val TimeCycle: Int,
  var TimeValues: DataFrame,
  var timeValueCol: String,
  var timeIndexCol: String,
  var InterpolationMethod: Int
) extends Serializable {

  override def toString: String = Identifiable.randomUID(
    prefix = f"TimeSeries[Cycle=$TimeCycle%s, Anchor=${TimeAnchor}]"
  )

  // array of column names of the covariates of the dataset
  var covariatesCol: List[String] = List()

  // usageIsForecast : is for forecast if true, is for fitting if false
  var usageIsForecast: Boolean = false;

  /**
    * In the dataframe, Keep only time series column y and covariates
    */
  def keepOnlyCovariates(): Unit = {
    var colsToKeep = timeIndexCol :: timeValueCol :: Nil
    colsToKeep :::= covariatesCol
    TimeValues = TimeValues.select(
      colsToKeep.map(x => col(x)): _*
    )
  }

  /**
    * Sort time series in ascending order of time value
    */
  def sortTimeSeries(): Unit =
    TimeValues = TimeValues.sort(timeIndexCol)

  /**
    * Check for duplicate time values, throw an error if duplicates are found.
    */
  def checkDuplicates(): Unit = {
    // get count of grouped ts
    val duplicates_count = TimeValues
      .groupBy(timeIndexCol)
      .count()
      .filter("count > 1")
      .collect();

    if (duplicates_count.length > 0) {
      throw new RuntimeException(
        "Duplicate time values not permitted in time series data."
      )
    }
  }

  /**
    * Interpolate missing and expected data points
    */
  def interpolateData(): Unit =
    if (InterpolationMethod == 1) {
      // TODO: implement linear interpolation
    } else if (InterpolationMethod == 2) {
      // TODO: implement exponential spline interpolation
    } else if (InterpolationMethod == 3) {
      // TODO: implement cubic spline interpolation
    }

  private def writeObject(out: java.io.ObjectOutputStream) =
    throw new IOException()

  private def readObject(in: java.io.ObjectInputStream) =
    throw new IOException()
  // throw ClassNotFoundException

  private def readObjectNoData() = {
    //throw ObjectStreamException();
  }

  sortTimeSeries();
  checkDuplicates();
  interpolateData();

}
