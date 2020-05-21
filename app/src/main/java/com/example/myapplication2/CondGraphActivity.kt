package com.example.myapplication2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CondGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cond_graph)
        val graph = Graph(this)
        graph.generateGraph(MyMatrix.generateMatrix())
        graph.setSymmetricity(true)
//        runBlocking {
//            graph.greph[0].dijkstra(0L)
//        }

        //val matrix = MyMatrix.generateWages()
        //val minWage = graph.minWage
        setContentView(CondGraph(this))
        //val routes2 = MyMatrix.generateMatrix(9304, 10).findPath2()
        //val routes3 = MyMatrix.generateMatrix(9304, 10).findPath3()
        val routes = graph.greph.map { it.calculatePath() }
//        val lengths = routes.map {list -> list.map { graph.findByNum2(it).calculatePathLength() }.sum() }
        val lengths = routes.map {list -> list.map { graph.findByNum2(it).getDistance() }.last() }
        val sb = StringBuilder()
        sb.append("Shortest routes and their length")
        sb.append('\n')
        sb.append(routes.zip(lengths).toString())
        sb.append('\n')
        sb.append('\n')
        sb.append('\n')
        sb.append(MyMatrix.generateWages())
        //val textView = findViewById<TextView>(R.id.goodText)
//        textView.text = sb.toString()
//        val texts = """Довжина2:
//            | $routes2
//            | Довжина 3:
//            | $routes3
//            """.trimMargin()
//        val butt = findViewById<TextView>(R.id.goodText)
//        butt.text = """Minimal wage: $minWage
//            |
//            |Wage matrix:
//            |$matrix
//        """.trimMargin()
    }

}
