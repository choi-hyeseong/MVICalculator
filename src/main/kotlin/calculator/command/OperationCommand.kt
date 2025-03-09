package org.example.calculator.command

/**
 * 연산을 수행하는 커맨드의 인터페이스
 */
interface OperationCommand {

    /**
     * 연산 결과 수행시 double - 소수점으로 나타냄
     */
    operator fun invoke() : Double
}