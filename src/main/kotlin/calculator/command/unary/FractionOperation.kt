package org.example.calculator.command.unary

/**
 * 분수화 처리하는 커맨드
 */
class FractionOperation(input: Double) : UnaryOperationCommand(input) {

    /**
     * 분수화 처리
    * @throws ArithmeticException 입력값이 0인경우 발생합니다.
    */
    override fun process(input: Double): Double {
        // 0으로 나누기 처리
        if (input == 0.0)
            throw ArithmeticException("divide with zero")
        return 1 / input
    }
}