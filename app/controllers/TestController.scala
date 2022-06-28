package controllers

import DbAccess.Repository.UserRepository
import DbAccess.Table.User
import play.api._
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsError, JsResult, JsSuccess, Json, Reads, __}
import play.api.mvc._
import play.request.{LoginReq, TestDbGetWithoutDslWithParamsReq}
import scalikejdbc._

import javax.inject._
import scala.language.postfixOps

@Singleton
class TestController @Inject() (
    val controllerComponents: ControllerComponents,
    val playBodyParsers: PlayBodyParsers,
    val userRepository: UserRepository)
  extends BaseController
  with Logging {
  implicit val session = AutoSession

  implicit val rds: Reads[(String, String)] = (
    (__ \ Symbol("email_address")).read[String] and
      (__ \ Symbol("password")).read[String]
  ) tupled

  def testLogin() = Action(parse.json) { request =>
    val parsedReq = request.body
      .validate[(String, String)](rds)
      .fold[LoginReq](
        _ => LoginReq.empty(),
        {
          case (emailAddress, password) => LoginReq(emailAddress, password)
          case _ => LoginReq("", "")
        }
      )

    // 本当はここでDBへのアクセス及びログイン情報の照会を行う
    if (parsedReq.emailAddress == "aaaa@gmail.com" && parsedReq.password == "aaaa") {
      Ok(
        Json.toJson(
          (Map("result" -> "success", "email_address" -> parsedReq.emailAddress, "password" -> parsedReq.password))
        )
      )
    } else {
      BadRequest(
        Json.toJson(
          (Map("result" -> "failed", "email_address" -> parsedReq.emailAddress, "password" -> parsedReq.password))
        )
      )
    }
  }

  // 引数なしのGET リクエストの時の書き方(sql 補完子を利用した書き方)
  def testDbGetWithoutDsl() = Action(parse.json) { request =>
    scalikejdbc.config.DBs.setupAll()

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
  def testDbGetWithDsl() = Action(parse.json) { request =>
    scalikejdbc.config.DBs.setupAll()

    val u = User.syntax("u")
    val jekyll: Seq[Option[User]] = Seq(withSQL[User] {
      select.from(User as u).where.eq(u.name, "Jekyll")
    }.map(User(u.resultName)).single().apply())
    println(s"jekyll: $jekyll")

    jekyll.head match {
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
    scalikejdbc.config.DBs.setupAll()

    val req = request.body.as[TestDbGetWithoutDslWithParamsReq] // バリデーションチェックはかからないけど取り合えず取得したいならこれが良い
    val validatedReq = request.body.validate[TestDbGetWithoutDslWithParamsReq] // バリデーションチェック付きだと思ったけどそうでもないのかも？？

    val hoge = validatedReq match {
      case s: JsSuccess[TestDbGetWithoutDslWithParamsReq] => s.get
      case _: JsError => TestDbGetWithoutDslWithParamsReq.empty()
    }
    // まさかの結果は変わらなかったw
    println(s"req.name: ${req.name}")
    println(s"hoge.name: ${hoge.name}")

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
