package com.example.myapplication2


import kotlin.random.Random


data class MyMatrix (val width: Int) {
    var skeleton: MutableList<MutableList<Int>> = MutableList(width) { MutableList(width) { 0 } }
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
                """${this.skeleton[i].joinToString(separator = " ", prefix = "[", postfix = "]")}
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
    fun sym2(): MyMatrix {
        val a = this + this.transponate()
        for (i in a.skeleton.indices){
            for (j in skeleton.indices)
                if (i == j) a[i, j] = a[i,j]/2
        }
        return a
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
        return when (n) {
            1 -> this.copy
            2 -> this.copy.compose(this.copy)
            else -> this.copy.compose(this.copy.power(n-1))
        }
    }

    fun transClosure(): MyMatrix? {
        fun transitive(depth: Int, matrix: MyMatrix): MyMatrix = if (depth == 1) matrix.copy else matrix.copy.power(depth) + transitive(depth-1, matrix)
        return transitive(6, this).postSym()
    }

    fun countStrComps(): List<List<Int>> {
        val finalium =  this.strongConnect.skeleton.map { nonZeroIndexes(it) }.distinct().toMutableList()
        if (finalium.all { it.isNotEmpty() }) return finalium
        val emptys = finalium.count { it.isEmpty() }
        val dist = (this.skeleton.indices.toList() - finalium.filter { it.isNotEmpty() }.flatten())
        println(dist)
        for (i in dist.indices) {
            for (j in finalium.indices) {
                if (finalium[j].isEmpty()) {
                    finalium[j] = listOf(dist[i])
                    break
                }
            }
        }
        return finalium
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
        fun generateMatrix(seed: Int = 9304, width: Int = 10): MyMatrix {
            val res = MyMatrix(width)
            val rand = Random(seed)
            for (i in 0 until width) {
                for (j in 0 until width) {
                    res[i, j] = kotlin.math.floor(rand.nextDouble(2.0)*(1.0 - N3*0.05 - N4*0.005 - 0.27)).toInt()
                }
            }
            return res
        }
        //generates the triangle matrix
        //0 0 0 0
        //1 0 0 0
        //1 1 0 0
        //1 1 1 0
        fun lowerTriangle(seed: Int = 9304, width: Int = 10): MyMatrix {
            val res = MyMatrix(width)
            for (i in 0 until width) {
                for (j in 0 until width) {
                    if (j > i - 1) {
                        continue
                    }
                    res[i, j] = 1
                }
            }
            return res
        }
        //Generates wages
        fun generateWages(seed: Int = 9304, width: Int = 10): MyMatrix {
            //unoriented matrix of connections
            val start = generateMatrix(seed, width).symmetric()
            //resulting matrix
            val wt = MyMatrix(width)
            val booleans = MutableList(width) { MutableList(width) {false} }
            val rand = Random(seed)
            val triangle = lowerTriangle()


            for (i in 0 until width){
                for (j in 0 until width) {
                    wt[i, j] = start[i, j] * (rand.nextDouble() * 100.0).toInt()
                    booleans[i][j] = wt[i, j] != 0
                }
            }
            for (i in 0 until width) {
                for (j in 0 until width) {
                    wt[i, j] = (if (booleans[i][j] && !booleans[j][i] || booleans[i][j] && booleans[j][i]) 1 else 0) * triangle[i, j] * wt[i, j]
                }
            }
            return wt + wt.transponate()
        }
    }
}

