import org.scalacheck.Properties

import scala.concurrent._
import scala.concurrent.duration._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, contentAsString}
import play.api.libs.json._

import pokezen.controllers.PokemonDetails


object PokemonDetailsSpec extends Properties("PokemonDetails") {
  property("PokemonDetails") = {
    val controller: PokemonDetails =
      PokemonDetails(
        stubControllerComponents(),
        ExecutionContext.global)
    val result: Future[Result] =
      controller.pokemon("bar").apply(FakeRequest())
    val bodyText: String = contentAsString(result)(1 seconds)
    val res = Json.stringify(Json.parse("""
      {
        "name": "bar",
        "image": "bar_image",
        "types": [
          "fire",
          "air"
        ],
        "stats": [
          {
            "stat": "speed",
            "base": 70,
            "comparison": {
              "fire": 5,
              "air": -10
            }
          },
          {
            "stat": "defense",
            "base": 50,
            "comparison": {
              "fire": -15,
              "air": 10
            }
          }
        ]
      }
    """))
    bodyText == res
  }
}