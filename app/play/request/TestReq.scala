package play.request

import play.api.libs.json._

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
