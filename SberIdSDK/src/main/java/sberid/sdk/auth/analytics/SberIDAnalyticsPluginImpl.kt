package sberid.sdk.auth.analytics

import android.content.Context
import android.util.Log
import ru.sberbank.mobile.clickstream.boundary.ISberbankAnalytics
import ru.sberbank.mobile.clickstream.boundary.SberbankAnalytics
import ru.sberbank.mobile.clickstream.meta.AnalyticsMetaCollector
import ru.sberbank.mobile.clickstream.meta.AnalyticsProfileCollector
import ru.sberbank.mobile.clickstream.models.data.SberbankAnalyticsEvent
import sberid.sdk.auth.BuildConfig
import sberid.sdk.auth.model.SberIDButtonDesignModel
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Реализация аналитического плагина событий для кнопки входа по Сбер ID
 * Все действия, связанные с обработкой событий, происходят в фоновом потоке во избежание блокировок UI
 * с использованием SingleThreadExecutor, который гарантирует последовательное исполнение задач
 *
 * При инициализации передается минимально необходимая информация, необходимимя для работоспособности
 * Сбераналитики, в дальнейшем отправка этой информации будет перенесена на уровень базовой логики
 *
 * @author Лелюх Александр
 */
class SberIDAnalyticsPluginImpl : ISberIDAnalyticsPlugin {

    companion object {

        // meta info
        private const val API_KEY = "apiKey"
        private const val SBER_ID_KEY = "sberId"
        private const val PLATFORM_KEY = "platform"
        private const val LANGUAGE_KEY = "systemLanguage"

        private const val API_KEY_VALUE =
            "da8570065d949a8a3ee551b99f31f7774909575e702289b2743fab0aad0ffe41"
        private const val PLATFORM_VALUE = "MOBILE"

        // actions
        private const val SBER_ID_BUTTON_SHOW = "SberID Login Show"
        private const val SBER_ID_WRONG_BUTTON_SIZE = "SberID Wrong Button Size"
        private const val SBER_ID_BUTTON_CLICK = "SberID Login Button Click"
        private const val SBER_ID_AUTH_RESULT = "SberID Login Auth Result"

        // action keys/values
        private const val SDK_VERSION_KEY = "sdkVersion"
        private const val RESULT_KEY = "result"
        private const val ERROR_KEY = "errorDescription"
        private const val COLOR_KEY = "colorView"
        private const val HEIGHT_KEY = "heightView"
        private const val WIDTH_KEY = "widthView"
        private const val MEASURED_WIDTH_KEY = "measuredWidthView"
        private const val PERSONAL_KEY = "personalView"

        private const val ANDROID_VALUE = "android_"
        private const val RESULT_SUCCESS = "success"
        private const val RESULT_FAIL = "fail"
        private const val COLOR_WHITE = "white"
        private const val COLOR_GREEN = "green"
        private const val TRUE_STR_INT = "1"
        private const val FALSE_STR_INT = "0"

        private const val EXECUTE_EXCEPTION_TAG = "AnalyticsExecutionError"
    }

    private val singleExecutor: Executor = Executors.newSingleThreadExecutor()

    private var sberbankAnalytics: ISberbankAnalytics? = null
    private var buttonDesignModel: SberIDButtonDesignModel = SberIDButtonDesignModel()

    override fun initPlugin(appContext: Context) {
        executeSafely(Runnable {
            if (sberbankAnalytics == null) {
                val metaCollector = AnalyticsMetaCollector()
                val profileCollector = AnalyticsProfileCollector()

                sberbankAnalytics = SberbankAnalytics.Builder(appContext)
                    .setAnalyticsMetaCollector(metaCollector, profileCollector)
                    .setCustomUrl(SberbankAnalyticsHostProvider.getSberbankAnalyticsHost())
                    .setAnalyticsDbEnabled(false)
                    .build()

                metaCollector.update(initMetaInfo(appContext))
            }
        })
    }

    override fun sberIDButtonShow(designModel: SberIDButtonDesignModel) {
        buttonDesignModel = designModel
        executeSafely(Runnable {
            val event = SberbankAnalyticsEvent(SBER_ID_BUTTON_SHOW)
            val params = hashMapOf<String, String>()
            params[SDK_VERSION_KEY] = ANDROID_VALUE.plus(BuildConfig.VERSION_NAME)
            event.addData(params.plus(designEventParams(designModel)))
            sberbankAnalytics?.sendEvent(event)
        })
    }

    override fun sberIDWrongButtonSize(measuredWidth: Int, realWidth: Int) {
        executeSafely(Runnable {
            val event = SberbankAnalyticsEvent(SBER_ID_WRONG_BUTTON_SIZE)
            val params = hashMapOf<String, String>()
            params[MEASURED_WIDTH_KEY] = measuredWidth.toString()
            params[WIDTH_KEY] = realWidth.toString()
            event.addData(params)
            sberbankAnalytics?.sendEvent(event)
        })
    }

    override fun sberIDButtonClick() {
        executeSafely(Runnable {
            val event = SberbankAnalyticsEvent(SBER_ID_BUTTON_CLICK)
            event.addData(designEventParams(buttonDesignModel))
            sberbankAnalytics?.sendEvent(event)
        })
    }

    override fun sberIDAuthResult(errorMessage: String?) {
        executeSafely(Runnable {
            val event = SberbankAnalyticsEvent(SBER_ID_AUTH_RESULT)
            val params = hashMapOf<String, String>()
            params[RESULT_KEY] = if (errorMessage.isNullOrBlank()) RESULT_SUCCESS else RESULT_FAIL
            if (errorMessage != null && errorMessage.isNotBlank()) {
                params[ERROR_KEY] = errorMessage
            }
            event.addData(params)
            sberbankAnalytics?.sendEvent(event)
        })
    }

    private fun executeSafely(runnable: Runnable) {
        try {
            singleExecutor.execute(runnable)
        } catch (ex: Exception) {
            Log.e(EXECUTE_EXCEPTION_TAG, ex.message.toString(), ex)
        }
    }

    private fun initMetaInfo(context: Context): Map<String, String> {
        return hashMapOf(
            Pair(API_KEY, API_KEY_VALUE),
            Pair(SBER_ID_KEY, getMerchantName(context)),
            Pair(PLATFORM_KEY, PLATFORM_VALUE),
            Pair(LANGUAGE_KEY, Locale.getDefault().displayLanguage)
        )
    }

    private fun getMerchantName(context: Context): String {
        val appLabel = context.packageManager.getApplicationLabel(context.applicationInfo)
        return if (appLabel.isNotBlank()) appLabel.toString() else context.packageName
    }

    private fun designEventParams(designModel: SberIDButtonDesignModel): MutableMap<String, String> {
        return hashMapOf(
            Pair(COLOR_KEY, if (designModel.isColored) COLOR_GREEN else COLOR_WHITE),
            Pair(HEIGHT_KEY, designModel.height.toString()),
            Pair(WIDTH_KEY, designModel.width.toString()),
            Pair(PERSONAL_KEY, if (designModel.isPersonal) TRUE_STR_INT else FALSE_STR_INT)
        )
    }
}