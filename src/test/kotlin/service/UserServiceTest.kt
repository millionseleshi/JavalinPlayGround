/*
 * @Author Million Seleshi
 *  2021.
 */

package service

import domain.User
import io.javalin.http.HttpResponseException
import io.javalin.http.NotFoundResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import repository.UserRepository

class UserServiceTest : FunSpec({

    val userRepository = mockk<UserRepository>(relaxed = true)
    val user = mockk<User>(relaxed = true)
    val userService = UserService(userRepository)

    test("userService create returns new user id if user doesn't exist")
    {
        every { userRepository.findByEmail(user.email) } returns null
        every { userRepository.create(user) } returns user.id
        userService.create(user).shouldBe(user.id)
        verify { userRepository.findByEmail(any<String>()) }
        verify { userRepository.create(any<User>()) }
        confirmVerified(userRepository)
    }
    test("usersService create throws exception if user exist")
    {
        every { userRepository.findByEmail(user.email) } returns user
        val exception = shouldThrow<HttpResponseException> {
            userService.create(user)
        }
        exception.status.shouldBe(HttpStatus.BAD_REQUEST_400)
        exception.message.shouldBe("Account Exist")
        verify { userRepository.findByEmail(any<String>()) }
        confirmVerified(userRepository)
    }

    test("userService getByEmail return a single user if user exist")
    {
        every { userRepository.findByEmail(user.email) } returns user
        userService.getByEmail(user.email).shouldBe(user)
        verify { userRepository.findByEmail(any<String>()) }
        confirmVerified(userRepository)
    }

    test("userService getByEmail throws exception if user doesn't exist")
    {
        every { userRepository.findByEmail(user.email) } returns null
        val exception = shouldThrow<NotFoundResponse> {
            userService.getByEmail(user.email)
        }
        exception.message.shouldBe("user not found")
        verify { userRepository.findByEmail(any<String>()) }
        confirmVerified(userRepository)
    }

    test("userService update return updated user")
    {
        every { userRepository.findByEmail(user.email) } returns user
        every { userRepository.update(user.email, any<User>()) } returns user
        userService.update(user.email, user).shouldBe(user)
        verify { userRepository.update(any<String>(), any<User>()) }
        verify { userRepository.findByEmail(any<String>()) }
        confirmVerified(userRepository)
    }

    test("userService update throws exception if user doesn't exist")
    {
        every { userRepository.update(user.email, user) } returns null
        val exception = shouldThrow<NotFoundResponse> {
            userService.update(user.email, user)
        }
        exception.message.shouldBe("user not found")

        verify { userRepository.update(user.email, user) }

        confirmVerified(userRepository)
    }

    test("userService delete user if user exist")
    {
        every { userRepository.delete(user.email) } just Runs
        userService.delete(user.email).shouldBeInstanceOf<Unit>()
        verify { userRepository.delete(any<String>()) }
        confirmVerified(userRepository)
    }

    afterTest {
        unmockkAll()
    }

})
