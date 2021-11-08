package sberid.sdk.auth.analytics

import android.content.Context
import androidx.annotation.WorkerThread
import sberid.sdk.auth.model.SberIDButtonDesignModel

/**
 * Интерфейс аналитического плагина событий для кнопки входа по Сбер ID
 * Все действия, связанные с обработкой событий, происходят в фоновом потоке во избежание блокировок UI
 *
 * @author Лелюх Александр
 */
@WorkerThread
interface ISberIDAnalyticsPlugin {

    /**
     * Инициализировать аналитический плагин, метод должен быть вызван первым
     * сразу после показа кнопки входа по Сбер ID, иначе события не будут отправлены
     *
     * @param appContext контекст приложения
     */
    fun initPlugin(appContext: Context)

    /**
     * Показана кнопка входа по Сбер ID
     *
     * @param designModel модель дизайна кнопки входа по Сбер ID
     */
    fun sberIDButtonShow(designModel: SberIDButtonDesignModel)

    /**
     * Кнопка входа по Сбер ID имеет ширину меньше допустимой
     *
     * @param measuredWidth минимально допустимая расчетная ширина кнопки в пикселях
     * @param realWidth     реальная ширина кнопки в пикселях
     */
    fun sberIDWrongButtonSize(measuredWidth: Int, realWidth: Int)

    /**
     * Нажата кнопка входа по Сбер ID
     */
    fun sberIDButtonClick()

    /**
     * Получен результат авторизации по Сбер ID
     *
     * @param errorMessage данные об ошибке при авторизации, в случае успеха будет null
     */
    fun sberIDAuthResult(errorMessage: String? = null)
}