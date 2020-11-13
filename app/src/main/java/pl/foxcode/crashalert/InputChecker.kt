package pl.foxcode.crashalert

class InputChecker {

    companion object {
        fun isEmailCorrect(email: String): Boolean {
            return email.contains("@")
        }

        fun isPasswordCorrect(password: String): Boolean {
            val pattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$")
            /**
            ^                 # start-of-string
            (?=.*[0-9])       # a digit must occur at least once
            (?=.*[a-z])       # a lower case letter must occur at least once
            (?=.*[A-Z])       # an upper case letter must occur at least once
            (?=.*[!?@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
            (?=\S+$)         # no whitespace allowed in the entire string
            .{6,}             # anything, at least six places though
            $                 # end-of-string
             */

            return password.contains(pattern)
        }

        fun areStringsTheSame(string1: String, string2: String): Boolean {
            return (string1 == string2)
        }
    }
}