package sberid.sdk.auth.service

/**
 * Лисенер для обработки события загрузки маскированного имени пользователя.
 *
 * @author Gerasimenko Nikita
 */
interface NameChangeListener {

    /**
     * Вызывается если нужно сменить название кнопки. Вызывать
     * @param name маскированное имя пользователя в виде "Имя Ф."
     */
    fun onNameChange(name: String?)
}