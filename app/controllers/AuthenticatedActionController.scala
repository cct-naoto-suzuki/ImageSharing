package controllers

import play.api.libs.json._
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * 認証済みユーザ用のアクションを定義するコントローラ。
 * ログイン済みユーザのみ実行可能なAPI はSecureAction を利用する。
 */
@Singleton
class AuthenticatedActionController @Inject() (val cc: ControllerComponents) extends AbstractController(cc) {

  // JSON を受け取る前提なので型パラメータじゃなくてこうした
  def SecureAction(bodyParser: BodyParser[JsValue]) = new ActionBuilder[Request, JsValue] {
    override def parser: BodyParser[JsValue] = cc.parsers.json
    override def executionContext: ExecutionContext = cc.executionContext

    // ここがSecureAction の実行部分になる
    override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {

      // セッションキーを見て、値が空じゃない or 期限が切れていない場合のみ許可してみる
      // 今回は期限のチェックの代わりに文字列が"authenticated" かどうかを調べる
      println("request.session.data(\"session\"): " + request.session.data("session"))
      if (request.session.data.isEmpty || request.session.data("session") != "authenticated") {
        Future(
          BadRequest(
            Json.toJson(
              Map("result" -> "Unauthorized")
            )
          )
        )(cc.executionContext)
      } else {
        block(request)
      }
    }
  }
}
