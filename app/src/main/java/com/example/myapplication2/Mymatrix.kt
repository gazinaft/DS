package com.example.myapplication2


import kotlin.random.Random


data class MyMatrix (val width: Int) {
    private val skeleton: MutableList<MutableList<Int>> = MutableList(width) { MutableList(width) { 0 } }
    var isSym = false


    operator fun set(row: Int, column: Int, value: Int) {
        skeleton[row][column] = value
    }

    operator fun get(row: Int, column: Int): Int = this.skeleton[row][column]
    operator fun get(row: Int): List<Int> = this.skeleton[row]



    operator fun plus(other: MyMatrix): MyMatrix {
        val res = MyMatrix(this.width)
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices) {
                res[i, j] = this[i, j] + other[i, j]
            }
        }
        return res
    }

    fun transponate(): MyMatrix {
        val copy = MyMatrix(this.width)
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices) {
                copy[i, j] = this[j, i]
            }
        }
        return copy
    }

    override fun toString(): String {
        val res = StringBuilder()
        for (i in this.skeleton.indices) {
            res.append(
                """${this.skeleton[i]}
                |
            """.trimMargin()
            )
        }
        return res.toString()
    }
    fun unOriented(): MyMatrix {
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices){
                if (j > i) this[i, j] = 0
            }
        }
        return this
    }

    private fun postSym(): MyMatrix {
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices)
                if (this[i, j] > 1) this[i, j] = 1
        }
        return this
    }


    fun symmetric(): MyMatrix {
        this.isSym = true
        return (this + transponate()).postSym()
    }

    companion object {
        fun generateMatrix(seed: Int, width: Int): MyMatrix {
            val res = MyMatrix(width)
            val rand = Random(seed)
            for (i in 0 until width) {
                for (j in 0 until width) {
                    res[i, j] = rand.nextInt(0, 2)
                }
            }
            return res
        }
    }
}
