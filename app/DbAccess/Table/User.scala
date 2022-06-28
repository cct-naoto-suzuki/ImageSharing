package DbAccess.Table

import scalikejdbc.{ResultName, SQLSyntaxSupport, WrappedResultSet}
import java.time.ZonedDateTime

case class User(
    id: Int,
    name: String,
    password: String,
    emailAddress: String,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime)

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"

  // 生のSQL を使った書き方用
  def apply(rs: WrappedResultSet): User =
    User(
      rs.int("id"),
      rs.string("name"),
      rs.string("password"),
      rs.string("email_address"),
      rs.zonedDateTime("created_at"),
      rs.zonedDateTime("updated_at")
    )

  // DSL を使った書き方用
  def apply(user: ResultName[User])(rs: WrappedResultSet): User =
    User(
      rs.int(user.id),
      rs.string(user.name),
      rs.string(user.password),
      rs.string(user.emailAddress),
      rs.zonedDateTime(user.createdAt),
      rs.zonedDateTime(user.updatedAt)
    )
}
