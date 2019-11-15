package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.example.Registry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

//#import-json-formats
//#routes-class
class Routes(Registry: ActorRef[Registry.Command])(implicit val system: ActorSystem[_]) {

  //#routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getStocks(): Future[Users] =
    Registry.ask(GetStocks)
  def getStock(article_id): Future[GetResponse] =
    Registry.ask(GetStocks(article_id, _))
  def createStock(stock: Stock): Future[ActionPerformed] =
    Registry.ask(CreateStock(stock, _))
  def deleteStock(article_id: Int): Future[ActionPerformed] =
    Registry.ask(DeleteStock(article_id, _))

  //#all-routes
  //#get-post
  //#get-delete
  val Routes: Route =
    pathPrefix("stocks") {
      concat(
        //#get-delete
        pathEnd {
          concat(
            get {
              complete(getStocks())
            },
            post {
              entity(as[Stock]) { stock =>
                onSuccess(createStock(stock)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#get-delete
        path(Segment) { article_id =>
          concat(
            get {
              //#retrieve-user-info
              rejectEmptyResponse {
                onSuccess(getStock(article_id)) { response =>
                  complete(response.maybe)
                }
              }
              //#retrieve-user-info
            },
            delete {
              //#delete-logic
              onSuccess(deleteStock(article_id)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#delete-logic
            })
        })
      //#get-delete
    }
  //#all-routes
}
