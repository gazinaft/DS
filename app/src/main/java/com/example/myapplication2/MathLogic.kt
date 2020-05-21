package com.example.myapplication2
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


const val wid = 720F
const val hei = 1124F
val upper = wid/2 to 75F
val bottomLeft = 75F to hei - 75F
val bottomRight = wid - 75F to hei - 75F
val points = generatePoints(10)
const val N3 = 0.0
const val N4 = 4.0
const val RADIUS = 20f


enum class Status {
    NEW, PASSED, CLOSED
}

fun gY(p1: Pair<Float, Float>, p2: Pair<Float, Float>): (Float) -> Float {
    return {x: Float ->  (x - p1.first)*(p2.second-p1.second)/(p2.first-p1.first) + p1.second}
}

fun numeration(): String = """
    |1
    |2
    |3
    |4
    |5
    |6
    |7
    |8
    |9
    |10
""".trimMargin()

fun gX(p1: Pair<Float, Float>, p2: Pair<Float, Float>): (Float) -> Float {
    return { y: Float -> (y - p1.second)/(p2.second - p1.second)*(p2.first - p1.first) + p1.first }
}

fun collide(start: Pair<Float, Float>, end: Pair<Float, Float>, points: List<Pair<Float, Float>>): Boolean {
    val dx = end.first - start.first
    val dy = end.second - start.second
    if (points.contains(start) && points.contains(end)){
        if ( abs(points.indexOf(start) - points.indexOf(end)) == 1) return false
    }
    if (start.first <= 360F && end.first <= 360F) return true
    if (start.first >= 360F && end.first >= 360F) return true
    if (dx == 0F || dy ==0F) return true
    return false
}

fun getPerpendicularY(start: Pair<Float, Float>, end: Pair<Float, Float>, pointF: Pair<Float, Float>): (Float) -> Float {
    return { xF: Float -> (xF - pointF.first)*(start.first - end.first)/(end.second - start.second) + pointF.second }
}

fun makeAnchorPoint(start: Pair<Float, Float>, end: Pair<Float, Float>): Pair<Float, Float> {
    val dx = end.first - start.first
    val dy = end.second - start.second
    val xs = (start.first + end.first) / 2
    val ys = (start.second + end.second) / 2
    val perpendicular = getPerpendicularY(start, end, xs to ys)
    when {
        (dy == 0F) -> return xs to start.second + (if (dx > 0F) 80F else -80F)
        (dx == 0F) -> return start.first + (if (dy > 0F) 80F else -80F) to (start.second + end.second) / 2
        dy > 0F -> return xs + 80F to perpendicular(xs + 80F)
        dy < 0F -> return xs - 80F to perpendicular(xs - 80F)
    }
    return 360F to 526F
}

fun addArr(arr: MutableList<Int>, arr2: MutableList<Int>): MutableList<Int> {
    val result = MutableList(arr.size){0}
    for (i in arr.indices) {
        result[i] = if (arr[i] + arr2[i] > 1) 1 else arr[i] + arr2[i]
    }
    return result
}

fun cirleCollision(k: Float, b: Float,x0: Float, y0: Float, radius: Float): List<Pair<Float, Float>>? {
    val a = k.pow(2) + 1f
    val bx = -2f * (x0 - (k*b) + (k*y0))
    val c = x0.pow(2) + y0.pow(2) + b.pow(2) - radius.pow(2) - (2f * y0 * b)
    val discr = bx.pow(2) - (4f * a * c)
    if (discr < 0) return null
    val x1 = (-bx + sqrt(discr))/ 2f / a
    val x2 = (-bx - sqrt(discr))/ 2f / a
    return listOf<Pair<Float, Float>>(x1 to (x1*k) + b, x2 to (x2 * k) + b)
}

fun convertToKX(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Pair<Float, Float> {
    val (x1, y1) = p1
    val (x2, y2) = p2
    val k = (y2-y1)/(x2-x1)
    val b = -k*x1 + y1
    return k to b
}
fun convertToPerpendKX(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Pair<Float, Float> {
    val (x1, y1) = p1
    val (x2, y2) = p2
    val k = - (x2 - x1) / (y2 - y1)
    val b = y2 - k * x2
    return k to b
}

fun closestPoint(pointF: Pair<Float, Float>, list: List<Pair<Float, Float>>?): Pair<Float, Float>? {
    if (list.isNullOrEmpty()) return null
    if (list[0] == list[1]) return list.first()
    val (dx1, dy1) = list[0].first-pointF.first to list[0].second-pointF.second
    val (dx2, dy2) = list[1].first-pointF.first to list[1].second-pointF.second
    return if (dx1.pow(2)+dy1.pow(2) < dx2.pow(2) + dy2.pow(2)) list[0] else list[1]
}

fun drawArrow(p: Paint, canvas: Canvas?, pointF: Pair<Float, Float>, crossPoint: Pair<Float, Float>) {
    val arrowWidth = 4f
    val arrowHeight = 10f
    val (kL, bL) = convertToKX(pointF, crossPoint)
    val midPointF =
        closestPoint(pointF, cirleCollision(kL, bL, crossPoint.first, crossPoint.second, arrowHeight))
            ?: return
    val (kP, bP) = convertToPerpendKX(pointF, midPointF)
    val drawPoints = cirleCollision(kP, bP, midPointF.first, midPointF.second, arrowWidth)
        ?: return
    val path = Path()
    path.moveTo(crossPoint.first, crossPoint.second)
    drawPoints.forEach{
        path.lineTo(it.first, it.second)
    }
    path.lineTo(crossPoint.first, crossPoint.second)
    canvas?.drawPath(path, p)
}

fun drawFinal(p: Paint, p1: Pair<Float, Float>, p2: Pair<Float, Float>, canvas: Canvas?) {
    val radius = 50f
    val (x1, y1) = p1
    val (x2, y2) = p2
    //canvas?.drawLine(x1, y1, x2, y2, p)
    val (kL, bL) = convertToKX(p1, p2)
    val midPoint =  closestPoint(p1, cirleCollision(kL, bL, x2, y2, radius)) ?: return
    canvas?.drawLine(x1, y1, midPoint.first, midPoint.second, p)
    drawArrow(p, canvas, p1, midPoint)
}

fun plainAncor(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Pair<Float, Float>? {
    val distance = 5f
    val (x1, y1) = p1
    val (x2, y2) = p2
    val (xc, yc) = (x1 + x2)/2 to (y1 + y2)/2
    if (x2-x1 == 0f) return (if (y2-y1 > 0f) x1 + distance else x1 - distance)  to yc
    if (y2 - y1 == 0f) return  xc to (if (x2 - x1 > 0f) y1 - distance else y1 + distance)
    val (kL, bL) = convertToPerpendKX(p1, xc to yc)
    val points = cirleCollision(kL, bL, xc, yc, distance)
    return if (y2 - y1 > 0f) points?.minBy { it.first } else points?.maxBy { it.first }
}

fun drawPlain(p1: Pair<Float, Float>, midPoint: Pair<Float, Float>?, p2: Pair<Float, Float>, canvas: Canvas?, p: Paint) {
    if (midPoint == null) return
    canvas?.drawLine(p1.first, p1.second, midPoint.first, midPoint.second, p)
    drawFinal(p, midPoint, p2, canvas)
}

fun nonZeroIndexes(arr: List<Int>) = arr.withIndex().filter { it.value!=0 }.map { it.index }


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

//draws a loop
fun loop(vertex: Graph.Vertex, canvas: Canvas?, b: Paint) {
    val first = vertex.coordinates.first
    val second = vertex.coordinates.second
    canvas?.drawArc( first - 50F, second - 90F, first + 50F, second, 135F, 270F, false, b)
}

//connects 2 points depending on their position
fun interconnect(point1: Pair<Float, Float>, point2: Pair<Float, Float>, canvas: Canvas?, p: Paint, b: Paint = p) {

    if (!collide(point1, point2, points)) {
        //drawFinal(cy, point1, point2,canvas)
        val (kx, bx) = convertToKX(point1, point2)
        val p1 = closestPoint(point2, cirleCollision(kx, bx, point1.first, point1.second, 50f))!!
        drawPlain(p1, plainAncor(point1, point2), point2, canvas, p)
    }
    else {
        val midDot = makeAnchorPoint(point1, point2)
        val (kx, bx) = convertToKX(point1, midDot)
        val p1 = closestPoint(midDot, cirleCollision(kx, bx, point1.first, point1.second, 50f))!!
        canvas?.drawLine(p1.first, p1.second ,midDot.first, midDot.second ,b)
        drawFinal(b, midDot, point2, canvas)
    }
}
//the main drawing function
fun drawPoints(list: List<Graph.Vertex>, radius: Float, canvas: Canvas?, p: Paint, edges: List<Graph.Edge>) {
    p.textSize = 18f
//    canvas?.drawRGB(255, 255, 255)
    for (index in list.indices) {
        val vert = list[index]
        val (x, y) = vert.coordinates
        val i = vert.num
        if (list[index].bfsOrder != 0) {
            if (vert.parent?.coordinates != null) {
                interconnect(vert.parent!!.coordinates, vert.coordinates, canvas,
                    p.apply {
                        strokeWidth = 7f
                        color = Color.rgb(20, 23, 60)
                    },
                    p.apply {
                        strokeWidth = 7f
                        color = Color.rgb(160, 141, 81)
                    }
                )
            }
            p.color = when (vert.status) {
                Status.NEW -> Color.LTGRAY
                Status.CLOSED -> Color.rgb(0, 87, 75)
                Status.PASSED -> Color.rgb(0, 133, 119)
            }
            canvas?.drawCircle(x, y, radius, p)
            canvas?.drawText("${i + 1}", x, y, p.apply { color = Color.BLACK })
            canvas?.drawCircle(x + 40, y, 20f, p.apply { color = Color.rgb(160, 141, 81) })
            canvas?.drawText(
                "${list[index].bfsOrder}",
                x + 36,
                y + 4,
                p.apply { color = Color.BLACK })
        } else {
            p.color = when (vert.status) {
                Status.NEW -> Color.LTGRAY
                Status.CLOSED -> Color.rgb(0, 87, 75)
                Status.PASSED -> Color.rgb(0, 133, 119)
            }

            canvas?.drawCircle(x, y, radius, p)
            canvas?.drawText("${i + 1}", x, y, p.apply { color = Color.BLACK })
        }
    }


    //part responsible for drawing the Karaskal algorithm


    for (i in edgesFin) {
        interconnect(i.inter.coordinates, i.outer.coordinates, canvas, p.apply { strokeWidth = 7f; color = Color.RED }, p.apply { strokeWidth = 7f; color = Color.RED })
        val point1 = i.inter.coordinates
        val point2 = i.outer.coordinates
        val (xc, yc) = plainAncor(point1, point2) ?: 0f to 0f
        if (!collide(point1, point2, points)) {
            canvas?.drawCircle(xc, yc, RADIUS, p.apply { color = Color.rgb(255, 140, 0) })
            canvas?.drawText(i.length.toString(), xc - 3f, yc + 2f, p.apply { color = Color.BLACK })
        }
        else {
            val midDot = makeAnchorPoint(point1, point2)
            canvas?.drawCircle(midDot.first, midDot.second, RADIUS, p.apply { color = Color.rgb(255, 140, 0) })
            canvas?.drawText(i.length.toString(), midDot.first - 3f, midDot.second + 2f, p.apply { color = Color.BLACK })
        }
    }
}

