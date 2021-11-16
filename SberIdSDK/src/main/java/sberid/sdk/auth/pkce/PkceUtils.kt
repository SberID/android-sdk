package sberid.sdk.auth.pkce

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

/**
 * Класс с инструментами для работы со Сбер ID
 *
 * @author Gerasimenko Nikita
 */
class PkceUtils {
    companion object {
        private const val CODE_CHALLENGE_METHOD = "S256"
        private const val ALGORITHM = "SHA-256"
        private const val CHARSET_NAME = "ISO_8859_1"
        private const val MIN_CODE_VERIFIER_ENTROPY = 32
        private const val PKCE_BASE64_ENCODE_SETTINGS =
            Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE

        @JvmStatic
        fun getCodeChallengeMethod(): String {
            return CODE_CHALLENGE_METHOD
        }

        @JvmStatic
        fun generateRandomCodeVerifier(entropySource: SecureRandom): String {
            val randomBytes = ByteArray(MIN_CODE_VERIFIER_ENTROPY)
            entropySource.nextBytes(randomBytes)
            return Base64.encodeToString(
                randomBytes,
                PKCE_BASE64_ENCODE_SETTINGS
            )
        }

        @JvmStatic
        fun deriveCodeVerifierChallenge(codeVerifier: String): String {
            return try {
                val sha256Digester = MessageDigest.getInstance(ALGORITHM)
                sha256Digester.update(codeVerifier.toByteArray(charset(CHARSET_NAME)))
                val digestBytes = sha256Digester.digest()
                Base64.encodeToString(
                    digestBytes,
                    PKCE_BASE64_ENCODE_SETTINGS
                )
            } catch (e: NoSuchAlgorithmException) {
                codeVerifier
            } catch (e: UnsupportedEncodingException) {
                throw IllegalStateException("ISO-8859-1 encoding not supported", e)
            }
        }
    }
}