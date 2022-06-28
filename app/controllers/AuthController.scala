package controllers

import javax.inject._
import play.api._
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.mvc._
import play.request.LoginReq

import scala.language.postfixOps

@Singleton
class AuthController @Inject() (
    val controllerComponents: ControllerComponents,
    val playBodyParsers: PlayBodyParsers)
  extends BaseController
  with Logging {

  private val MAX_NAME_LENGTH = 50

  def login() = Action(parse.json) { request =>
    val parsedReq = request.body.validate[LoginReq]

    val hoge = parsedReq match {
      case s: JsSuccess[LoginReq] => s.get
      case _: JsError => LoginReq.empty()
    }

    if (hoge.emailAddress == "aaaa@gmail.com" && hoge.password == "aaaa") {
      Ok(
        Json.toJson(
          (Map("result" -> "success", "email_address" -> hoge.emailAddress, "password" -> hoge.password))
        )
      )
    } else {
      BadRequest(
        Json.toJson(
          (Map("result" -> "failed", "email_address" -> hoge.emailAddress, "password" -> hoge.password))
        )
      )
    }
  }

}
