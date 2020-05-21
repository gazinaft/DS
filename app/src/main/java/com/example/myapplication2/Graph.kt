

package com.example.myapplication2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import kotlinx.coroutines.delay
var edgesFin: List<Graph.Edge> = listOf()

class Graph(context: Context): View(context) {
    var isSym = true


    val symmetricMatrix = MyMatrix.generateMatrix(9304, 10).symmetric()
    val asymmetricMatrix = MyMatrix.generateMatrix(9304, 10)
    var greph = generateGraph(symmetricMatrix)
    val degrees by lazy {if (this.isSym) this.toSymStringDegrees() else this.toStringDegrees()}
    lateinit var rootMatrix: MyMatrix
    var refr = false

    inner class Vertex(val num: Int, val neighbours: List<Int>, val coordinates: Pair<Float, Float>) {

        val wages: List<Int> = MyMatrix.generateWages(9304, 10)[num]
        val outer by lazy { this.countOuter() }
        val inter by lazy { this.countInner() }
        val total by lazy { this.inter + this.outer }
        val isolated by lazy { this.total == 0 }
        val hanging by lazy { this.total == 1 }
        var status = Status.NEW
        var bfsOrder = 0
        var parent: Vertex? = null
        var statusSpread = Status.NEW

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

        // Beginning of 6th Lab assignment

        //Calculates the route, which was used during Dijkstra algorithm
        fun calculatePath(): List<Int> = ((this.parent?.calculatePath() ?: mutableListOf<Int>()) + this.num)
        //Calculates the distance to the root vertex
        //In other words, the shortest distance from the beginning to this vertex
        fun getDistance(): Int = (this.parent?.getDistance() ?: 0) + ( (this.parent?.wages?.get(this.num)) ?: 0)

        //The Dijkstra algorithm itself
        suspend fun dijkstra(time: Long) {
            //List of constant vertexes
            val closed = mutableListOf<Vertex>(this)
            this.status = Status.CLOSED
            //A buffer array to update the array of constant vertexes
            val buffer = ArrayList<Vertex>()
            while (greph.any{it.status != Status.CLOSED}) {
                //iterates over the constant vertexes
                for (vert in closed) {
                    //finds new vertexes and optimises the old ones
                    vert.setWageChildren()
                }
                for (vert in closed) {
                    //chooses the most optimal vertex to make it constant
                    val debug = greph.filter { it.status == Status.PASSED }.minBy { it.getDistance() }
                    val edge = getConnections().find { it.inter == debug?.parent && it.outer == debug}
                    edge?.status = Status.CLOSED
                    if (edge != null) edgesFin += edge
                    debug?.status = Status.CLOSED
                    if (debug == null) continue
                    //Buffer for updating an array of constant vertexes
                    buffer.add(debug)
                    delay(time)
                }
                closed.addAll(buffer)
                buffer.clear()
            }
        }


        //The function for locating the most optimal routes on every step of the algorithm
        private fun setWageChildren() {
            for (i in this.neighbours.indices) {
                //println(neighbours)
                if (neighbours[i] == 0) continue
                val vertex = findByNum(i)

                //checks whether it is constant
                if (vertex.status == Status.CLOSED) continue

                //checks whether it is new
                if (vertex.parent == null) {
                    vertex.parent = this
                    vertex.status = Status.PASSED
                    val edge = getConnections().find { it.inter == vertex.parent && it.outer == vertex }
                    //println("Adding a parent ${vertex.num}, ${vertex.parent?.num}, ${vertex.getDistance()}")
                    edge?.status = Status.PASSED
                    if (edge != null) edgesFin += edge
                }
                //checks whether the route to this vertex is optimal
                //if not, changes it to more optimal
                else if (vertex.getDistance() > getDistance() + wages[i]) {
                    vertex.parent = this
                    edgesFin = (edgesFin.minus( edgesFin.find{ it.outer == vertex })) as List<Edge>
                    edgesFin = (edgesFin.plus( edgesFin.find{ it.outer == vertex }).filterNotNull())
                }
            }
        }




        //End of 6th Lab assignment



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


    //5th Lab Assignment




    data class Edge(val length: Int, val inter: Vertex, val outer: Vertex) {

        override fun toString(): String {
            return "$length ${inter.num} ${outer.num}"
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Edge) return false
            return (inter == other.inter) && (outer == other.outer)
        }
        var status = Status.NEW
        var status2 = Status.NEW
        //Checks the cycles in containsRoute by assigning new status to Vertexes
        fun spread() {
            if (outer.statusSpread == Status.PASSED) inter.statusSpread = Status.PASSED
            if (inter.statusSpread == Status.PASSED) outer.statusSpread = Status.PASSED
        }

        override fun hashCode(): Int {
            var result = length
            result = 31 * result + inter.hashCode()
            result = 31 * result + outer.hashCode()
            result = 31 * result + status.hashCode()
            result = 31 * result + status2.hashCode()
            return result
        }
    }

    //Checks whether there is a route between 2 points
    //If so, the edge between these points will create cycle
    //If status of the last Vertex changed, it means that there`s a route between these 2 Vertexes
    fun containsRoute(edgesL: List<Edge>, start: Vertex, end: Vertex): Boolean {
        start.statusSpread = Status.PASSED
        repeat(4) {
            edgesL.forEach{ it.spread() }
        }
        val res: Boolean = end.statusSpread == Status.PASSED
        greph.forEach { it.statusSpread == Status.NEW }
        return res
    }

    //Minimal wage of a graph
    var minWage: Int = 0
    //Karaskal algorithm itself
    //suspend means async or something similar
    suspend fun karaskal(time: Long) {
        val all = getConnections().filterNot { it.inter.num == it.outer.num }.toMutableList()
        val vertStack = mutableListOf<Graph.Vertex>()
        val edgeStack = mutableListOf<Graph.Edge>()
        //It continues until all the vertexes are present in skeleton
        while (vertStack.toSet() != greph.toSet()) {
            val active = all.filter { it.status==Status.NEW }.minBy { it.length }
                ?: break
            all.remove(active)
            active.status = Status.PASSED
            delay(time)
            //checks the presence of cycles
            if (active.inter in vertStack && active.outer in vertStack) if (containsRoute(edgeStack, active.inter, active.outer)) continue

            vertStack += mutableListOf(active.inter, active.outer)
            edgeStack += active
            //passes the value to the external variable
            edgesFin = edgeStack.map { x-> x }

            active.inter.status = Status.PASSED
            active.outer.status = Status.PASSED
            delay(time)
        }
        minWage = edgeStack.sumBy { it.length }
    }

    //Refreshes the status of edges and verticles
    fun refreshKaraskal() {
        getConnections().forEach { it.status = Status.NEW
        it.status2 = Status.NEW
        }
    }

    //Get all the edges
    fun getConnections(): List<Graph.Edge> {
        val res = mutableListOf<Graph.Edge>()
        greph.forEach { vert ->
            for ((index, secondV) in vert.neighbours.withIndex()) {
                if (secondV == 1) {
                    res += Graph.Edge(vert.wages[index], vert, findByNum2(index))
                }
            }
        }

        return res
    }
    //makes a matrix of a list of edges
    fun getWagedTreeMatrix(edgesL: List<Edge>): MyMatrix {
        val res = MyMatrix(greph.size)
        edgesL.forEach { edge ->
            val n1 = edge.inter.num
            val n2 = edge.outer.num
            res[n1, n2] = edge.length
            res[n2, n1] = edge.length
        }
        return res
    }
    fun getTreeMatrix(edgesL: List<Edge>): MyMatrix {
        val res = MyMatrix(greph.size)
        edgesL.forEach { edge ->
            val n1 = edge.inter.num
            val n2 = edge.outer.num
            res[n1, n2] = 1
            res[n2, n1] = 1
        }
        return res
    }


    //END OF 5TH LAB ASSIGNMENT

    fun findByNum2(n: Int): Vertex {
        return greph.first { it.num == n }
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
        fun connect2(matrix: MyMatrix) {
        for (i in matrix.skeleton.indices) {
            for (j in matrix.skeleton.indices){
                if (matrix[i,j] == 1) {
                    if (i == j) loop(findByNum2(i), canvas, b)
                    else interconnect(findByNum2(i).coordinates, findByNum2(j).coordinates, canvas, cy, b)
                }
            }
        }
    }

//        val graphs = if (!isSym) generateGraph(symmetricMatrix.unOriented()) else greph
//        connect(graphs)
        connect2(symmetricMatrix.unOriented())
//        for ((x, y) in points){
//            val i = points.indexOf(x to y)
//            canvas?.drawCircle(x, y, 50F, p)
//            canvas?.drawText("${i+1}", x, y, b)
//        }
//        drawPoints(greph, 50f, canvas, p, getConnections())


//        for (i in getConnections()) {
//            //interconnect(i.inter.coordinates, i.outer.coordinates, canvas, p.apply { strokeWidth = 7f; color = Color.RED }, p.apply { strokeWidth = 7f; color = Color.RED })
//            val point1 = i.inter.coordinates
//            val point2 = i.outer.coordinates
//            val (xc, yc) = plainAncor(point1, point2) ?: 0f to 0f
//            if (!collide(point1, point2, points)) {
//                //canvas?.drawCircle(xc, yc, RADIUS, p.apply { color = Color.rgb(255, 140, 0) })
//                canvas?.drawText(i.length.toString(), xc - 3f, yc + 2f, p.apply { color = Color.RED })
//            }
//            else {
//                val midDot = makeAnchorPoint(point1, point2)
////                canvas?.drawCircle(midDot.first, midDot.second, RADIUS, p.apply { color = Color.rgb(255, 140, 0) })
//                canvas?.drawText(i.length.toString(), midDot.first - 3f, midDot.second + 2f, p.apply { color = Color.RED })
//            }
//        }
        drawPoints(greph, 50f, canvas, p, getConnections())



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
            this.isSym = false
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
