package com.shmrkm.queue.provider

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.scaladsl.Source
import akka.util.{ByteString, Timeout}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random

object ProviderServerSample {
  implicit val system: ActorSystem = ActorSystem("queue-provider")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var orders: List[Item] = Nil

  final case class Item(name: String, id: Long)
  final case class Order(items: List[Item])

  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat2(Item)
  implicit val orderFormat: RootJsonFormat[Order] = jsonFormat1(Order)

  def fetchItem(itemId: Long): Future[Option[Item]] = Future {
    orders.find(_.id == itemId)
  }

  def saveOrder(order: Order): Future[Done] = {
    orders = order match {
      case Order(items) => items ::: orders
      case _ => orders
    }

    Future {Done}
  }

  def run(): Unit = {

    val route =
      get {
        pathPrefix("item" / LongNumber) { id =>
          val maybeItem: Future[Option[Item]] = fetchItem(id)
          onSuccess(maybeItem) {
            case Some(item) => complete(item)
            case None => complete(StatusCodes.NotFound)
          }
        }
      } ~
        post {
          path("create-order") {
            entity(as[Order]) { order =>
              val saved: Future[Done] = saveOrder(order)
              onComplete(saved) { done =>
                complete("""{"result":true}""")
              }
            }
          }
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 10080)
    println(s"http server online at http://localhost:10080")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }

  def streaming(): Unit = {
    val numbers = Source.fromIterator(() =>
      Iterator.continually(Random.nextInt()))

    val route =
      path("random") {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/plain(UTF-8)`,
              numbers.map(n => ByteString(s"$n\n"))
            )
          )
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 10081)
    println(s"running http://localhost:10082")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def actor(): Unit = {
    case class Bid(userId: String, offer: Int)
    case class Bids(bids: List[Bid])
    case object GetBids

    class Auction extends Actor with ActorLogging {
      var bids = List.empty[Bid]

      def receive: Receive = {

        case bid @ Bid(userId, offer) =>
          bids = bids :+ bid
          log.info(s"Bid complete $userId, $offer")

        case GetBids => sender() ! Bids(bids)
        case _ => log.info("Invalid message")
      }
    }

    implicit val bidFormat = jsonFormat2(Bid)
    implicit val bidsFormat = jsonFormat1(Bids)

    val auction = system.actorOf(Props[Auction], "auction")

    val route =
      path("auction") {
        put {
          parameter("bid".as[Int], "user") { (bid, user) =>
            auction ! Bid(user, bid)
            complete(StatusCodes.Accepted, "bid placed")
          }
        } ~
        get {
          implicit val timeout: Timeout = 5.seconds
          val bids: Future[Bids] = (auction ? GetBids).mapTo[Bids]
          complete(bids)
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 10082)
    println(s"http server online at http://localhost:10080")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def flow(): Unit = {
    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
        HttpResponse(entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          "<html><body><h1>Hello World</h1></body></html>"
        ))

      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
        HttpResponse(entity = "pong")

      case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
        sys.error("BOMB")

      case r: HttpRequest =>
        r.discardEntityBytes()
        HttpResponse(404, entity = "Unknown resource")
    }

    val bindingFuture = Http().bindAndHandleSync(requestHandler, "localhost", 10083)
    println(s"http server online at http://localhost:10083")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
