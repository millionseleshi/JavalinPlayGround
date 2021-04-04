package controller


import domain.User
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import org.valiktor.validate
import service.UserService
import util.PasswordValid.isPasswordValid

class UserController(private val userService: UserService) {

    fun register(ctx: Context): User {
        return ctx.body<User>().apply {
            validate(this) {
                validate(User::email).isEmail()
                validate(User::password).isPasswordValid()
            }
        }.also { userService.create(it) }
            .apply {
                ctx.json(this).status(HttpStatus.CREATED_201)
            }
    }

    fun getCurrentUser(ctx: Context): User {
        val userEmail = ctx.attribute<String>("email")!!
        return userService.getByEmail(userEmail)
            .apply { ctx.json(this).status(HttpStatus.OK_200) }
    }

    fun updateUser(ctx: Context): User {
        val userEmail = ctx.attribute<String>("email")!!
        return ctx.body<User>()
            .apply {
                validate(this)
                {
                    validate(User::email).isEmail().isNotBlank()
                    validate(User::username).isNotBlank()
                    validate(User::password).isPasswordValid()
                    validate(User::token).isNotBlank()
                }
            }.apply {
                userService.update(userEmail, this)
            }.also {
                ctx.json(it).status(HttpStatus.OK_200)
            }
    }

    fun deleteUser(ctx: Context) {
        val userEmail = ctx.pathParam<String>("email").get()
        userService.delete(userEmail)
    }

}
