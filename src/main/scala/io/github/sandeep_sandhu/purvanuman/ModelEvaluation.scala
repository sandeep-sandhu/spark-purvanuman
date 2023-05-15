// ModelEvaluation.scala
package io.github.sandeep_sandhu.purvanuman

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, lag}

// TODO: define class
// implement spark ml evaluation methods - MAE, MAD, MAPE, MASE, sMAPE

/**
 * Abstraction for time-series results for a given model.
 */
trait ModelEvaluation extends Serializable {

  // TODO: implement evaluation metrics:
  //  MAE = SUM(x.yhat-x.y)/n
  //  MAD = SUM(ABS(x.yhat-x.y))/n
  //  MSE = SUM(POWER((x.yhat-x.y),2))/n
  //  RMSE = sqrt(SUM(POWER((x.yhat-x.y),2))/n)
  //  MAPE = SUM(ABS((x.yhat-x.y)/x.y_corrected))/n

  //Dataframe output by the model's `transform` method.

  def predictions: DataFrame

  /** Field in "predictions" which gives the prediction of each class. */
  def predictionCol: String

  /** Field in "predictions" which gives the true label of each instance (if available). */
  def labelCol: String


}
