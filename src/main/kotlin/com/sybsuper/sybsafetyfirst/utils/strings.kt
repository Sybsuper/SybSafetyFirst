package com.sybsuper.sybsafetyfirst.utils

typealias SplitString = List<String>

fun SplitString.toFlatCase(): String = joinToString(separator = "").lowercase()

fun String.fromCamelCase(): SplitString {
    val result = mutableListOf<String>()
    var currentWord = StringBuilder()

    for (char in this) {
        if (char.isUpperCase() && currentWord.isNotEmpty()) {
            result.add(currentWord.toString())
            currentWord = StringBuilder()
        }
        currentWord.append(char)
    }
    if (currentWord.isNotEmpty()) {
        result.add(currentWord.toString())
    }

    return result.map { it.lowercase() }
}

fun SplitString.toCamelCase(): String =
    this.first().lowercase() +
            this.drop(1).joinToString(separator = "") { it.replaceFirstChar { c -> c.uppercase() } }

fun String.fromPascalCase(): SplitString = this.fromCamelCase()
fun SplitString.toPascalCase(): String =
    this.joinToString(separator = "") { it.replaceFirstChar { c -> c.uppercase() } }

fun String.fromSnakeCase(): SplitString = this.split('_').map { it.lowercase() }
fun SplitString.toSnakeCase(): String = this.joinToString(separator = "_")
fun String.fromKebabCase(): SplitString = this.split('-').map { it.lowercase() }
fun SplitString.toKebabCase(): String = this.joinToString(separator = "-")