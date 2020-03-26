package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var greph: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val graph = Graph(this)
        greph = graph.toStringDegrees()
        graph.setOnClickListener { goToText(graph) }
        setContentView(graph)
    }
    fun goToText(view: View) {
        val symMatrix = MyMatrix.generateMatrix(9304, 10)

        val inten = Intent(this, MatrixActivity::class.java)
        inten.putExtra(MatrixActivity.MATRIX_STR, symMatrix.toString())
        inten.putExtra(MatrixActivity.DEGREES, greph)
        startActivity(inten)
    }
}
