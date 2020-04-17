package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_matrix.*

class MatrixActivity : AppCompatActivity() {
    companion object {
        const val MATRIX_STR: String = "matrix_str"
        const val DEGREES = "degrees"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix)
        showMatrix()
    }
    fun showMatrix() {
        val matrix = intent.getStringExtra(MATRIX_STR)
        val degrees = intent.getStringExtra(DEGREES)
        matrixTextView.text = matrix
        degreeTextView.text = degrees
    }
    fun goToCondensation(view: View) {
        val going = Intent(this, CondGraphActivity::class.java)
        startActivity(going)
    }

    fun showMatrixes(view: View){
        val matrix = MyMatrix.generateMatrix(9304, 10)
        val myToast = Toast.makeText(this,
            """Довжина 2:
            |${matrix.power(2)}
            |Довжина 3:
            |${matrix.power(3)}
        """.trimMargin(), Toast.LENGTH_LONG)
        myToast.show()
    }

    fun showRoutes2(view: View) {
        val routes3 = MyMatrix.generateMatrix(9304, 10).findPath3()
        val text = """Довжина3:
            | $routes3
            """.trimMargin()
        val showing = Toast.makeText(this, text, Toast.LENGTH_LONG)
        showing.show()
    }
}
