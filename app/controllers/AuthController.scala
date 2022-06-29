package controllers

import DbAccess.Repository.UserRepository
import DbAccess.Table.User

import javax.inject._
import play.api._
import play.api.libs.json.JsResult.Exception
import play.api.libs.json._
import play.api.mvc._
import play.request.LoginReq

import java.time.ZonedDateTime
import scala.language.postfixOps

@Singleton
class AuthController @Inject() (
    val controllerComponents: ControllerComponents,
    val userRepository: UserRepository,
    val playBodyParsers: PlayBodyParsers)
  extends BaseController
  with Logging {

  private val MAX_NAME_LENGTH = 50

  def login(): Action[JsValue] = Action(parse.json) { request =>
    val sessionKey = "authenticated"
    val parsedReq = request.body.validate[LoginReq] match {
      case s: JsSuccess[LoginReq] => s.get
      case _: JsError => LoginReq.empty()
    }
    val userinfo = userRepository.findByEmailAddress(parsedReq.emailAddress)
    val emailAddress = userinfo.fold[String]("")(_.emailAddress)
    val password = userinfo.fold[String]("")(_.password)
    if (emailAddress.isEmpty || password.isEmpty) {
      throw Exception(JsError.apply("bad request exception"))
    }

    if (parsedReq.emailAddress == emailAddress && parsedReq.password == password) {
      Ok(
        Json.toJson(
          (Map("result" -> "success", "email_address" -> parsedReq.emailAddress, "password" -> parsedReq.password))
        )
      ).withSession("session" -> sessionKey)
    } else {
      BadRequest(
        Json.toJson(
          (Map("result" -> "failed", "email_address" -> parsedReq.emailAddress, "password" -> parsedReq.password))
        )
      )
    }
  }

}
