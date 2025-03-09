package org.example

import org.example.calculator.Calculator
import org.example.calculator.view.CalculatorView

fun main() {
    val calculator = Calculator(CalculatorView())
    calculator.show()
}