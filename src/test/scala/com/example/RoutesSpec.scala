package com.example

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import akka.actor.typed.scaladsl.adapter._

class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.toClassic

  val registry = testKit.spawn(Registry())
  lazy val routes = new Routes(registry).routes

  // use the json formats to marshal and unmarshall objects in the test
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  "Routes" should {

    "be able to add users (POST /users)" in {
      val stock = Stock("RO", 2, 3, "electronics", "TV 4k", 22, "TV")
      val stockEntity = Marshal(stock).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      val request = Post("/stocks").withEntity(stockEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and we know what message we're expecting back:
        entityAs[String] should ===("""{"description":"Stock TV 4k created."}""")
      }

    }

    "return no stocks (GET /stocks)" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/stocks?country=\"Ro\"&location_id=2")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"Stocks":[]}""")
      }
    }

    "be able to remove users (DELETE /users)" in {
      // user the RequestBuilding DSL provided by ScalatestRouteSpec:
      val request = Delete(uri = "/stocks/3")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"description":"Stock 3 deleted."}""")
      }
    }
    //#actual-test
  }
  //#actual-test

  //#set-up
}
//#set-up
//#user-routes-spec
