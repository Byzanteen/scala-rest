package com.example

//#user-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._

//#user-case-classes
final case class Stock(country: String, location_id: Int, article_id: Int, category: String, 
                      product_name: String, stock: Int, subcategory: String)
final case class Stocks(Stocks: immutable.Seq[Stock])
//#user-case-classes

object Registry {
  // actor protocol
  sealed trait Command
  final case class GetStocks(country: String, location_id: Int, replyTo: ActorRef[Stocks]) extends Command
  final case class CreateStock(stock: Stock, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetStock(article_id: Int, replyTo: ActorRef[GetResponse]) extends Command
  final case class DeleteStock(article_id: Int, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetResponse(maybe: Option[Stock])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  val uri = CassandraConnectionUri("cassandra://localhost:9042/dev")
  val session = Helper.createSessionAndInitKeyspace(this.uri)

  private def registry(stocks: Set[Stock]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetStocks(country, location_id, replyTo) =>
        replyTo ! Stocks(stocks.filter(_stock => (_stock.country == country && _stock.location_id == location_id)).toSeq)
        Behaviors.same
      case CreateStock(stock, replyTo) =>
        replyTo ! ActionPerformed(s"Stock ${stock.product_name} created.")
        Helper.createStock(stock,this.session)
        registry(stocks + stock)
      case GetStock(article_id, replyTo) =>
        replyTo ! GetResponse(stocks.find(_.article_id == article_id))
        Behaviors.same
      case DeleteStock(article_id, replyTo) =>
        replyTo ! ActionPerformed(s"Stock $article_id deleted.")
        registry(stocks.filterNot(_.article_id == article_id))
    }
}
//#registry-actor
