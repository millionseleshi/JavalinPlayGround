/*
 * @Author Million Seleshi
 *  2021.
 */

package domain

data class User(
    val id: Long?=null,
    var email: String,
    val token: String? = null,
    val username: String? = null,
    val password: String? = null
)

data class UserDTO(val user: User? = null)