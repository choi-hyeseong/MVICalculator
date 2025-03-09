package org.example.calculator.command.unary

import kotlin.math.sin

/**
 * sin 함수 수행 - example
 */
class SinOperation(input: Double) : UnaryOperationCommand(input) {

    override fun process(input: Double): Double {
        return sin(input)
    }
}