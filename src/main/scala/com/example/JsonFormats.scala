package com.example

import com.example.Registry.ActionPerformed

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val stockJsonFormat = jsonFormat7(Stock)
  implicit val stocksJsonFormat = jsonFormat1(Stocks)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
