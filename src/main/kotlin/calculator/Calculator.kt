package org.example.calculator

import org.example.calculator.view.CalculatorView

class Calculator(private val view : CalculatorView) {

    // 계산기 보여주기
    fun show() {
        view.showCalculator()
    }
}