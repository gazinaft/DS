

package com.example.myapplication2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import kotlinx.coroutines.delay

class Graph(context: Context): View(context) {
    var isSym = false


    val symmetricMatrix = MyMatrix.generateMatrix(9304, 10).symmetric()
    val asymmetricMatrix = MyMatrix.generateMatrix(9304, 10)
    var greph = generateGraph(symmetricMatrix)
    val degrees by lazy {if (this.isSym) this.toSymStringDegrees() else this.toStringDegrees()}
    lateinit var rootMatrix: MyMatrix
    var refr = false

    inner class Vertex(val num: Int, val neighbours: List<Int>, val coordinates: Pair<Float, Float>) {

        val outer by lazy { this.countOuter() }
        val inter by lazy { this.countInner() }
        val total by lazy { this.inter + this.outer }
        val isolated by lazy { this.total == 0 }
        val hanging by lazy { this.total == 1 }
        var status = Status.NEW
        var bfsOrder = 0
        var parent: Vertex? = null


        fun findByNum(n: Int): Vertex {
            return greph.first { it.num == n }
        }
        fun calculateChildren(): List<Vertex> {
            return nonZeroIndexes(this.neighbours).map {  findByNum(it) }.filter { it.status==Status.NEW }.filter { it!=this }
        }


        fun bfsStringify(): String{
            val start = greph.sortedBy { it.num }.map{it.num+1}
            val end = greph.sortedBy { it.num }.map{it.bfsOrder}
            return """Old and new order
            |$start
            |$end
        """.trimMargin()
        }

        fun bfsMatrix(): String {
            val start = greph.sortedBy { it.num }
            val result = MyMatrix(greph.size)
            for (vert in start) {
                val par = vert.parent ?: vert
                result[par.num, vert.num] = 1
            }
            return result.toString()
        }
        fun refreshVert() {
            greph.forEach{
                it.status = Status.NEW
                it.bfsOrder = 0
                it.parent = null
            }

        }
        suspend fun startBFS() {
            var k = 1
            this.bfsOrder = k
            this.status = Status.PASSED
            val queue = mutableListOf<Vertex>(this)
            delay(200L)
            while (queue.isNotEmpty()) {
                val active = queue.first()
                if (active.calculateChildren().isEmpty()) {
                    active.status = Status.CLOSED
                    delay(200L)
                    queue.removeAt(0)
                    continue
                }
                for (child in active.calculateChildren()) {
                    k += 1
                    Log.d("TestCycle", "${child.num}")
                    child.bfsOrder = k
                    child.status = Status.PASSED
                    child.parent = active
                    delay(200L)
                    queue.add(child)
                }
                active.status = Status.CLOSED
                delay(200L)
                queue.removeAt(0)
            }
        }
        fun countInner(): Int {
            var res = 0
            for (vert in greph) {
                if(vert.neighbours[this.num] == 1) res+=1
            }
            return res
        }
        fun countOuter() = this.neighbours.reduce { acc, i -> acc + i }
        fun symTotal() = this.outer + (if (this.neighbours[this.num] == 1) 1 else 0)

    }

    fun countHanging() = greph.count{ it.hanging }
    fun countIsolated() = greph.count{ it.isolated }
    fun notEvenDegrees(): Boolean {
        val first = greph[0].total
        return greph.any { it.total!= first }
    }
    override fun onDraw(canvas: Canvas?) {
        val p = Paint()
        val b = Paint()
        val cy = Paint()
        b.color = Color.BLACK
        b.strokeWidth = 2F
        b.style = Paint.Style.STROKE
        p.color = Color.LTGRAY
        p.strokeWidth = 10F
        cy.style = Paint.Style.FILL_AND_STROKE
        cy.color = Color.rgb(165,159,98)

        fun connect(vertexes: List<Graph.Vertex>) {
            for (i in vertexes.indices) {
                for (j in vertexes.indices){
                    if (vertexes[i].neighbours[j] == 1) {
                        if (i == j) loop(vertexes[i], canvas, b)
                        else interconnect(vertexes[i].coordinates, vertexes[j].coordinates, canvas, cy, b)
                    }
                }

            }
        }
        val graphs = if (isSym) generateGraph(symmetricMatrix.unOriented()) else greph
        connect(graphs)
//        for ((x, y) in points){
//            val i = points.indexOf(x to y)
//            canvas?.drawCircle(x, y, 50F, p)
//            canvas?.drawText("${i+1}", x, y, b)
//        }
        drawPoints(greph, 50f, canvas, p)
        if (refr){
            canvas?.drawRGB(255, 255, 255)
            refr = false
        }
        invalidate()

    }
    fun setSymmetricity(boolean: Boolean) {
        if (boolean) {
            this.rootMatrix = symmetricMatrix
            this.greph = generateGraph(symmetricMatrix)
            this.isSym = true
        }
        else {
            this.rootMatrix = asymmetricMatrix
            this.greph = generateGraph(asymmetricMatrix)
            this.isSym = false
        }
    }
    fun generateGraph(matrix: MyMatrix): MutableList<Vertex> {
        if (matrix.isSym) this.isSym = true
        val points = generatePoints(matrix.width)
        val vertexes = MutableList(matrix.width) {Vertex(0, listOf(0), 0F to 0F)}
        for (i in 0 until matrix.width) {
        vertexes[i] = Vertex(i, matrix[i], points[i])
        }
        this.greph = vertexes
        return vertexes
    }

    fun refresh() {
        this.greph.forEach{
            it.status = Status.NEW
            it.bfsOrder = 0
            it.parent = null
        }
        this.refr = true


    }

    fun toStringDegrees(): String {
        val sb = StringBuilder()
        for (vert in greph){
            sb.append("""${vert.num+1} Вхідні: ${vert.inter} Вихідні: ${vert.outer} Загально: ${vert.total}
                |
            """.trimMargin()
            )
        }
        sb.append("""Висячих:${this.countHanging()} Ізольованих:${this.countIsolated()}
            |
        """.trimMargin())
        sb.append("Граф ${if (this.notEvenDegrees()) "неоднорідний" else "однорідний"}")
        return sb.toString()
    }
    fun toSymStringDegrees(): String {
        val sb = StringBuilder()
        for (vert in greph){
            sb.append("""${vert.num+1} Cтепінь: ${vert.symTotal()}
                |
            """.trimMargin()
            )
        }
        sb.append("""Висячих:${this.countHanging()} Ізольованих:${this.countIsolated()}
            |
        """.trimMargin())
        sb.append("Граф ${if (this.notEvenDegrees()) "неоднорідний" else "однорідний"}")
        return sb.toString()
    }

}
