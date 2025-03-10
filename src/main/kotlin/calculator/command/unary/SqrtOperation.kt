package org.example.calculator.command.unary

import kotlin.math.sqrt

/**
 * 제곱근 커맨드 처리
 */
class SqrtOperation(input: Double) : UnaryOperationCommand(input) {
    /**
     * 처리할때 입력값이 음수이면 에러 처리. (허수..?)
     */
    override fun process(input: Double): Double {
        if (input < 0.0)
            throw ArithmeticException("Negative sqrt")
        return sqrt(input)
    }
}