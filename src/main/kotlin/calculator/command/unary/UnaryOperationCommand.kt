package org.example.calculator.command.unary

import org.example.calculator.command.OperationCommand

/**
 * 한개의 연산대상을 가지는 경우의 커맨드.
 * @property input 단일 연산대상
 */
abstract class UnaryOperationCommand(private val input : Double) : OperationCommand {

    /**
     * 각 구현체가 가져할 처리 로직
     */
    protected abstract fun process(input : Double) : Double

    override fun invoke(): Double {
        return process(input)
    }
}