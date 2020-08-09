package com.saifu

import com.saifu.model.ZaimSession
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.URI

class ZaimClient(
    CONSUMER_KEY: String,
    CONSUMER_SECRET: String
) {

    companion object {
        const val REQUEST_TOKEN_URL = "https://api.zaim.net/v2/auth/request"
        const val AUTHORIZE_URL = "https://auth.zaim.net/users/auth"
        const val ACCESS_TOKEN_URL = "https://api.zaim.net/v2/auth/access"
        const val CALLBACK_URL = "http://localhost:8080/login/callback"
    }

    // Empty if user are not logged in
    val principal
        get() = Pair(consumer.token, consumer.tokenSecret)

    private val httpClient = HttpClients.createDefault()
    private val responseHandler = APIResponseHandler()

    private val provider = CommonsHttpOAuthProvider(
        REQUEST_TOKEN_URL,
        ACCESS_TOKEN_URL,
        AUTHORIZE_URL
    )
    private val consumer = CommonsHttpOAuthConsumer(
        CONSUMER_KEY,
        CONSUMER_SECRET
    )

    // --- OAuth ---
    // step 1 : get request token & redirect to zaim authentication page
    fun retrieveRequestToken(): String {
        return provider.retrieveRequestToken(consumer, CALLBACK_URL)
    }

    // step 2 : receive oauth_verifier & generate tokens
    fun retrieveAccessToken(oauthVerifier: String?) {
        provider.retrieveAccessToken(consumer, oauthVerifier)
    }

    // --- zaim operations ---
    fun getUserVerify(session: ZaimSession): String {
        val uri = URI("https://api.zaim.net/v2/home/user/verify")
        return getRequest(uri, session)
    }

    private fun getRequest(uri: URI, session: ZaimSession): String {
        val httpGet = HttpGet(uri)

        consumer.setTokenWithSecret(session.accessToken, session.accessTokenSecret)
        consumer.sign(httpGet)

        return httpClient.execute(httpGet, responseHandler)
    }
}

// @see https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientWithResponseHandler.java
class APIResponseHandler : ResponseHandler<String> {
    override fun handleResponse(response: HttpResponse?): String? {
        val status = response?.statusLine?.statusCode
        return if (status != null) {
            if (status in 200..299) {
                val entity = response.entity
                entity?.let { EntityUtils.toString(entity) }
            } else {
                throw ClientProtocolException("Unexpected response status: $status")
            }
        } else {
            null
        }
    }
}
