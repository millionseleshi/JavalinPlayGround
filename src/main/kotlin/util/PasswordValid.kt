package util

import domain.User
import org.valiktor.Constraint
import org.valiktor.Validator
import java.util.regex.Pattern

object PasswordValid : Constraint {

    fun Validator<User>.Property<String?>.isPasswordValid() = this.validate(PasswordValid) {
        isValidPassword(it)
    }

    fun isValidPassword(data: String?): Boolean {
        val str = data.toString()
        var valid = true

        if (str.length < 8) {
            valid = false
        }

        var exp = ".*[0-9].*"
        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        var matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        exp = ".*[A-Z].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        exp = ".*[a-z].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        exp = ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        pattern = Pattern.compile(exp)
        matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }
        return valid
    }
}