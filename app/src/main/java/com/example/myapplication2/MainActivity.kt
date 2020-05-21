package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var graphMain: Graph
    lateinit var greph: String
    lateinit var matrix: MyMatrix
    var counter = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        graphMain = Graph(this)
    }

    override fun onStart() {
        super.onStart()

        graphMain.setSymmetricity(false)
        greph = graphMain.degrees
        graphMain.setOnClickListener { goToText(graphMain) }
//        graphMain.setOnClickListener { karasskal(graphMain) }
//        graphMain.setOnClickListener { sixthLab(graphMain) }
        setContentView(graphMain)
    }
    fun goToText(view: View) {
        matrix = graphMain.rootMatrix
        val inten = Intent(this, MatrixActivity::class.java)
        inten.putExtra(MatrixActivity.MATRIX_STR, matrix.toString())
        inten.putExtra(MatrixActivity.DEGREES, greph)
        startActivity(inten)
    }
    fun sixthLab(view: View) {
        //graphMain.refresh()
        //graphMain.refreshKaraskal()
        val inten = Intent(this, MatrixActivity::class.java)
        GlobalScope.launch {
            graphMain.greph[0].dijkstra(2000L)
            delay(5000L)
            startActivity(inten)
//            graphMain.refresh()
//            graphMain.refreshKaraskal()
//            edgesFin = emptyList()
        }
    }
    fun karasskal(view: View) {
        graphMain.refresh()
        graphMain.refreshKaraskal()
        val inten = Intent(this, MatrixActivity::class.java)
        GlobalScope.launch {
            graphMain.karaskal(1000L)
            delay(3000L)
            //startActivity(inten)
        }

    }

    fun test(view: View) {
        graphMain.refresh()
        if(counter == 9) {
            graphMain.refresh()
            val myIntent = Intent(this, MatrixActivity::class.java)
            startActivity(myIntent)
            counter = 0
        }
//        graphMain.greph[counter].status = Status.PASSED
        GlobalScope.launch {
            graphMain.greph[counter].startBFS()
        }
        counter += 1

    }

}


