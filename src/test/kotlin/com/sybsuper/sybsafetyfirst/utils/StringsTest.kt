package com.sybsuper.sybsafetyfirst.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringsTest {
    @Test
    fun toFlatCase() {
        val input = listOf("hello", "world", "test")
        val expected = "helloworldtest"
        assertEquals(expected, input.toFlatCase())
    }

    @Test
    fun fromCamelCase() {
        val input = "camelCaseString"
        val expected = listOf("camel", "case", "string")
        assertEquals(expected, input.fromCamelCase())
    }

    @Test
    fun toCamelCase() {
        val input = listOf("camel", "case", "string")
        val expected = "camelCaseString"
        assertEquals(expected, input.toCamelCase())
    }

    @Test
    fun fromPascalCase() {
        val input = "PascalCaseString"
        val expected = listOf("pascal", "case", "string")
        assertEquals(expected, input.fromCamelCase())
    }

    @Test
    fun toPascalCase() {
        val input = listOf("pascal", "case", "string")
        val expected = "PascalCaseString"
        assertEquals(expected, input.toPascalCase())
    }

    @Test
    fun fromSnakeCase() {
        val input = "snake_case_string"
        val expected = listOf("snake", "case", "string")
        assertEquals(expected, input.fromSnakeCase())
    }

    @Test
    fun toSnakeCase() {
        val input = listOf("snake", "case", "string")
        val expected = "snake_case_string"
        assertEquals(expected, input.toSnakeCase())
    }

    @Test
    fun fromKebabCase() {
        val input = "kebab-case-string"
        val expected = listOf("kebab", "case", "string")
        assertEquals(expected, input.fromKebabCase())
    }

    @Test
    fun toKebabCase() {
        val input = listOf("kebab", "case", "string")
        val expected = "kebab-case-string"
        assertEquals(expected, input.toKebabCase())
    }

    @Test
    fun toFlatCase_emptyList() {
        val input = emptyList<String>()
        val expected = ""
        assertEquals(expected, input.toFlatCase())
    }

    @Test
    fun fromCamelCase_singleWord() {
        val input = "word"
        val expected = listOf("word")
        assertEquals(expected, input.fromCamelCase())
    }

    @Test
    fun toCamelCase_singleWord() {
        val input = listOf("word")
        val expected = "word"
        assertEquals(expected, input.toCamelCase())
    }

    @Test
    fun fromPascalCase_emptyString() {
        val input = ""
        val expected = emptyList<String>()
        assertEquals(expected, input.fromPascalCase())
    }

    @Test
    fun toPascalCase_emptyList() {
        val input = emptyList<String>()
        val expected = ""
        assertEquals(expected, input.toPascalCase())
    }

    @Test
    fun fromSnakeCase_emptyString() {
        val input = ""
        val expected = listOf("")
        assertEquals(expected, input.fromSnakeCase())
    }

    @Test
    fun toSnakeCase_emptyList() {
        val input = emptyList<String>()
        val expected = ""
        assertEquals(expected, input.toSnakeCase())
    }

    @Test
    fun fromKebabCase_emptyString() {
        val input = ""
        val expected = listOf("")
        assertEquals(expected, input.fromKebabCase())
    }

    @Test
    fun toKebabCase_emptyList() {
        val input = emptyList<String>()
        val expected = ""
        assertEquals(expected, input.toKebabCase())
    }
}