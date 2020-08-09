package com.saifu.controller

import com.saifu.*
import com.saifu.model.ZaimSession
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

fun Route.login(zaimClient: ZaimClient) {
    get<Login> {
        val session = call.sessions.get<ZaimSession>()

        if (session != null) {
            call.respondRedirect(application.locations.href(Index()))
        } else {
            // move to zaim
            call.respondRedirect(zaimClient.retrieveRequestToken())
        }
    }
}

fun Route.loginCallback(zaimClient: ZaimClient) {
    get<LoginCallback> {
        val session = call.sessions.get<ZaimSession>()

        if (session != null) {
            call.respondRedirect(application.locations.href(Index()))
        } else {
            // callback
            val oauthVerifier = call.request.queryParameters["oauth_verifier"]
            zaimClient.retrieveAccessToken(oauthVerifier)

            // save to session
            val (token, tokenSecret) = zaimClient.principal
            call.sessions.set(ZaimSession(token, tokenSecret))
            call.respondRedirect(application.locations.href(Index()))
        }
    }
}
