/*
 * @Author Million Seleshi
 *  2021.
 */

package controller


import arrow.core.Either
import domain.UserDTO
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import service.UserService
import util.InputValidation.Factory.validateRegisterInput
import util.InputValidation.Factory.validateUpdateInput

class UserController(private val userService: UserService) {


    fun register(ctx: Context) {
        when (val result = validateRegisterInput(ctx.body<UserDTO>())) {
            is Either.Right -> {
                result.value.user.takeIf { it != null }?.also {
                    userService.create(it)
                }?.apply {
                    ctx.json(userService.getByEmail(this.email)).status(HttpStatus.CREATED_201)
                }
            }
            is Either.Left -> {
                ctx.json(result.value.errors).status(HttpStatus.BAD_REQUEST_400)
            }
        }
    }


    fun getCurrentUser(ctx: Context) {
        val userEmail = ctx.pathParam<String>("email").get()
        val user = userService.getByEmail(userEmail)
        ctx.json(user).status(HttpStatus.OK_200)
    }

    fun updateUser(ctx: Context) {
        val userEmail = ctx.body<UserDTO>().user?.email
        when (val result = validateUpdateInput(ctx.body<UserDTO>())) {
            is Either.Right -> {
                result.value.user?.also {
                    userService.update(userEmail, it)?.apply {
                        ctx.json(this).status(HttpStatus.OK_200)
                    }
                }
            }
            is Either.Left -> {
                ctx.json(result.value.errors).status(HttpStatus.BAD_REQUEST_400)
            }
        }
    }

    fun deleteUser(ctx: Context) {
        val userEmail = ctx.pathParam<String>("email").get()
        userService.delete(userEmail)
    }

}
