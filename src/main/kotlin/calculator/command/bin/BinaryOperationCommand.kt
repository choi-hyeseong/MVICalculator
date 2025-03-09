package org.example.calculator.command.bin

import org.example.calculator.command.OperationCommand

/**
 * 두개의 연산대상을 가지는 경우의 커맨드.
 * @property input 연산 기준 대상
 * @property target 계산할 대상
 */
abstract class BinaryOperationCommand(private val input : Double, private val target : Double) : OperationCommand {

    /**
     * 각 구현체가 가져할 처리 로직
     */
    protected abstract fun process(input : Double, target: Double) : Double

    override fun invoke(): Double {
        return process(input, target)
    }
}