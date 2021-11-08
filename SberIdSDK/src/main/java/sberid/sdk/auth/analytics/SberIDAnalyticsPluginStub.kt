package sberid.sdk.auth.analytics

import android.content.Context
import sberid.sdk.auth.model.SberIDButtonDesignModel

/**
 * Стабовая реализация аналитического плагина событий для кнопки входа по Сбер ID
 *
 * @author Лелюх Александр
 */
class SberIDAnalyticsPluginStub : ISberIDAnalyticsPlugin {
    override fun initPlugin(appContext: Context) {
        // do nothing
    }

    override fun sberIDButtonShow(designModel: SberIDButtonDesignModel) {
        // do nothing
    }

    override fun sberIDWrongButtonSize(measuredWidth: Int, realWidth: Int) {
        // do nothing
    }

    override fun sberIDButtonClick() {
        // do nothing
    }

    override fun sberIDAuthResult(errorMessage: String?) {
        // do nothing
    }
}