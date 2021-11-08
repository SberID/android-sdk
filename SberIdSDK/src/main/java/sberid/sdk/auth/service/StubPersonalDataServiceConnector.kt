package sberid.sdk.auth.service

import android.content.ComponentName
import android.os.IBinder

/**
 * Стабовая реализация сервиса персонализации кнопки авторизации по Сбер ID
 *
 * @author Лелюх Александр
 */
class StubPersonalDataServiceConnector : ServiceConnectionWrapper() {

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        // do nothing
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        // do nothing
    }
}