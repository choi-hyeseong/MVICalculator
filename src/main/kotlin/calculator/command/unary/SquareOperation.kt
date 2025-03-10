package org.example.calculator.command.unary

/**
 * 제곱 처리 커맨드
 */
class SquareOperation(input: Double) : UnaryOperationCommand(input) {
    override fun process(input: Double): Double {
        return input * input
    }
}