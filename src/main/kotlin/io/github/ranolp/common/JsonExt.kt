package io.github.ranolp.common

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser

val GSON = GsonBuilder().disableHtmlEscaping().create()

val GSON_PRETTY = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

val JSON_PARSER = JsonParser()

fun String.parseJson(): JsonElement = JSON_PARSER.parse(this)
