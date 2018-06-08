package io.github.ranolp.gitoka

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.google.gson.JsonElement
import com.natpryce.konfig.Configuration
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType
import io.github.ranolp.common.parseJson
import org.funktionale.either.Either

class Gitoka(private val config: Configuration) {
    object gitoka : PropertyGroup() {
        val clientSecret by stringType
    }

    fun request(
        url: String,
        param: List<Pair<String, Any?>> = emptyList(),
        block: Request.() -> Unit = {}
    ): Either<JsonElement, FuelError> =
        "https://api.github.com/$url".httpGet(param).also(block).responseString().third.fold({
            Either.left(it.parseJson())
        }, {
            Either.right(it)
        })
}
