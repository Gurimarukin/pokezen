package pokezen.services.pokeapi

import javax.inject.Inject
import scala.concurrent._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json._

import pokezen.controllers.{SearcheableService, DetaileableService}
import pokezen.{Pokemon, PokemonNames, PokemonName, Type}


case class PokeAPIService @Inject()(
    ws: WSClient,
    ec: ExecutionContext) extends InjectedController
                             with SearcheableService
                             with DetaileableService {
  implicit val implicitEc = ec

  def getAndMap[A](route: String)
                  (implicit rds: Reads[A]): Future[Option[A]] = {
    val apiUrl = "https://pokeapi.co/api/v2"

    def validateResponse(response: WSResponse): Option[A] =
      if (response.status == 200)
        Json.parse(response.body).validate[A].asOpt
      else None

    ws.url(s"${apiUrl}${route}")
      .addHttpHeaders("Accept" -> "application/json")
      .get
      .map(validateResponse)
  }

  def pokemons: Future[Option[PokemonNames]] =
    this.getAndMap[PokeAPIPokemonNames](s"/pokemon")
        .map(names => names.map(_.sorted))

  def pokemonByName(name: PokemonName): Future[Option[Pokemon]] =
    this.getAndMap[PokeAPIPokemon](s"/pokemon/${name.name}")

  def pokemonsOfType(pokemonType: Type): Future[Option[PokemonNames]] = {
    implicit val pokemonInTypeReads: Reads[PokemonName] =
      (__ \ "pokemon" \ "name").read[String].map(PokemonName.apply _)

    val namesFromTypeReads: Reads[PokemonNames] = (
      (__ \ "pokemon").read[Seq[PokemonName]].map(
        (names: Seq[PokemonName]) => PokemonNames(names: _*))
    )
    this.getAndMap[PokemonNames](
      s"/type/${pokemonType.typeName}")(namesFromTypeReads)
  }
}
