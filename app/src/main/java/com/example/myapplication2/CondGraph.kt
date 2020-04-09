package com.example.myapplication2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class CondGraph(context: Context): View(context)  {

    class CVertex(val caption: String, val coords: Pair<Float, Float>)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val p = Paint()
        p.color = Color.LTGRAY
        p.style = Paint.Style.FILL
        val con = Paint()
        con.style = Paint.Style.STROKE
        con.strokeWidth = 2f
        con.color = Color.BLACK
        con.textSize = 23f
        val vertexes = generateCGraph(MyMatrix.generateMatrix(9304, 10))

        for (vert in vertexes.indices){
            if (vert + 1 < vertexes.size)
                canvas?.drawLine(vertexes[vert].coords.first, vertexes[vert].coords.second,vertexes[vert+1].coords.first, vertexes[vert+1].coords.second, con)
            canvas?.drawCircle(vertexes[vert].coords.first, vertexes[vert].coords.second, 150F, p)
            canvas?.drawText(vertexes[vert].caption, vertexes[vert].coords.first-125f, vertexes[vert].coords.second, con)
        }
    }
    companion object {
        fun generatePoints(n: Int) = when (n) {
            1 -> listOf(360f to 560F)
            2-> listOf(360f to 300f, 360f to 700f)
            else -> listOf(0f to 0f)
        }
        fun generateCGraph(matrix: MyMatrix): MutableList<CVertex> {
            val comps = matrix.strongComponents
            val verts = mutableListOf<CVertex>()
            if (comps.size == 1) return mutableListOf(CVertex(comps.first().map { x-> x+1 }.toString(), 360f to 560f))
            for (c in comps.indices){
                verts[c] = CVertex(comps[c].toString(), generatePoints(comps.size)[c])
            }
            return verts
        }
    }
}