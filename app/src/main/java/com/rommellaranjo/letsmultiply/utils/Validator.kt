package com.rommellaranjo.letsmultiply.utils

class Validator {

    public fun isAlphaNumeric(str: String) : Boolean {
        return str.matches("^[a-zA-Z0-9]*$".toRegex())
    }
}