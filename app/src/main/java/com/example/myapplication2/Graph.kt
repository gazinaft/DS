

package com.example.myapplication2

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.abs

class Graph(context: Context): View(context) {

    val wid = 720F
    val hei = 1124F
    val upper = wid/2 to 75F
    val bottomLeft = 75F to hei - 75F
    val bottomRight = wid - 75F to hei - 75F
    val points = generatePoints(10)


    inner class Vertex(val num: Int, val neighbours: List<Int>, val coordinates: Pair<Float, Float>)

    override fun onDraw(canvas: Canvas?) {
        val p = Paint()
        val b = Paint()
        val cy = Paint()
        b.color = Color.BLACK
        b.strokeWidth = 2F
        b.style = Paint.Style.STROKE
        p.color = Color.LTGRAY
        p.strokeWidth = 10F
        cy.style = Paint.Style.STROKE
        cy.color = Color.RED
        val verticalPoints = generatePoints(10)
        fun loop(vertex: Vertex) {
            val first = vertex.coordinates.first
            val second = vertex.coordinates.second
            canvas?.drawArc( first - 50F, second - 90F, first + 50F, second, 135F, 270F, false, b)
        }

        fun interconnect(point1: Pair<Float, Float>, point2: Pair<Float, Float>, paint: Paint = cy) {
            val x1 = point1.first
            val y1 = point1.second
            val x2 = point2.first
            val y2 = point2.second
            if (!collide(point1, point2, points)) {
                canvas?.drawLine(point1.first, point1.second, point2.first, point2.second, paint)
            }
            else{
                val midDot = makeAnchorPoint(point1, point2)
                canvas?.drawLine(point1.first, point1.second ,midDot.first, midDot.second ,b)
                canvas?.drawLine(midDot.first, midDot.second ,point2.first, point2.second ,b)

            }
        }
        fun connect(vertexes: List<Vertex>) {
            for (i in vertexes.indices) {
                for (j in vertexes.indices){
                    if (vertexes[i].neighbours[j] == 1) {
                        if (i == j) loop(vertexes[i])
                        else interconnect(vertexes[i].coordinates, vertexes[j].coordinates)
                    }
                }

            }
        }
        val points = generatePoints(10)
        val graphs = generateGraph(MyMatrix.generateMatrix(9304, 10).symmetric().unOriented())
        connect(graphs)
        for ((x, y) in points){
            val i = points.indexOf(x to y)
            canvas?.drawCircle(x, y, 50F, p)
            canvas?.drawText("${i+1}", x, y, b)
        }

    }

    fun generateGraph(matrix: MyMatrix): MutableList<Vertex> {
        val points = generatePoints(matrix.width)
        val vertexes = MutableList(matrix.width) {Vertex(0, listOf(0), 0F to 0F)}
        for (i in 0 until matrix.width) {
        vertexes[i] = Vertex(i, matrix[i], points[i])
        }
        return vertexes
    }

    fun generatePoints(quan: Int): MutableList<Pair<Float, Float>> {
        if (quan < 3) throw Error("Too little arguments")
        val result = MutableList(quan){ Pair(0F, 0F)}
        result[0] = bottomLeft

        when {
            quan % 2 == 0 -> {
                result[quan - 1] = wid/2 to bottomLeft.second
                result[quan - 2] = bottomRight
            }
            quan % 2 != 0 -> {
                result[quan - 1] = (wid - 150F)/3 + bottomLeft.first to bottomLeft.second
                result[quan - 2] = 2*(wid - 150F)/3 + bottomLeft.first to bottomLeft.second
                result[quan - 3] = bottomRight
            }
        }

        val middle = if (quan % 2 == 0 ) (quan / 2) - 1
        else (quan - 1)/2 - 1

        result[middle] = upper
        for (i in 1 until middle) {
            val x = bottomLeft.first + i * abs(bottomLeft.first - upper.first)/(middle)
            result[i] = x to gY(upper, bottomLeft)(x)
        }
        for (i in middle+1 until (middle*2)) {
            val x = upper.first + (i - middle) * abs(bottomRight.first - upper.first)/(middle)
            result[i] = x to gY(upper, bottomRight)(x)
        }
        return result
    }
}
