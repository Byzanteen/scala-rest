package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.example.Registry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.util.Timeout

//#import-json-formats
//#routes-class
class Routes(registry: ActorRef[Registry.Command])(implicit val system: ActorSystem[_]) {

  //#routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getStocks(country: String, location_id: Int): Future[Stocks] =
    registry.ask(GetStocks(country, location_id, _))
  def getStock(article_id: Int): Future[GetResponse] =
    registry.ask(GetStock(article_id, _))
  def createStock(stock: Stock): Future[ActionPerformed] =
    registry.ask(CreateStock(stock, _))
  def deleteStock(article_id: Int): Future[ActionPerformed] =
    registry.ask(DeleteStock(article_id, _))

  //#all-routes
  //#get-post
  //#get-delete
  val routes: Route =
    pathPrefix("stocks") {
      concat(
        //#get-delete
        pathEnd {
          concat(
            post {
              entity(as[Stock]) { stock =>
                onSuccess(createStock(stock)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        get {
          parameters('country.as[String], 'location_id.as[Int])  { (country: String, location_id: Int) =>
            complete(getStocks(country,location_id))
          }
        },
        //#get-delete
        path(Segment) { article_id =>
          concat(
            get {
              //#retrieve-user-info
              rejectEmptyResponse {
                onSuccess(getStock(article_id.toInt)) { response =>
                  complete(response.maybe)
                }
              }
              //#retrieve-user-info
            },
            delete {
              //#delete-logic
              onSuccess(deleteStock(article_id.toInt)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#delete-logic
            })
        })
      //#get-delete
    }
  //#all-routes
}
