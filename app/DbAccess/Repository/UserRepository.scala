package DbAccess.Repository

import DbAccess.Table.User
import DbAccess.Table.User.autoSession
import scalikejdbc._

import java.time.ZonedDateTime
import javax.inject._

@Singleton
class UserRepository {

  /**
   * ポイント4: DB へのアクセスを行うクラス内で、以下の項目を設定する
   *
   * 1. DBのコネクションプールとのセッション情報
   *
   * 2. SQL の句以外の意味のある情報を管理する変数を定義する(後で解説)
   *
   * 3. DB との接続を行うため、setupAll() を唱える
   */
  implicit val session = AutoSession // 1. DB とのセッション情報

  /**
   *  2について: 主にテーブルのカラム名等の情報を管理する変数。SQL の句以外の情報を司っている。
   *
   *  scalikejdbc は「型安全に」SQL を関数チェーンで操作するのが目的なので、テーブルのカラムのscala 的な型情報を管理するには、
   *  実際のテーブルと呼応しているcase class からヘルパーオブジェクトを作成するのが妥当である。
   */
  val u = User.syntax("table_alias")

  scalikejdbc.config.DBs.setupAll() // 3. DB のセットアップ

  def findAll(): Seq[User] = {
    println(s"u.tableAliasName: ${u.tableAliasName}")
    sql"select * from users".map(User(_)).list().apply()
  }

  def findById(id: Int): Option[User] = {
    findAll().find(_.id == id)
  }

  def findByName(name: String): Option[User] = {
    findAll().find(_.name == name)
  }

  def findByEmailAddress(emailAddress: String): Option[User] = {
    findAll().find(_.emailAddress == emailAddress)
  }

  def findByNameAndPassword(name: String, password: String): Option[User] = {
    findAll().find(user => user.name == name && user.password == password)
  }

  // ここからDSL 表記

  def findAllDsl(): Seq[User] = {
    withSQL {
      select(u.result.*)
        .from(User as u)
    }.map(resultSet => User(resultSet, u.resultName)).list().apply()
  }

  def findByNameDsl(name: String): Option[User] = {
    withSQL {
      select(u.result.*)
        .from(User as u)
        .where
        .eq(u.name, name)
    }.map(resultSet => User(resultSet, u.resultName)).single().apply()
  }

  def insertOne(name: String, password: String, emailAddress: String): Int = {
    withSQL {
      insert.into(User).values(0, name, password, emailAddress, ZonedDateTime.now, ZonedDateTime.now)
    }.update().apply()
  }

  def deleteByName(name: String): Int = {
    withSQL {
      delete.from(User as u).where.eq(u.name, name)
    }.update().apply()
  }

  def updatePassword(name: String, password: String): Int = {
    withSQL {
      update(User as u)
        .set(u.password -> password)
        .where
        .eq(User.column.name, name)
    }.update().apply()
  }

}
