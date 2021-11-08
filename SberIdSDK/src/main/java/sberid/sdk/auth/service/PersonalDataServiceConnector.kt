package sberid.sdk.auth.service

import android.content.ComponentName
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import sberid.sdk.auth.view.NameChangeListenerImpl
import sberid.sdk.auth.view.SberIDButton
import java.lang.ref.WeakReference

/**
 * Класс отвечающий за вызов внешнего сервиса МП Сбол, для получения данных для персонализации
 * кнопки
 *
 * @constructor
 * @param sberIDButton экземпляр кнопки, имя которой будем изменять, после ответа сервера
 * @author Gerasimenko Nikita
 */
class PersonalDataServiceConnector(sberIDButton: SberIDButton) : ServiceConnectionWrapper() {

    /**
     * Использучется для вызова публичного сервиса на стороне МП Сбол
     */
    private var mService: Messenger? = null

    /**
     * Используется для обработки ответа от сервиса МП Сбол.
     */
    private val mMessenger: Messenger = Messenger(
        SberIDPersonalDataHandler(
            NameChangeListenerImpl(WeakReference(sberIDButton))
        )
    )

    override fun onServiceDisconnected(name: ComponentName?) {
        isBound = false
        mService = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        isBound = true
        mService = Messenger(service)
        val msg = Message.obtain()
        msg.replyTo = mMessenger
        try {
            mService?.send(msg)
        } catch (e: Exception) {
            // На самсунгах начиная с Api 9 версии, после авторизации выбрасывается DeadObjectException
            // Скорее всего ОС убивает процесс, но наша кнопка все равно пытается вызвать сервис
            Log.e(TAG, "onServiceConnected: ", e)
        }
    }

    private companion object {
        private const val TAG = "PersonalDataService"
    }
}