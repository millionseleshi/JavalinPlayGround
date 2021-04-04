package service

import domain.User
import io.javalin.http.HttpResponseException
import io.javalin.http.NotFoundResponse
import org.eclipse.jetty.http.HttpStatus
import repository.UserRepository

class UserService(private val userRepository: UserRepository) {
    fun create(user: User): Long? {
        (userRepository.findByEmail(user.email)).takeIf { it != null }?.apply {
            throw HttpResponseException(HttpStatus.BAD_REQUEST_400, "Account exist")
        }
        return userRepository.create(user)
    }

    fun getByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw NotFoundResponse("user not found")
    }

    fun update(email: String, user: User): User? {
        return userRepository.update(email, user) ?: throw NotFoundResponse("user not updated")
    }

    fun delete(email: String) {
        userRepository.delete(email)
    }


}