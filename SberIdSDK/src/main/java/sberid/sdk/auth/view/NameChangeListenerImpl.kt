package sberid.sdk.auth.view

import sberid.sdk.auth.service.NameChangeListener
import java.lang.ref.WeakReference

/**
 * Слушатель для отображения персональной кнопки
 *
 * @author Gerasimenko Nikita
 */
class NameChangeListenerImpl(private val sberIDButton: WeakReference<SberIDButton>) : NameChangeListener {

    override fun onNameChange(name: String?) {
        sberIDButton.get()?.initMaskNameText(name)
    }

}