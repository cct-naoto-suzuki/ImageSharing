package DbAccess.Repository

import DbAccess.Table.User
import scalikejdbc._
import javax.inject._

@Singleton
class UserRepository {
  implicit val session = AutoSession
  scalikejdbc.config.DBs.setupAll()

  // DSL を使わない場合、findAll() でのみSELECT 文を実行し、他のAPI はこれの結果から再捜査する事になるため、若干パフォーマンスが落ちそう。
  def findAll(): Seq[User] = {
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
}
