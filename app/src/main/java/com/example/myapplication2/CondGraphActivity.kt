package com.example.myapplication2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CondGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cond_graph)
        setContentView(CondGraph(this))
    }

}
