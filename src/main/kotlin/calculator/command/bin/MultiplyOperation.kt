package org.example.calculator.command.bin

/**
 * 곱셈 계산하는 커맨드
 */
class MultiplyOperation(input: Double, target: Double) : BinaryOperationCommand(input, target) {
    override fun process(input: Double, target: Double): Double {
        return input * target
    }
}