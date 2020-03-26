package com.example.myapplication2

import android.os.Bundle
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
}
