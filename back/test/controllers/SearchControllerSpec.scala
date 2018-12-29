import org.scalacheck.Properties

import scala.concurrent._
import scala.concurrent.duration._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, contentAsString}

import tests.MockSearchService
import pokezen.Name
import pokezen.controllers.{SearcheableService, SearchController}


object SearchControllerSpec extends Properties("SearchController") {
  property("searchPokemon") = {
    val controller: SearchController =
      SearchController(
        MockSearchService(),
        stubControllerComponents(),
        ExecutionContext.global)
    val result: Future[Result] =
      controller.searchPokemon("whatever").apply(FakeRequest())
    val bodyText: String = contentAsString(result)(1 seconds)
    bodyText == """["foo","bar"]"""
  }
}