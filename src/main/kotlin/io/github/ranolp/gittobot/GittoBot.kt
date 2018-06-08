package io.github.ranolp.gittobot

import com.github.salomonbrys.kotson.array
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import io.github.ranolp.gitoka.Gitoka

fun main(args: Array<String>) {
    val gitokaConfig = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("settings/gitoka.properties")

    val gitoka = Gitoka(gitokaConfig)

    gitoka.request("users/RanolP/GittoBot/events").left().map {
        it.array.forEach {
            println(it)
        }
    }
}
