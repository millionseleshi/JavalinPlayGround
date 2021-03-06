/*
 * @Author Million Seleshi
 *  2021.
 */

package repository

import domain.User
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

internal object Users : LongIdTable() {
    val email: Column<String> = varchar("email", 200).uniqueIndex()
    val username: Column<String> = varchar("username", 100)
    val password: Column<String> = varchar("password", 150)
    val token: Column<String> = varchar("token", 200)

    fun toDomain(row: ResultRow): User {
        return User(
            id = row[id].value,
            email = row[email],
            username = row[username],
            password = row[password],
            token = row[token]
        )
    }
}

class UserRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource))
        {
            SchemaUtils.create(Users)
        }
    }

    fun findById(id: Long): User? {
        return transaction(Database.connect(dataSource))
        {
            Users.select { Users.id eq id }
                .map { Users.toDomain(it) }
                .firstOrNull()
        }
    }

    fun create(user: User): Long? {
        return transaction(Database.connect(dataSource))
        {
            Users.insertAndGetId { row ->
                row[email] = user.email
                row[username] = user.username.toString()
                row[token] = user.token.toString()
                row[password] = user.password.toString()
            }
        }.value
    }

    fun findByEmail(email: String): User? {
        return transaction(Database.connect(dataSource))
        {
            Users.select { Users.email eq email }
                .map { Users.toDomain(it) }
                .firstOrNull()
        }
    }

    fun update(email: String, user: User): User? {
        transaction(Database.connect(dataSource))
        {
            Users.update({ Users.email eq email }) { row ->
                row[Users.email] = user.email
                if (user.username != null) {
                    row[username] = user.username
                }
                if (user.token != null) {
                    row[token] = user.token
                }
                if (user.password != null) {
                    row[password] = user.password
                }
            }
        }
        return findByEmail(user.email)
    }

    fun delete(email: String) {
        transaction(Database.connect(dataSource)) {
            Users.deleteWhere { Users.email eq email }
        }
    }

}