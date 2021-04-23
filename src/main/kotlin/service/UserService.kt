/*
 * @Author Million Seleshi
 *  2021.
 */

package service

import domain.User
import io.javalin.http.HttpResponseException
import io.javalin.http.NotFoundResponse
import org.eclipse.jetty.http.HttpStatus
import repository.UserRepository

class UserService(private val userRepository: UserRepository) {
    fun create(user: User): Long? {
            (userRepository.findByEmail(user.email)).takeIf { it != null }?.apply {
                throw HttpResponseException(HttpStatus.BAD_REQUEST_400, "Account Exist")
            }
            return userRepository.create(user)
    }

    fun getByEmail(email: String?): User {
        email ?: throw HttpResponseException(HttpStatus.NOT_ACCEPTABLE_406,"email is required")
        return userRepository.findByEmail(email) ?: throw NotFoundResponse("user not found")
    }

    fun update(email: String?, user: User): User? {
        email ?: throw HttpResponseException(HttpStatus.NOT_ACCEPTABLE_406,"email is required")
        return userRepository.update(email, user) ?: throw NotFoundResponse("user not found")
    }

    fun delete(email: String) {
        userRepository.delete(email)
    }


}