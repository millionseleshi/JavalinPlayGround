/*
 * @Author Million Seleshi
 *  2021.
 */

package util

import arrow.core.Either
import domain.User
import domain.UserDTO
import exception.ValidationError
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import org.valiktor.i18n.mapToMessage
import org.valiktor.validate
import util.PasswordValid.isPasswordValid
import java.util.*

class InputValidation {
    companion object Factory {
        fun validateRegisterInput(input: UserDTO): Either<ValidationError, UserDTO> =
            try {
                Either.Right(validate(input) {
                    validate(input.user!!) {
                        validate(User::email).isEmail()
                        validate(User::password).isPasswordValid()
                    }

                })
            } catch (ex: ConstraintViolationException) {
                Either.Left(
                    ValidationError(ex.constraintViolations
                        .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
                        .map { "${it.property}: ${it.message}" })
                )
            }

        fun validateUpdateInput(input: User): Either<ValidationError, User> =
            try {
                Either.Right(validate(input) {
                    validate(User::email).isEmail()
                    input.username ?: validate(User::username).isNotBlank()
                    input.token ?: validate(User::token).isNotBlank()
                    input.password ?: validate(User::password).isNotBlank()
                })
            } catch (ex: ConstraintViolationException) {
                Either.Left(
                    ValidationError(ex.constraintViolations
                        .mapToMessage(baseName = "messages", locale = Locale.ENGLISH)
                        .map { "${it.property}: ${it.message}" })
                )
            }
    }
}
