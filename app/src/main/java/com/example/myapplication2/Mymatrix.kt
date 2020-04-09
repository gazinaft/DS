package com.example.myapplication2


import kotlin.random.Random


data class MyMatrix (val width: Int) {
    private var skeleton: MutableList<MutableList<Int>> = MutableList(width) { MutableList(width) { 0 } }
    var isSym = false
    val copy by lazy { this.copy() }
    val access by lazy { this.transClosure() }
    val strongConnect by lazy { this.access!! * this.access!!.transponate() }
    val strongComponents by lazy { this.countStrComps() }

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

    operator fun times(other: MyMatrix): MyMatrix {
        val result = MyMatrix(this.width)
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices) {
                result[i, j] = this[i, j] * other[i, j]
            }
        }
        return result
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

    fun copy(): MyMatrix {
        val copy = MyMatrix(this.width)
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices) {
                copy[i, j] = this[i, j]
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
        val copy = this.copy()
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices){
                if (j > i) copy[i, j] = 0
            }
        }
        return copy
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

    fun compose(other: MyMatrix): MyMatrix {
        val result = MyMatrix(this.width)
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices){
                if (i == j && this[i, j] == 1 && other[i, j] == 1) {
                    val additional = other.skeleton[i].map{ x -> x }.toMutableList()
                    additional[j] = 0
                    result.skeleton[i] = addArr(result.skeleton[i], additional)
                }
                else if (this[i, j] == 1) {
                    result.skeleton[i] = addArr(result.skeleton[i], other.skeleton[j])
                }
            }
        }
        return result.postSym()
    }


    fun power(n: Int): MyMatrix {
        when (n) {
            1 -> return this.copy
            2 -> return this.copy.compose(this.copy)
            else -> return this.copy.compose(this.copy.power(n-1))
        }
    }

    fun transClosure(): MyMatrix? {
        fun transitive(depth: Int, matrix: MyMatrix): MyMatrix = if (depth == 1) matrix.copy else matrix.copy.power(depth) + transitive(depth-1, matrix)
        return transitive(6, this).postSym()
    }

    fun countStrComps(): List<List<Int>> {
        return this.strongConnect.skeleton.map { nonZeroIndexes(it) }.distinct()
    }

    fun findPath1(): MutableList<MutableList<Int>> {
        val result = mutableListOf<MutableList<Int>>()
        for (i in this.skeleton.indices) {
            for (j in this.skeleton.indices)
                if (this[i, j] == 1) result += mutableListOf(i, j)
        }
        return result
    }

    fun findPath2(): MutableList<MutableList<Int>> {
        val result = findPath1()
        val copy = mutableListOf<MutableList<Int>>()
        val second = this.power(1)
        for (elem in result) {
            val counting = elem.last()
            for (i in second.skeleton.indices){
                if (second[counting, i] == 1) copy += elem.plusElement(i).toMutableList()
            }

        }
        return copy.filterNot { it[0]==it[1] && it[1]==it[2] }.toMutableList()
    }
    fun findPath3(): MutableList<MutableList<Int>> {
        val result = findPath2()
        val copy = mutableListOf<MutableList<Int>>()
        val second = this.power(2)
        for (elem in result) {
            val counting = elem.last()
            for (i in second.skeleton.indices){
                if (second[counting, i] == 1) copy += elem.plusElement(i).toMutableList()
            }

        }
        return copy.filterNot { it[0]==it[1] && it[1]==it[2] }.toMutableList()
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
