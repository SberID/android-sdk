/*
 * Используя данное SDK вы соглашаетесь с Пользовательским соглашением, размещенным по адресу:
 * https://www.sberbank.ru/common/img/uploaded/files/sberbank_id/agreement_sdk.pdf
 */
package sberid.sdk.auth.model

/**
 * Модель результата авторизации по Сбер ID
 * @property isSuccess        true если ответ положительный и вход произведен удачно.
 * @property nonce            Значение, сгенерированное внешней АС для предотвращения атак
 *                            повторения.
 * @property state            Значение, сгенерированное внешней АС для предотвращения CSRF атак.
 * @property authCode         Код авторизации клиента.
 * @property errorDescription Текст ошибки, не показывайте клиентам, они только для разработчиков.
 *                            Типы ошибок:
 *                            - invalid_request: В запросе отсутствуют обязательные атрибуты;
 *
 *                            - unauthorized_client: АС-источник запроса не зарегистрирована в
 *                            банке, АС-источник запроса заблокирована в банке, или значение
 *                            атрибута client_id не соответствует формату;
 *
 *                            - unsupported_response_type: Значение атрибута response_type не равно
 *                            «code»;
 *
 *                            - invalid_scope: Значение атрибута scope не содержит параметр openid в
 *                            начальной позиции или запрошенный scope содержит значения, недоступные
 *                            для АС-источника запроса
 * @property errorCode       Приходит только в случае ошибки 5, когда вы неверно передали параметры,
 *                            ошибки описаны выше. В случае неожиданной ошибки, такие как падения
 *                            приложения и тп.
 * @author Gerasimenko Nikita
 */
data class SberIDResultModel @JvmOverloads constructor(
    var isSuccess: Boolean? = null,
    var state: String? = null,
    var nonce: String? = null,
    var authCode: String? = null,
    var errorDescription: String? = null,
    var errorCode: String? = null
)