package com.example.florida.extencions

fun String.onlyDigits(maxLength: Int? = null): String {
    val digits = filter(Char::isDigit)
    return maxLength?.let { digits.take(it) } ?: digits
}

fun String.normalizeCpfCnpjInput(): String = onlyDigits(maxLength = 14)

fun String.normalizePhoneInput(): String = onlyDigits(maxLength = 11)

fun String.cpfCnpjTranformer(): String {
    val digits = filter { it.isDigit() }

    return when (digits.length) {
        11 -> digits.replace(
            Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"),
            "$1.$2.$3-$4"
        )

        14 -> digits.replace(
            Regex("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})"),
            "$1.$2.$3/$4-$5"
        )

        else -> this
    }
}

fun String.phoneTranformer(): String {
    val digits = filter(Char::isDigit)

    return when (digits.length) {
        10 -> { // Fixo
            "(${digits.substring(0, 2)}) " +
                    "${digits.substring(2, 6)}-" +
                    digits.substring(6)
        }

        11 -> { // Celular
            "(${digits.substring(0, 2)}) " +
                    "${digits.substring(2, 3)} " +
                    "${digits.substring(3, 7)}-" +
                    digits.substring(7)
        }

        else -> this
    }
}
