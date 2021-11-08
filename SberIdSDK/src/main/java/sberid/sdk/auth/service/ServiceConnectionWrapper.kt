package sberid.sdk.auth.service

import android.content.ServiceConnection

/**
 * Обёртка над [ServiceConnection] для удобной подмены реализаций сервиса персонализации кнопки
 *
 * @author Лелюх Александр
 */
abstract class ServiceConnectionWrapper : ServiceConnection {

    /**
     * Информирует о статусе привязанности сервиса
     * Использучется для вызова публичного сервиса на стороне МП Сбол
     */
    var isBound = false
}