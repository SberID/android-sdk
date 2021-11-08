package sberid.sdk.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 *  Тестовая стартовая активити, которая вызывает активити с кнопкой
 *
 * @author Лелюх Александр
 */
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val textView: TextView = findViewById(R.id.text)
        textView.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}