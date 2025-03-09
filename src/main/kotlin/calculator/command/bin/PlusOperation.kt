package org.example.calculator.command.bin

/**
 * 덧셈 계산하는 커맨드
 */
class PlusOperation(input: Double, target: Double) : BinaryOperationCommand(input, target) {
    override fun process(input: Double, target: Double): Double {
        return input + target
    }
}