package controller

import domain.User
import io.javalin.http.Context
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import service.UserService


class UserControllerTest : FunSpec({

    val userService = mockk<UserService>(relaxed = true)
    val ctx = mockk<Context>(relaxed = true)
    val user = User(1, "john@test.com", password = "Pass@4321")
    val userController = UserController(userService)

    test("user controller register new user and return it")
    {
        every { ctx.body<User>() } returns user
        every { userService.create(any<User>()) } returns user.id
        every { ctx.json(user) } returns ctx
        every { ctx.status() } returns HttpStatus.CREATED_201

        userController.register(ctx) shouldBe user

        verify { ctx.body<User>() }
        verify { ctx.json(any<User>()) }
        verify { ctx.status(HttpStatus.CREATED_201) }
        verify { userService.create(any<User>()) }

    }

    test("user controller get user By email")
    {
        every { ctx.attribute<String>("email") } returns user.email
        every { userService.getByEmail(any<String>()) } returns user
        every { ctx.json(user) } returns ctx

        userController.getCurrentUser(ctx) shouldBe user

        verify { ctx.attribute<String>(any<String>()) }
        verify { ctx.json(any<User>()) }
        verify { ctx.status(HttpStatus.OK_200) }
        verify { userService.getByEmail(any<String>()) }

    }

    test("user controller update user and return updated user")
    {
        every { userService.update(any<String>(), any<User>()) } returns user
        every { ctx.body<User>() } returns user
        every { ctx.json(user) } returns ctx
        every { ctx.attribute<String>("email") } returns user.email

        userController.updateUser(ctx) shouldBe user

        verify { ctx.attribute<String>(any<String>()) }
        verify { ctx.body<User>() }
        verify { ctx.json(any<User>()) }
        verify { ctx.status(HttpStatus.OK_200) }
        verify { userService.update(any<String>(), any<User>()) }
    }

    test("user controller delete user")
    {
        every { userService.delete(user.email) } just runs
        every { ctx.pathParam<String>("email").get() } returns user.email
        userController.deleteUser(ctx).shouldBeSameInstanceAs(Unit)
        verify { ctx.pathParam<String>(any<String>()) }
        verify { userService.delete(any<String>()) }
    }

    afterTest {
        confirmVerified(ctx)
        confirmVerified(userService)
    }
})
