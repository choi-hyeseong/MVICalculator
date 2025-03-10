package org.example.calculator.command.bin

/**
 * 나머지 계산하는 커맨드
 */
class ModOperation(input: Double, target: Double) : BinaryOperationCommand(input, target) {
    /**
     * 해당 나머지 계산을 처리함.
     * @throws ArithmeticException 0으로 나누는경우 발생합니다.
     */
    override fun process(input: Double, target: Double): Double {
        // 0으로 나누기 처리
        if (target == 0.0)
            throw ArithmeticException("divide with zero")
        return input % target
    }
}