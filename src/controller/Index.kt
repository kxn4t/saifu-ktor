package com.saifu.controller

import com.saifu.Index
import com.saifu.Login
import com.saifu.ZaimClient
import com.saifu.model.ZaimSession
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Route.index(zaimClient: ZaimClient) {
    get<Index> {

        val user = call.sessions.get<ZaimSession>()?.let {
            zaimClient.getUserVerify(it)
        }
        if (user != null) {
            call.respondText("login OK $user", contentType = ContentType.Text.Plain)
        } else {
            call.respondRedirect(application.locations.href(Login()))
        }
    }
}
