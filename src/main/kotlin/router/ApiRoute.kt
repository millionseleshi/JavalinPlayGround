/*
 * @Author Million Seleshi
 *  2021.
 */

package router


import controller.UserController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.koin.core.component.KoinComponent

class ApiRoute(
    private val userController: UserController
) : KoinComponent {

    fun register(app: Javalin) {
        app.routes {
            path("user")
            {
                post(userController::register)
                put(userController::updateUser)
                path(":email") {
                    get(userController::getCurrentUser)
                    delete(userController::deleteUser)
                }

            }
        }
    }

}