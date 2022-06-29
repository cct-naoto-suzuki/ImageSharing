package DbAccess.Table

import scalikejdbc.{AutoSession, ResultName, SQLSyntaxSupport, WrappedResultSet}

import java.time.ZonedDateTime

/**
 * ポイント1: テーブルのカラムと対応するcase class を作成する(カラム名とクラスの要素名が一致してなくてもok)
 */
case class User(
    id: Int,
    name: String,
    password: String,
    emailAddress: String,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime)

// 2. 1 のcase class のコンパニオンオブジェクトを作成し、SQLSyntaxSupport[] を継承する

/**
 * ポイント2: case class のコンパニオンオブジェクトを作成し、SQLSyntaxSupport[] を継承する
 */
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
  // 3. "WrappedResultSet" と"ResultName[A]" 型の引数を取るapply 関数を定義する。

  /**
   * ポイント3: SQL の実行結果をUser クラスに格納するためのapply メソッドを用意する
   * @param resultSet SQL の実行結果が詰まったレコードスタック。
   * @param resultName テーブルのカラム名の情報を保持するオブジェクト。
   */
  def apply(resultSet: WrappedResultSet, resultName: ResultName[User]): User =
    User(
      resultSet.int(resultName.id),
      resultSet.string(resultName.name),
      resultSet.string(resultName.password),
      resultSet.string(resultName.emailAddress),
      resultSet.zonedDateTime(resultName.createdAt),
      resultSet.zonedDateTime(resultName.updatedAt)
    )

}
