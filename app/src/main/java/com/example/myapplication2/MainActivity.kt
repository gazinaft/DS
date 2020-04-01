package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var graph: Graph
    lateinit var greph: String
    lateinit var matrix: MyMatrix
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        graph = Graph(this)
        graph.setSymmetricity(false)
        greph = graph.degrees
        graph.setOnClickListener { goToText(graph) }
        setContentView(graph)
    }
    fun goToText(view: View) {
        matrix = graph.rootMatrix
        val inten = Intent(this, MatrixActivity::class.java)
        inten.putExtra(MatrixActivity.MATRIX_STR, matrix.toString())
        inten.putExtra(MatrixActivity.DEGREES, greph)
        startActivity(inten)
    }
}
