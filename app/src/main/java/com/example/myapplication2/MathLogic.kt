package com.example.myapplication2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


fun gY(p1: Pair<Float, Float>, p2: Pair<Float, Float>): (Float) -> Float {
    return {x: Float ->  (x - p1.first)*(p2.second-p1.second)/(p2.first-p1.first) + p1.second}
}

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
fun scaleAnchor(center: Pair<Float, Float>, koef: Float, supportDot: Pair<Float, Float>): Pair<Float, Float> {
    val dx = supportDot.first - center.first
    return (koef*dx) + center.first to  gY(center, supportDot)(center.first + (koef*dx))
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

fun convertToKX(p1: Pair<Float, Float>, p2: Pair<Float, Float>) {

}

fun nonZeroIndexes(arr: List<Int>) = arr.withIndex().filter { it.value!=0 }.map { it.index }