package sberid.sdk.auth.service

import android.content.ComponentName
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection

private const val TAG = "ServiceConnector"

/**
 * Обёртка над [CustomTabsServiceConnection] для отработки статуса привязанности сервиса
 *
 * @author Gerasimenko Nikita
 */
internal class CustomTabsServiceConnector : CustomTabsServiceConnection() {

    /**
     * Информирует о статусе привязанности сервиса
     * Использучется для вызова публичного сервиса на стороне МП Сбол
     */
    var isBound = false

    override fun onServiceDisconnected(name: ComponentName?) {
        isBound = false
    }

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        try {
            client.warmup(0L)
            isBound = true
        } catch (e: SecurityException) {
            Log.e(TAG, e.message.toString(), e)
        }
    }
}