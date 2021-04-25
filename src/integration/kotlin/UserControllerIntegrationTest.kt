/*
 * @Author Million Seleshi
 *  2021.
 */

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import config.AppConfig
import domain.User
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.eclipse.jetty.http.HttpStatus
import org.h2.tools.Server
import java.util.*


class UserControllerIntegrationTest : FunSpec({

    val app = AppConfig()
    val baseURL = "http://localhost:7000/api"
    val client = OkHttpClient()

    val mediaType = "application/json; charset=utf-8".toMediaType()

    val userEmail = "jhon@email.com"
    val userName = "jhon"
    val token = UUID.randomUUID().toString()
    val password = "Pass@4321"

    val mapper = jacksonObjectMapper()

    beforeSpec {
        app.setUp().start()
        Server.createWebServer().start()
    }

    test("Post to create new user returns Created 201")
    {
        val requestPostUser = requestFirstPost(baseURL, userEmail, userName, token, password, mediaType)

        client.newCall(requestPostUser).execute()
            .use { response ->
                val user = mapper.readValue(response.body!!.string(), User::class.java)
                user.id shouldNotBe null
                user.email shouldBe userEmail
                user.username shouldBe userName
                user.token shouldBe token
                user.password shouldBe password
                response.code shouldBe HttpStatus.CREATED_201
                response.header("content-type") shouldBe "application/json"
            }
    }

    test("Post with duplicate email return Error message Account exist")
    {
        val requestPostUserOne =
            requestFirstPost(baseURL, userEmail, userName, token, password, mediaType)

        client.newCall(requestPostUserOne).execute()

        client.newCall(requestPostUserOne).execute()
            .use { response ->
                response.body?.string() shouldContainIgnoringCase ("Account Exist")
                response.code shouldBe HttpStatus.BAD_REQUEST_400
            }
    }

    test("Post with invalid email return BAD REQUEST 400")
    {
        val requestPostUser = requestFirstPost(baseURL, "joe", userName, token, password, mediaType)
        client.newCall(requestPostUser).execute()
            .use { response ->
                response.code shouldBe HttpStatus.BAD_REQUEST_400
                response.header("content-type") shouldBe "application/json"
            }
    }

    test("Post with weak password return BAD REQUEST 400")
    {
        val requestPostUser = requestFirstPost(baseURL, userEmail, userName, token, "pass", mediaType)
        client.newCall(requestPostUser).execute()
            .use { response ->
                response.code shouldBe HttpStatus.BAD_REQUEST_400
                response.header("content-type") shouldBe "application/json"
            }
    }

    test("Put with existing user email return  OK 200")
    {
        createUser(baseURL, userEmail, userName, token, password, mediaType, client)

        val requestPutUser = requestPut(baseURL, userEmail, "newUsername", mediaType)
        client.newCall(requestPutUser).execute().use { response ->
            val user = mapper.readValue(response.body!!.string(), User::class.java)
            user.email shouldBe userEmail
            user.username shouldNotBe userName
            response.code shouldBe HttpStatus.OK_200
            response.header("Content-Type") shouldBe "application/json"
        }
    }

    test("Put with non existing user email return Not Found 404")
    {
        createUser(baseURL, userEmail, userName, token, password, mediaType, client)

        val requestPutUser = requestPut(baseURL, "user@email.com", userName, mediaType)
        client.newCall(requestPutUser).execute().use { response ->
            response.body!!.string() shouldContainIgnoringCase ("Resource can't be found to fulfill the request")
            response.code shouldBe HttpStatus.NOT_FOUND_404
            response.header("Content-Type") shouldBe "application/json"
        }
    }

    test("Put with invalid email return Not Acceptable 406")
    {
        createUser(baseURL, userEmail, userName, token, password, mediaType, client)

        val requestPutUser = requestPut(baseURL, "user", userName, mediaType)
        client.newCall(requestPutUser).execute().use { response ->
            response.code shouldBe HttpStatus.BAD_REQUEST_400
            response.header("Content-Type") shouldBe "application/json"
        }
    }

    test("Get Existing user with email return OK 200")
    {
        createUser(baseURL, userEmail, userName, token, password, mediaType, client)

        val requestGetUser = requestGet(baseURL, userEmail)

        client.newCall(requestGetUser).execute().use { response ->
            val user = mapper.readValue(response.body!!.string(), User::class.java)
            user?.email shouldBe userEmail
            response.code shouldBe HttpStatus.OK_200
            response.header("content-type") shouldBe "application/json"
        }
    }

    test("Get None Existing user with email return Not Found 404")
    {

        val requestGetUser = requestGet(baseURL, "userEmail@email.com")

        client.newCall(requestGetUser).execute().use { response ->
            response.body!!.string() shouldContainIgnoringCase ("Resource can't be found to fulfill the request")
            response.code shouldBe HttpStatus.NOT_FOUND_404
            response.header("content-type") shouldBe "application/json"
        }
    }

    test("Delete Existing user return success message")
    {
        createUser(baseURL, userEmail, userName, token, password, mediaType, client)
        val requestDeleteUser = requestDelete(baseURL, userEmail)
        client.newCall(requestDeleteUser).execute().use { response ->
            response.code shouldBe HttpStatus.OK_200
        }
    }

    afterEach {
        app.stop()
    }
})

private fun createUser(
    baseURL: String,
    userEmail: String,
    userName: String,
    token: String,
    password: String,
    mediaType: MediaType,
    client: OkHttpClient
) {
    val requestPostUser = requestFirstPost(baseURL, userEmail, userName, token, password, mediaType)
    client.newCall(requestPostUser).execute()
}


private fun requestFirstPost(
    baseURL: String,
    userEmail: String,
    userName: String,
    token: String,
    password: String,
    MEDIA_TYPE: MediaType
): Request {
    val postBody = """
                      {"user":{ "email" : "$userEmail","username" : "$userName","token": "$token","password" : "$password"}}
                      """
    return Request.Builder()
        .url("$baseURL/user")
        .post(postBody.toRequestBody(MEDIA_TYPE))
        .build()
}


private fun requestPut(
    baseURL: String,
    userEmail: String,
    userName: String,
    MEDIA_TYPE: MediaType
): Request {
    val putBody = """
      {"user":{ "email" : "$userEmail","username" : "$userName"}}
  """
    return Request.Builder()
        .url("$baseURL/user")
        .put(putBody.toRequestBody(MEDIA_TYPE))
        .build()
}

private fun requestGet(
    baseURL: String,
    userEmail: String,
): Request {
    return Request.Builder()
        .url("$baseURL/user/$userEmail")
        .get()
        .build()
}

private fun requestDelete(
    baseURL: String,
    userEmail: String,
): Request {
    return Request.Builder()
        .url("$baseURL/user/$userEmail")
        .delete()
        .build()
}