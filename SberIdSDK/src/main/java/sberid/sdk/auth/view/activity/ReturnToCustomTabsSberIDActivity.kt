package sberid.sdk.auth.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Активити используется только для возврата к стеку активити приложения партнера при авторизации через веб окно Сбер ID
 * в CustomTabs. Партнеру нужно будет прописать в своем manifest данную активити и intent-filter для нее.
 *
 * <b>Пример</b>
 * <intent-filter>
 *     <action android:name="android.intent.action.VIEW" />
 *     <category android:name="android.intent.category.DEFAULT" />
 *     <category android:name="android.intent.category.BROWSABLE" />
 *         <data
 *              android:host="app_host"
 *              android:scheme="app_scheme" />
 * </intent-filter>
 *
 * @author Gerasimenko Nikita
 */
class ReturnToCustomTabsSberIDActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}