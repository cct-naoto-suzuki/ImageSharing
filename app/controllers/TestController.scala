package controllers

import DbAccess.Repository.UserRepository
import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.request.{LoginReq, TestDbDelete, TestDbGetWithoutDslWithParamsReq, TestDbInsert, TestDbUpdate}
import scalikejdbc._

import javax.inject._
import scala.language.postfixOps

@Singleton
class TestController @Inject() (
    val controllerComponents: ControllerComponents,
    val playBodyParsers: PlayBodyParsers,
    val authenticatedActionController: AuthenticatedActionController, // 認証済みかどうかの判定を行う
    val userRepository: UserRepository)
  extends BaseController
  with Logging {
  implicit val session = AutoSession

  // 引数なしのGET リクエストの時の書き方(sql 補完子を利用した書き方)
  def testDbGetWithoutDsl() = Action(parse.json) { request =>
    val suzuki = userRepository.findByName("suzuki")
    suzuki match {
      case Some(suzuki) =>
        Ok(
          Json.toJson(
            Map(
              "name" -> suzuki.name,
              "email_address" -> suzuki.emailAddress,
              "password" -> suzuki.password
            )
          )
        )
      case None =>
        Ok(Json.toJson(Map("name" -> "", "email_address" -> "", "password" -> "")))
    }

  }

  // 引数なしのGET リクエストの書き方(DSL Query を利用した書き方)
  def testDbGet() = Action(parse.json) { request =>
    scalikejdbc.config.DBs.setupAll()

    val jekyll = userRepository.findByNameDsl("Jekyll")

    println(s"jekyll: $jekyll")

    jekyll match {
      case Some(jekyll) =>
        Ok(
          Json.toJson(
            Map(
              "name" -> jekyll.name,
              "email_address" -> jekyll.emailAddress,
              "password" -> jekyll.password
            )
          )
        )
      case None =>
        Ok(Json.toJson(Map("name" -> "", "email_address" -> "", "password" -> "")))
    }
  }

  // 引数ありのGET リクエストの書き方
  def testDbGetWithoutDslWithParams() = Action(parse.json) { request =>
    val req = request.body.as[TestDbGetWithoutDslWithParamsReq] // 取得出来なかったときexception を投げてしまう
    val validatedReq = request.body.validate[TestDbGetWithoutDslWithParamsReq] // エラーハンドリングが可能

    val hoge = validatedReq match {
      case s: JsSuccess[TestDbGetWithoutDslWithParamsReq] => s.get
      case _: JsError => TestDbGetWithoutDslWithParamsReq.empty()
    }
    // まさかの結果は変わらなかったw
    println(s"req.name: ${req.name}")
    println(s"hoge.name: ${hoge.name}")

    val user = userRepository.findByNameDsl(req.name)
    user match {
      case Some(user) =>
        Ok(
          Json.toJson(
            Map(
              "name" -> user.name,
              "email_address" -> user.emailAddress,
              "password" -> user.password
            )
          )
        )
      case None =>
        Ok(Json.toJson(Map("name" -> "", "email_address" -> "", "password" -> "")))
    }

  }

  // INSERT のサンプル
  def testDbInsert() = Action(parse.json) { request =>
    val validatedReq = request.body.validate[TestDbInsert]

    val req = validatedReq match {
      case s: JsSuccess[TestDbInsert] => s.get
      case _: JsError => TestDbInsert.empty()
    }
    println(s"name: ${req.name}")
    println(s"email_address: ${req.emailAddress}")
    println(s"password: ${req.password}")

    val statusCode = userRepository.insertOne(req.name, req.password, req.emailAddress)
    println(s"statusCode: $statusCode")
    statusCode match {
      case 1 => Ok(Json.toJson(Map("result" -> "success")))
      case _ => BadRequest(Json.toJson(Map("result" -> "failure")))
    }
  }

  // DELETE のサンプル
  def testDbDelete() = Action(parse.json) { request =>
    val validatedReq = request.body.validate[TestDbDelete]

    val req = validatedReq match {
      case s: JsSuccess[TestDbDelete] => s.get
      case _: JsError => TestDbDelete.empty()
    }
    val statusCode = userRepository.deleteByName(req.name)
    statusCode match {
      case 1 => Ok(Json.toJson(Map("result" -> "success")))
      case _ => BadRequest(Json.toJson(Map("result" -> "failure")))
    }
  }

  def testDbUpdate() = Action(parse.json) { request =>
    val validatedReq = request.body.validate[TestDbUpdate]

    val req = validatedReq match {
      case s: JsSuccess[TestDbUpdate] => s.get
      case _: JsError => TestDbUpdate.empty()
    }
    val statusCode = userRepository.updatePassword(req.name, req.password)
    statusCode match {
      case 1 => Ok(Json.toJson(Map("result" -> "success")))
      case _ => BadRequest(Json.toJson(Map("result" -> "failure")))
    }
  }

  // 引数ありのGET でSecureAction の実験
  def testSecureAction() = authenticatedActionController.SecureAction(parse.json) { request =>
    val validatedReq = request.body.validate[TestDbGetWithoutDslWithParamsReq]

    val req = validatedReq match {
      case s: JsSuccess[TestDbGetWithoutDslWithParamsReq] => s.get
      case _: JsError => TestDbGetWithoutDslWithParamsReq.empty()
    }

    val user = userRepository.findByName(req.name)
    user match {
      case Some(user) =>
        Ok(
          Json.toJson(
            Map(
              "name" -> user.name,
              "email_address" -> user.emailAddress,
              "password" -> user.password
            )
          )
        )
      case None =>
        Ok(Json.toJson(Map("name" -> "", "email_address" -> "", "password" -> "")))
    }

  }

}
