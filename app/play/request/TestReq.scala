package play.request

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

case class TestDbInsert(name: String, password: String, emailAddress: String)

object TestDbInsert {

  implicit val readTestDbInsert: Reads[TestDbInsert] = (
    (__ \ "name").read[String] ~ (__ \ "password").read[String] ~ (__ \ "email_address").read[String]
  ).apply(TestDbInsert(_, _, _))

  def empty(): TestDbInsert = apply("", "", "")

}

case class TestDbDelete(name: String)

object TestDbDelete {

  implicit val readTestDbDelete = Reads[TestDbDelete] { json =>
    json \ "name" match {
      case JsDefined(name) => JsSuccess(TestDbDelete(name.as[String]))
      case error: JsUndefined => JsError(error.toString)
    }
  }
  def empty(): TestDbDelete = apply("")
}

case class TestDbUpdate(name: String, password: String)

object TestDbUpdate {

  implicit val readTestDbUpdate: Reads[TestDbUpdate] = (
    (__ \ "name").read[String] ~ (__ \ "password").read[String]
  ).apply(TestDbUpdate(_, _))

  def empty(): TestDbUpdate = apply("", "")

}

case class TestDbGetWithoutDslWithParamsReq(name: String)

object TestDbGetWithoutDslWithParamsReq {

  implicit val reads = Reads[TestDbGetWithoutDslWithParamsReq] { json =>
    val parsePattern = json \ "name" // こんな感じでパターンを書いていく
    parsePattern match {
      case JsDefined(name) => JsSuccess(TestDbGetWithoutDslWithParamsReq(name.as[String]))
      case error: JsUndefined => JsError(error.toString)
    }
  }

  def empty(): TestDbGetWithoutDslWithParamsReq = apply("")
}
