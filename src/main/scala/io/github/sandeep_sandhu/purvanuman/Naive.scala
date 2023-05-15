/**
  * File: Naive.scala
  * Purpose: Trains a simple time series model that generates forecast by repeating the last observed value
  */

package io.github.sandeep_sandhu.purvanuman

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, udf, _}
import org.apache.spark.ml.{Estimator, Pipeline, PipelineModel, PipelineStage, PredictionModel}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.ml.util.{GeneralMLWritable, GeneralMLWriter, Identifiable}
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, MetadataBuilder, StringType, StructType}


/**
 * NaiveEstimator
 * TODO: To be implemented
 * get next m time periods required to be forecasted
 * set input column and schema - get time-date column, get input column = y
 * set time period frequency
 * save transformer object with these parameters
 */
object NaiveEstimator extends org.apache.spark.ml.Predictor {

  final val timeCol = new Param[String](this, "time_index", "The column with the date-time values")
  final val inputCol = new Param[String](this, "y_input", "The input column")
  final val forecastCol = new Param[String](this, "y_forecast", "The output column")

  def setInputCol(value: String): this.type = set(inputCol, value)

  def setOutputCol(value: String): this.type = set(forecastCol, value)

  override val uid: String = {
    Identifiable.randomUID("NaiveForecast")
  }

  /**
   * Fits a naive forecast model as a transformer
   * @param dataset
   * @return
   */
  override def fit(dataset: Dataset[_]): Nothing = ???


  override def transformSchema(schema: StructType): StructType = {
    var logger: Logger = Logger.getLogger("NaiveEstimator")
    logger.setLevel(Level.DEBUG)

    // Check that the time index is a date/time field or long or integer
    val idx = schema.fieldIndex($(timeCol))
    val field = schema.fields(idx)
    if (field.dataType != IntegerType || field.dataType != LongType) {
      throw new Exception(s"Time index data type ${field.dataType} did not match input type DoubleType")
    }

    // check input column is double or float
    val input_idx = schema.fieldIndex($(inputCol))
    val inputField = schema.fields(input_idx)
    if (inputField.dataType != DoubleType) {
      throw new Exception(s"Input data type ${inputField.dataType} did not match input type DoubleType")
    }

    // add output column metadata:
    var metaBuild: MetadataBuilder = new MetadataBuilder().putString(
      "Description", "Naiive forecast value":String);
    val metaData = metaBuild.build()
    // Add the return field
    schema.add(
      org.apache.spark.sql.types.StructField(
        $(forecastCol),
        DoubleType,
        nullable=true,
        metadata=metaData)
    )
  }

  override def copy(extra: ParamMap): Nothing = ???

  override protected def train(dataset: Dataset[_]): Nothing = ???
}



/**
 * TODO: implement this model:
 * 1. determine most recent time period
 * 2. calculate next m time periods from most recent time period
 * 3. get previous y value and set as forecasted y for m time periods
 * 4. add to a new dataframe and return back
 * @param uid
 */
class NaiveForecastModel  (override val uid: String)
      extends PredictionModel[Vector[Double], NaiveForecastModel] with GeneralMLWritable
 {

  override def write: GeneralMLWriter = ???

  override def predict(features: Vector[Double]): Double = ???

  override def copy(extra: ParamMap) = {
    defaultCopy(extra)
  }

}
