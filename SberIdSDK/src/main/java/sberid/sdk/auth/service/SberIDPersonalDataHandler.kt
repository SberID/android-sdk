package sberid.sdk.auth.service

import android.os.Handler
import android.os.Looper
import android.os.Message
import sberid.sdk.auth.view.NameChangeListenerImpl

/**
 * Класс для обработки обратного сообщения с маскированным именем от сервиса приложения МП Сбол.
 *
 * [Looper] указываем явно `Looper.getMainLooper()` чтобы избежать ошибок, когда операции незаметно теряются
 * (если Обработчик не ожидает новых задач и завершает работу). Используем MainLooper, так как общение идет
 * в главном потоке, у которого уже есть [Looper]
 *
 * @constructor
 * @param nameChangeListener слушатель для изменения имени в UI кнопки
 * @author Gerasimenko Nikita
 */
class SberIDPersonalDataHandler(private val nameChangeListener: NameChangeListenerImpl) :
    Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message) {
        nameChangeListener.onNameChange(msg.data.getString(MASK_NAME_KEY))
    }

    companion object {
        private const val MASK_NAME_KEY = "mask_name"
    }
}