package org.example.calculator.command.bin

/**
 * 나눗셈 계산하는 커맨드
 */
class DivisionOperation(input: Double, target: Double) : BinaryOperationCommand(input, target) {
    /**
     * 해당 나눗셈을 처리함.
     * @throws ArithmeticException 0으로 나누는경우 발생합니다.
     */
    override fun process(input: Double, target: Double): Double {
        // 0으로 나누기 처리
        if (target == 0.0)
            throw ArithmeticException("divide with zero")
        return input / target
    }
}