package sberid.sdk.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Тестовая активити, разметка которой состоит из простого линейного лейаута и ресайклера внутри
 * Кнопки есть как в родительском лейауте, так и внутри ресайклера, они одинаковые, чтобы было удобно сравнивать результат (он не должен отличаться)
 * Для удобства какие-то кнопки можно закомментировать или добавить новые, пока там есть все основные варианты по щирине:
 * - match_parent
 * - wrap_content
 * - недопустимо меленькая ширина
 * - ширина больше минимальной
 */
class MainLinearRecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linear)

        val recycler: RecyclerView = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TestAdapter()

    }

    private class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

        class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.buttons_item, parent, false)
            return TestViewHolder(view)
        }

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            //nothing
        }

        override fun getItemCount() = 1
    }
}
