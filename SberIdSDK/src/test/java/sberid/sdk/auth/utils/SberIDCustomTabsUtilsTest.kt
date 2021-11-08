package sberid.sdk.auth.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Log.DEBUG
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import sberid.sdk.auth.R
import java.util.ArrayList

/**
 * @author Gerasimenko Nikita
 */
class SberIDCustomTabsUtilsTest {

    private val context: Context = mockk()
    private val intent: Intent = mockk()
    private val uri: Uri = mockk()

    private val packageManager: PackageManager = mockk()
    private val resources: Resources = mockk()

    @Before
    fun setUp() {
        //intent
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().setAction(any()) }.returns(intent)
        every {
            anyConstructed<Intent>().putExtra(
                CustomTabsIntent.EXTRA_TITLE_VISIBILITY_STATE,
                CustomTabsIntent.SHOW_PAGE_TITLE
            )
        }.returns(intent)
        every { anyConstructed<Intent>().putExtras(any<Bundle>()) }.returns(intent)
        every { anyConstructed<Intent>().hasExtra(any()) }.returns(false)
        every { anyConstructed<Intent>().putExtra(any(), any<Boolean>()) }.returns(intent)
        every { anyConstructed<Intent>().putExtra(any(), any<Int>()) }.returns(intent)
        every { anyConstructed<Intent>().setData(uri) }.returns(intent)
        every { intent.setAction(any()) }.returns(intent)
        every { intent.addCategory(Intent.CATEGORY_BROWSABLE) }.returns(intent)
        every { intent.setData(any()) }.returns(intent)
        every { anyConstructed<Intent>().setPackage(any()) }.returns(intent)

        //static
        mockkStatic(TextUtils::class, Uri::class, Log::class, CustomTabsClient::class, ContextCompat::class)
        every { TextUtils.isEmpty(any()) }.returns(false)
        every { Uri.fromParts(any(), any(), any()) }.returns(uri)
        every { Log.d(any(), any()) }.returns(DEBUG)
        every { Log.i(any(), any(), any()) }.returns(DEBUG)
        every { CustomTabsClient.bindCustomTabsService(context, any(), any()) }.returns(true)

        //context
        every { context.packageManager }.returns(packageManager)
        every { context.applicationContext }.returns(context)
        every { context.resources }.returns(resources)
        every { context.startActivity(any()) }.returns(Unit)
        every { ContextCompat.getColor(context, R.color.color_sber_id_button_primary) }.returns(COLOR)
        every { context.bindService(any(), any(), any()) }.returns(true)

        //bundle
        mockkConstructor(Bundle::class)
        every { anyConstructed<Bundle>().putInt(any(), any()) }.returns(Unit)
        every { anyConstructed<Bundle>().putInt(any(), any()) }.returns(Unit)
    }

    @Test
    fun `launch customTabs when returns null resolveInfo`() {
        mockTestResolveInfoList()
        every { packageManager.resolveService(any(), any()) }.returns(null)

        SberIDCustomTabsUtils.launchCustomTabs(context, uri)

        assertThat(SberIDCustomTabsUtils.launchCustomTabs(context, uri)).isFalse()
        verify(exactly = 0) { context.startActivity(any()) }
    }

    @Test
    fun `launch customTabs when returns only one resolveInfo`() {
        mockTestResolveInfoList(CHROME_PACKAGE)
        every { packageManager.resolveService(any(), any()) }.returns(ResolveInfo())

        SberIDCustomTabsUtils.launchCustomTabs(context, uri)

        assertThat(SberIDCustomTabsUtils.launchCustomTabs(context, uri)).isTrue()
        verify { context.startActivity(any()) }
    }

    @Test
    fun `launch customTabs when returns some resolveInfo`() {
        mockTestResolveInfoList(CHROME_PACKAGE, OPERA, FIREFOX)
        every { packageManager.resolveService(any(), any()) }.returns(ResolveInfo())

        SberIDCustomTabsUtils.launchCustomTabs(context, uri)

        assertThat(SberIDCustomTabsUtils.launchCustomTabs(context, uri)).isTrue()
        verify { context.startActivity(any()) }
    }

    @Test
    fun `warnUp customTabs when returns null resolveInfo`() {
        mockTestResolveInfoList()
        every { packageManager.resolveService(any(), any()) }.returns(null)

        SberIDCustomTabsUtils.warnUpCustomTabs(context)

        verify(exactly = 0) { CustomTabsClient.bindCustomTabsService(context, any(), any()) }
    }

    @Test
    fun `warnUp customTabs when returns only one resolveInfo`() {
        mockTestResolveInfoList(CHROME_PACKAGE)
        every { packageManager.resolveService(any(), any()) }.returns(ResolveInfo())

        SberIDCustomTabsUtils.warnUpCustomTabs(context)

        verify { CustomTabsClient.bindCustomTabsService(context, any(), any()) }
    }

    @Test
    fun `warnUp customTabs when returns some resolveInfo`() {
        mockTestResolveInfoList(CHROME_PACKAGE, OPERA, FIREFOX)
        every { packageManager.resolveService(any(), any()) }.returns(ResolveInfo())

        SberIDCustomTabsUtils.warnUpCustomTabs(context)

        verify(exactly = 3) { CustomTabsClient.bindCustomTabsService(context, any(), any()) }
    }

    private fun mockTestResolveInfoList(vararg packageNames: String) {
        val resolveInfos: MutableList<ResolveInfo> = ArrayList()
        for (packageName in packageNames) {
            val resolveInfo = ResolveInfo()
            resolveInfo.activityInfo = ActivityInfo()
            resolveInfo.activityInfo.packageName = packageName
            resolveInfos.add(resolveInfo)
        }
        every { packageManager.queryIntentActivities(any(), 0) }.returns(resolveInfos)
    }

    companion object {
        /**
         * [CHROME_PACKAGE], [OPERA], [FIREFOX] взять для наглядности тестов, проверки пакетов не происходит
         */
        private const val CHROME_PACKAGE = "com.android.chrome"
        private const val OPERA = "com.opera.browser"
        private const val FIREFOX = "com.mozilla.firefox"
        private const val COLOR = 1234
    }
}