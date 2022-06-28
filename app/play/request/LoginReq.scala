package play.request

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

import scala.language.postfixOps

case class LoginReq(emailAddress: String, password: String)

object LoginReq {

  implicit val loginRead: Reads[LoginReq] = (
    (__ \ "email_address").read[String] ~ (__ \ "password").read[String]
  ).apply(LoginReq(_, _))

  def empty(): LoginReq = apply("", "")
}
