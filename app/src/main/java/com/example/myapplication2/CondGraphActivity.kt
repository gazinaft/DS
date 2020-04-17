package com.example.myapplication2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CondGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cond_graph)
        //setContentView(CondGraph(this))
        val routes2 = MyMatrix.generateMatrix(9304, 10).findPath2()
        val routes3 = MyMatrix.generateMatrix(9304, 10).findPath3()
        val texts = """Довжина2:
            | $routes2
            | Довжина 3:
            | $routes3
            """.trimMargin()
        val butt = findViewById<TextView>(R.id.goodText)
        butt.text = texts
    }

}
