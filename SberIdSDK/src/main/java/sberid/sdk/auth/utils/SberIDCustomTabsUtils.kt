package sberid.sdk.auth.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import ru.sberbank.mobile.clickstream.utils.CollectionUtils
import ru.sberbank.mobile.clickstream.utils.StringUtils
import sberid.sdk.auth.R
import sberid.sdk.auth.service.CustomTabsServiceConnector
import java.util.ArrayList

/**
 * Класс с инструментами для работы c CustomTabs
 *
 * @author Gerasimenko Nikita
 */
internal class SberIDCustomTabsUtils {

    companion object {
        private const val TAG = "CustomTabsUtils"
        private const val EXAMPLE_SCHEME = "https"
        private const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"
        private const val NO_FLAG = 0

        /**
         * Позволяет разогреть процесс браузера. Если на устройстве не выбранных по умолчанию браузеров, вызывает
         * разогревает все браузеры. Если такой браузер есть, то только его.
         */
        internal fun warnUpCustomTabs(context: Context): CustomTabsServiceConnector {
            val connection = CustomTabsServiceConnector()
            for (info in getPackagesSupportingCustomTabs(context)) {
                CustomTabsClient.bindCustomTabsService(context, info.activityInfo.packageName, connection)
            }
            return connection
        }

        /**
         * Метод для холодного запуска CustomTabs
         *
         * @param context     [Context] context для запуска активити
         * @param merchantUri [Uri] uri ресурса для загрузки
         * @return `true` запуск успешен, `false` - не найден браузер, поддерживающий CustomTabs
         */
        internal fun launchCustomTabs(context: Context, merchantUri: Uri): Boolean {
            if (!isCustomChromeTabsSupported(context)) {
                return false
            }

            try { // Для случаев, когда партнер не подкючил библиотеку androidx.browser
                val params = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(context, R.color.color_sber_id_button_primary))
                    .build()

                val customTabsIntent = CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(params)
                    .setShowTitle(true)
                    .build()

                Log.d(TAG, "CustomTabs was launched with URL: $merchantUri")
                customTabsIntent.launchUrl(context, merchantUri)
            } catch (e: Throwable) {
                Log.e(TAG, e.message.toString(), e)
                return false
            }

            return true
        }

        /**
         * Метод для проверки доступности Custom Chrome Tabs на устройстве.
         *
         * @param context [Context] для работы с [PackageManager].
         * @return `true` - если Custom Chrome Tabs доступен на устройстве, `false` - иначе.
         */
        private fun isCustomChromeTabsSupported(context: Context): Boolean {
            return CollectionUtils.isNotEmpty(getPackagesSupportingCustomTabs(context))
        }

        private fun getPackagesSupportingCustomTabs(context: Context): List<ResolveInfo> {
            val packageManager = context.packageManager
            val activityIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.fromParts(EXAMPLE_SCHEME, StringUtils.EMPTY, null))

            val resolvedActivityList = packageManager.queryIntentActivities(activityIntent, NO_FLAG)
            val packagesSupportingCustomTabs = ArrayList<ResolveInfo>()

            for (info in resolvedActivityList) {
                val serviceIntent = Intent()
                serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
                serviceIntent.setPackage(info.activityInfo.packageName)
                if (packageManager.resolveService(serviceIntent, 0) != null) {
                    packagesSupportingCustomTabs.add(info)
                }
            }

            return packagesSupportingCustomTabs
        }
    }
}