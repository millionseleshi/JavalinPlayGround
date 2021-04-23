/*
 * @Author Million Seleshi
 *  2021.
 */

package exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.javalin.Javalin
import io.javalin.http.*
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.exceptions.ExposedSQLException

data class ErrorResponse(val errors: Map<String, List<String?>>)
data class ValidationError(val errors: List<String>)

object ExceptionMapper {
    fun register(app: Javalin) {
        app.exception(Exception::class.java) { e, ctx ->
            val error = ErrorResponse(mapOf("Unknown Error" to listOf(e.message ?: "Error occurred!")))
            ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }
        app.exception(ExposedSQLException::class.java) { _, ctx ->
            val error = ErrorResponse(mapOf("Unknown Error" to listOf("Error occurred!")))
            ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }
        app.exception(BadRequestResponse::class.java) { _, ctx ->
            val error = ErrorResponse(mapOf("body" to listOf("can't be empty or invalid")))
            ctx.json(error).status(HttpStatus.UNPROCESSABLE_ENTITY_422)
        }
        app.exception(UnauthorizedResponse::class.java) { _, ctx ->
            val error = ErrorResponse(mapOf("login" to listOf("User not authenticated!")))
            ctx.json(error).status(HttpStatus.UNAUTHORIZED_401)
        }
        app.exception(ForbiddenResponse::class.java) { _, ctx ->
            val error =
                ErrorResponse(mapOf("login" to listOf("User doesn't have permissions to perform the action!")))
            ctx.json(error).status(HttpStatus.FORBIDDEN_403)
        }
        app.exception(NotFoundResponse::class.java) { _, ctx ->
            val error = ErrorResponse(mapOf("body" to listOf("Resource can't be found to fulfill the request.")))
            ctx.json(error).status(HttpStatus.NOT_FOUND_404)
        }
        app.exception(HttpResponseException::class.java) { e, ctx ->
            val error = ErrorResponse(mapOf("body" to listOf(e.message)))
            ctx.json(error).status(e.status)
        }
        app.exception(JsonParseException::class.java) { _, ctx ->
            val error = ErrorResponse(mapOf("body" to listOf("unable to parse")))
            ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }
    }
}