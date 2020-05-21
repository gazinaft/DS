package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_matrix.*

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
        graph = Graph(this)

        verts = graph.generateGraph(MyMatrix.generateMatrix(9304, 10))
        //degreeTextView.setOnClickListener { nextTick(degreeTextView) }
    }

    override fun onResume() {
        super.onResume()
        showMatrix()
    }
    fun showMatrix() {
        val matrix = MyMatrix.generateMatrix().symmetric()
        val degrees = intent.getStringExtra(DEGREES)
        //val matr = graph.getTreeMatrix(edgesFin)
        //val wages = graph.getWagedTreeMatrix(edgesFin)
        matrixTextView.text = matrix.toString()
        //degreeTextView.text = wages.toString()
        numetationView.text = numeration()
    }
    fun show6(view: View) {
        val g = graph
    }
//    fun nextTick(view: View){
//        if (counter == 10) {
//            counter = 0
//        }
//        runBlocking {
//            verts[counter].refreshVert()
//            verts[counter].startBFS()
//            matrixTextView.text = verts[counter].bfsMatrix()
//                degreeTextView.text = "${counter+1} ${verts[counter].bfsStringify()}"
//            counter +=1
//       }
//
//    }
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

    fun showMatrixes2(view: View){
        val matrix = MyMatrix.generateMatrix(9304, 10)
        val myToast = Toast.makeText(this,
            """Матриця досяжності:
            |${matrix.transClosure()}
            |Матриця зв'язності:
            |${matrix.strongConnect}
            |
            |Компоненти сильної зв'язності
            |${matrix.strongComponents.map { list -> list.map { it+1 } }}
        """.trimMargin(), Toast.LENGTH_LONG)
        myToast.show()
    }

    fun showRoutes2(view: View) {
        val routes3 = MyMatrix.generateMatrix(9304, 10).findPath2()
        val text = """Довжина2:
            | $routes3
            """.trimMargin()
        val showing = Toast.makeText(this, text, Toast.LENGTH_LONG)
        showing.show()
    }

    fun showRoutes3(view: View) {
        val routes3 = MyMatrix.generateMatrix(9304, 10).findPath3()
        val text = """$routes3"""
        val showing = Toast.makeText(this, text, Toast.LENGTH_LONG)
        showing.show()
    }
//    fun showEdges(view: View) {
//        val a = graph.getConnections()
//        val textView = findViewById<TextView>(R.id.degreeTextView)
//        textView.text = a.toString()
//        matrixTextView.text
//    }

}
