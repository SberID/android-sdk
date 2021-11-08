package sberid.sdk.auth.login

/**
 * Содержит ключ приложеий, которые могут авторизовать пользователя
 *
 * @property keyApp ключ приложения
 *
 * @author Gerasimenko Nikita
 */
internal enum class AuthApp(val keyApp: String) {
    SBOL_APP("sbol")
}