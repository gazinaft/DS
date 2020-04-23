package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_matrix.*
import kotlinx.coroutines.runBlocking

class MatrixActivity : AppCompatActivity() {
    companion object {
        const val MATRIX_STR: String = "matrix_str"
        const val DEGREES = "degrees"
    }
    lateinit var verts: MutableList<Graph.Vertex>
    lateinit var graph: Graph
    var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix)
        //showMatrix()
        graph = Graph(this)
        verts = graph.generateGraph(MyMatrix.generateMatrix(9304, 10))
        //degreeTextView.setOnClickListener { nextTick(degreeTextView) }
    }
    fun showMatrix() {
        val matrix = intent.getStringExtra(MATRIX_STR)
        val degrees = intent.getStringExtra(DEGREES)
        matrixTextView.text = matrix
        degreeTextView.text = degrees
    }
    fun nextTick(view: View){
        if (counter == 10) {
            counter = 0
        }
        runBlocking {
            verts[counter].refreshVert()
            verts[counter].startBFS()
            matrixTextView.text = verts[counter].bfsMatrix()
            degreeTextView.text = "${counter+1} ${verts[counter].bfsStringify()}"
            counter +=1
       }

    }
    fun goToCondensation(view: View) {
        val going = Intent(this, CondGraphActivity::class.java)
        startActivity(going)
    }

    fun showMatrixes(view: View){
        val matrix = MyMatrix.generateMatrix(9304, 10)
        val myToast = Toast.makeText(this,
            """Довжина 2:
            |${matrix.power(2)}
            |Довжина 3:
            |${matrix.power(3)}
        """.trimMargin(), Toast.LENGTH_LONG)
        myToast.show()
    }

    fun showRoutes2(view: View) {
        val routes3 = MyMatrix.generateMatrix(9304, 10).findPath3()
        val text = """Довжина3:
            | $routes3
            """.trimMargin()
        val showing = Toast.makeText(this, text, Toast.LENGTH_LONG)
        showing.show()
    }
}
