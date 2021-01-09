package com.rommellaranjo.letsmultiply.utils

class Validator {

    public fun isAlphaNumeric(str: String) : Boolean {
        return str.matches("^[a-zA-Z0-9]*$".toRegex())
    }

    public fun isCorrectLength(str: String, len: Int) : Boolean {
        if (str.length in 1..len) {
            return true
        }
        return false
    }
}