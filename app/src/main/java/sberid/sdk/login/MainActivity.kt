package sberid.sdk.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import sberid.sdk.auth.login.SberIDLoginManager
import sberid.sdk.auth.pkce.PkceUtils
import sberid.sdk.auth.view.SberIDButton
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var sberIDButton: SberIDButton

    private var resultTextView: TextView? = null

    private val sberIDLoginManager = SberIDLoginManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.text)

        if (intent.data != null) {
            setResponse(intent)
        }

        sberIDButton = findViewById(R.id.sberid_button)

        sberIDButton.setOnClickListener {
            val codeVerifide = PkceUtils.generateRandomCodeVerifier(SecureRandom())
            val codeChallenge = PkceUtils.deriveCodeVerifierChallenge(codeVerifide)
            val uri = SberIDLoginManager
                .sberIDBuilder()
                .clientID("CC7D5AF2-0830-9B05-FE4D-8B047B968093")
                .scope("openid")
                .state("ffad1d59c1e34844a3499966103d44f3")
                .nonce("b1947d4f10a24eb0a6bb62679be9b066")
                .redirectUri("https://ru.domclick.mortgage.debug")
                .codeChallenge(codeChallenge)
                .codeChallengeMethod(PkceUtils.getCodeChallengeMethod())
                .build()
            sberIDLoginManager.loginWithSberbankID(this, uri)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data != null) {
            setResponse(intent)
        }
    }

    private fun setResponse(intent: Intent) {
        val model = sberIDLoginManager.getSberIDAuthResult(intent)
        val response = StringBuilder()
        response
            .append("\nResult : ")
            .append(model.isSuccess)

        if (model.isSuccess!!) {
            response
                .append("\nAuthCode : ")
                .append(model.authCode)
                .append("\nNonce : ")
                .append(model.nonce)
        } else {
            response
                .append("ErrorDescription : \n")
                .append(model.errorDescription)
                .append("\nCode : ")
                .append(model.errorCode)
        }

        resultTextView!!.text = response.toString()
    }
}
