package sberid.sdk.auth.login

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import sberid.sdk.auth.R
import sberid.sdk.auth.analytics.SberIDAnalyticsPluginProvider
import sberid.sdk.auth.login.AuthApp.SBOL_APP
import sberid.sdk.auth.login.SberIDLoginManager.Companion.SberIDBuilder
import sberid.sdk.auth.model.SberIDResultModel
import sberid.sdk.auth.utils.SberIDCustomTabsUtils
import java.util.UUID

/**
 * Класс отвечает за подготовку диплинка, открытие МП СБОЛ для авторизации по Сбер ID и
 * получение результата авторизации
 *
 * Для подготовки диплинка используйте класс [SberIDBuilder]
 *
 * @author Gerasimenko Nikita
 */
class SberIDLoginManager {

    companion object {

        private const val NO_FLAG = 0

        private const val SCHEME_WEB = "https"

        private const val HOST_WEB = "online.sberbank.ru"

        private const val PATH_WEB = "CSAFront/oidc/authorize.do"

        private const val SCHEME_MP = "sberbankidlogin"

        private const val HOST_MP = "sberbankid"

        private const val RESPONSE_TYPE = "response_type"

        private const val CLIENT_ID = "client_id"

        private const val STATE = "state"

        private const val NONCE = "nonce"

        private const val SCOPE = "scope"

        private const val REDIRECT_URI = "redirect_uri"

        private const val CODE_CHALLENGE = "code_challenge"

        private const val CODE_CHALLENGE_METHOD = "code_challenge_method"

        private const val LOG_UID = "log_uid"

        private const val ERROR_CODE = "error_code"

        private const val AUTH_CODE = "code"

        private const val ERROR_DESCRIPTION = "error"

        private const val RESPONSE_TYPE_VALUE = "code"

        private const val STATE_ERROR = "invalid_state"

        private const val BASE_ERROR = "internal_error"

        private const val SBER_ID_SSO_REDIRECT_KEY = "sberIDRedirect"

        private const val CUSTOM_TABS_REDIRECT_URI_KEY = "app_redirect_uri"

        private const val AUTH_APP_KEY = "authApp"

        private const val DASH = "-"

        /**
         * Получить билдер для создания uri авторизации по Сбер ID
         */
        fun sberIDBuilder(): SberIDBuilder {
            return SberIDBuilder()
        }

        /**
         * Билдер для создания uri авторизации по Сбер ID
         */
        class SberIDBuilder {

            private lateinit var clientID: String
            private lateinit var scope: String
            private lateinit var state: String
            private lateinit var nonce: String
            private lateinit var redirectUri: String

            private var customTabRedirectUri: String? = null
            private var authApp: String? = null
            private var customUriScheme: String? = null
            private var codeChallenge: String? = null
            private var codeChallengeMethod: String? = null

            /**
             * Кастомная схема диплинка авторизации по Сбер ID в формате app://host,
             * используется к примеру при бесшовной авторизации по Сбер ID
             * Для стандартной авторизации по кнопке не требуется
             *
             * @param uriScheme схема диплинка авторизации по Сбер ID в формате app://host
             */
            fun uriScheme(uriScheme: String?): SberIDBuilder {
                this.customUriScheme = uriScheme
                return this
            }

            /**
             * Идентификатор, полученный при регистрации партнера в личном кабинете Сбер ID
             *
             * @param clientID
             */
            fun clientID(clientID: String): SberIDBuilder {
                this.clientID = clientID
                return this
            }

            /**
             * Список передаваемых данных на которые подписана ваше приложение, указывать
             * стоит через пробел, например "openid previous_name phones". Значение openid
             * является обязательным и располагается на первой позиции.
             *
             * @param scope строка со списком передаваемых данных
             */
            fun scope(scope: String): SberIDBuilder {
                this.scope = scope
                return this
            }

            /**
             * Для предотвращения подделки общения между приложениями используется
             * генерируемое случайным образом уникальное значение.
             *
             * @param state значение параметра
             */
            fun state(state: String): SberIDBuilder {
                this.state = state
                return this
            }

            /**
             * Это значение обычно представляет собой случайную уникальную строку или
             * глобальный уникальный идентификатор, которые можно использовать для
             * определения источника запроса. Ограничение по длине значения составляет
             * 64 символа.
             *
             * @param nonce значение параметра
             */
            fun nonce(nonce: String): SberIDBuilder {
                this.nonce = nonce
                return this
            }

            /**
             * Адрес, куда будет перенаправлен клиент после окончания аутентификации.
             * Возврат происходит и при сценарии с ошибкой.
             *
             * @param redirectUri строка с редиректом после аутентификации
             */
            fun redirectUri(redirectUri: String): SberIDBuilder {
                this.redirectUri = redirectUri
                return this
            }

            /**
             * Адрес "фейковой" активити, куда будет перенаправлен клиент после окончания идентификации для продолжения
             * авторизаци в том же самом CustomTab, в котором запустился процесс входа.
             * Возврат происходит и при сценарии с ошибкой.
             *
             * <b>Не нужен для обычного входа</b>
             *
             * @param customTabRedirectUri строка с редиректом после аутентификации
             */
            fun customTabRedirectUri(customTabRedirectUri: String, context: Context): SberIDBuilder {
                if (!checkSbolIsNotInstalled(context)) {
                    this.customTabRedirectUri = customTabRedirectUri
                    this.authApp = SBOL_APP.keyApp
                }
                return this
            }

            /**
             * Хэшированное значение секретного кода code_verifier партнера.
             * Хэширование выполняется методом, указанным в code_challenge_method.
             * code_challenge = BASE64URL- ENCODE (SHA256 (ASCII (code_verifier)))).
             *
             * @param codeChallenge значение кода code_verifier партнера
             */
            fun codeChallenge(codeChallenge: String): SberIDBuilder {
                this.codeChallenge = codeChallenge
                return this
            }

            /**
             * Метод преобразования, который был применен к коду проверки (code_verifier),
             * для получения значения кода вызова (code_challenge)
             *
             * @param codeChallengeMethod метод преобразования code_verifier
             */
            fun codeChallengeMethod(codeChallengeMethod: String): SberIDBuilder {
                this.codeChallengeMethod = codeChallengeMethod
                return this
            }

            fun build(): Uri {
                var uri = when {
                    customUriScheme != null -> {
                        Uri.parse(customUriScheme)
                    }
                    customTabRedirectUri != null -> {
                        Uri.Builder()
                            .scheme(SCHEME_WEB)
                            .authority(HOST_WEB)
                            .appendEncodedPath(PATH_WEB)
                            .appendQueryParameter(RESPONSE_TYPE, RESPONSE_TYPE_VALUE)
                            .appendQueryParameter(CUSTOM_TABS_REDIRECT_URI_KEY, customTabRedirectUri)
                            .appendQueryParameter(AUTH_APP_KEY, authApp)
                            .build()
                    }
                    else -> {
                        Uri.Builder()
                            .scheme(SCHEME_MP)
                            .authority(HOST_MP)
                            .build()
                    }
                }
                uri = uri
                    .buildUpon()
                    .appendQueryParameter(CLIENT_ID, clientID)
                    .appendQueryParameter(SCOPE, scope)
                    .appendQueryParameter(STATE, state)
                    .appendQueryParameter(NONCE, nonce)
                    .appendQueryParameter(REDIRECT_URI, redirectUri)
                    .appendQueryParameter(LOG_UID, createLogUid())
                    .build()
                if (codeChallenge != null && codeChallengeMethod != null) {
                    uri = uri
                        .buildUpon()
                        .appendQueryParameter(CODE_CHALLENGE, codeChallenge)
                        .appendQueryParameter(CODE_CHALLENGE_METHOD, codeChallengeMethod)
                        .build()
                }
                return uri
            }
        }

        private fun checkSbolIsNotInstalled(context: Context): Boolean {
            val deeplinkUri = Uri.Builder().scheme(SCHEME_MP).authority(HOST_MP).build()
            return context.packageManager.queryIntentActivities(
                Intent(
                    Intent.ACTION_VIEW,
                    deeplinkUri
                ), NO_FLAG
            ).isEmpty()
        }

        private fun createLogUid() = UUID
            .randomUUID()
            .toString()
            .replace(DASH, "")
    }

    private val analyticsPlugin = SberIDAnalyticsPluginProvider.getInstance()

    private var stateApp: String? = null
    private var nonceApp: String? = null

    /**
     * Получить схему диплинка в формате app://host для авторизации по Сбер ID
     * из исходного Uri при переходе в МП партнера в сценарии бесшовной авторизаии
     * Полученное значение необходимо передать в метод [SberIDBuilder.uriScheme] при построении диплинка на авторизацию
     *
     * @param uri исходный Uri при переходе в МП партнера при бесшовной авторизации
     */
    fun getSeamlessUriScheme(uri: Uri) = uri.getQueryParameter(SBER_ID_SSO_REDIRECT_KEY)

    /**
     * Запуск аутентификации через Сбер ID. Если МП Сбол не установленно, запустится web версия.
     * Если бэк присылает готовый uri или вы хотите сами обработать создание его.
     *
     * @param context для получения информации о МП Сбол и старте активити
     * @param uri     URI с данными для запуска активити
     *
     * @return `true` если получилось начать авторизацию, `false` если нет приложения, МП Сбол или браузеров на
     * устройстве
     */
    fun loginWithSberbankID(context: Context, uri: Uri): Boolean {
        stateApp = uri.getQueryParameter(STATE)
        nonceApp = uri.getQueryParameter(NONCE)

        val intent = Intent(Intent.ACTION_VIEW, checkUri(uri, context))
        if (checkSbolIsNotInstalled(context)) {
            SberIDCustomTabsUtils.launchCustomTabs(context, checkUri(uri, context)).let {
                try {
                    if (!it) context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, context.getText(R.string.browser_not_found_toast), Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
            }
        } else {
            context.startActivity(intent)
        }
        analyticsPlugin.sberIDButtonClick()
        return true
    }

    /**
     * <h3>Внимание</h3>
     * Если вам нужна обычная авторизация по Сбер ID используйте [loginWithSberbankID].
     *
     * Запускает аутентификации через Сбер ID в CustomTabs. С последующей авторизацией через web страницу Сбер ID.
     *
     * @param context для получения информации о МП Сбол и старте активити
     * @param uri     URI с данными для запуска активити
     *
     * @return `true` запуск успешен, `false` - не найден браузер, поддерживающий CustomTabs
     */
    fun loginWithSberIDToCustomTabs(context: Context, uri: Uri): Boolean {
        stateApp = uri.getQueryParameter(STATE)
        nonceApp = uri.getQueryParameter(NONCE)

        return SberIDCustomTabsUtils.launchCustomTabs(context, uri)
    }

    /**
     * Метод для получения результата аутентификации через Сбер ID.
     *
     * @param intent содержит данные ответа от Сбер ID
     * @return [SberIDResultModel] - Сущность ответа от Сбер ID.
     */
    fun getSberIDAuthResult(intent: Intent): SberIDResultModel {
        val sberIDResultModel = SberIDResultModel()
        if (intent.data == null) {
            return sberIDResultModel
        }
        val state = intent.data!!.getQueryParameter(STATE)
        sberIDResultModel.isSuccess = isSuccess(intent) && checkState(state)
        if (sberIDResultModel.isSuccess!!) {
            sberIDResultModel.state = state
            sberIDResultModel.nonce = nonceApp
            sberIDResultModel.authCode = intent.data!!.getQueryParameter(AUTH_CODE)
            analyticsPlugin.sberIDAuthResult()
        } else {
            sberIDResultModel.errorDescription = getErrorText(intent.data!!)
            sberIDResultModel.errorCode = intent.data!!.getQueryParameter(ERROR_CODE)
            analyticsPlugin.sberIDAuthResult(
                sberIDResultModel.errorCode?.plus("_").plus(sberIDResultModel.errorDescription)
            )
        }
        stateApp = null
        nonceApp = null
        return sberIDResultModel
    }

    private fun checkUri(uri: Uri, context: Context): Uri {
        var deeplinkUri = uri
        if (checkSbolIsNotInstalled(context)) {
            deeplinkUri = uri
                .buildUpon()
                .scheme(SCHEME_WEB)
                .authority(HOST_WEB)
                .appendEncodedPath(PATH_WEB)
                .appendQueryParameter(RESPONSE_TYPE, RESPONSE_TYPE_VALUE)
                .build()
        }
        return deeplinkUri
    }

    private fun isSuccess(intent: Intent): Boolean {
        return intent.data != null
            && intent.data!!.getQueryParameter(AUTH_CODE) != null
    }

    private fun checkState(state: String?): Boolean {
        return stateApp.equals(state)
    }

    private fun getErrorText(data: Uri): String? {
        val error = data.getQueryParameter(ERROR_DESCRIPTION)
        if (error != "null") {
            return data.getQueryParameter(ERROR_DESCRIPTION)
        } else if (checkState(data.getQueryParameter(STATE))) {
            return STATE_ERROR
        }
        return BASE_ERROR
    }
}

