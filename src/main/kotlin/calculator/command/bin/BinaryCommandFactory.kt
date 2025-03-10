package org.example.calculator.command.bin

/**
 * 이진 연산 커맨드 제공하는 팩토리, 싱글톤으로 구성.
 * 싱글톤으로 구성 -> 인터페이스나 추상클래스 상속 or 구현가능
 */
class BinaryCommandFactory private constructor() {

    companion object {
        val instance = BinaryCommandFactory()
    }


    fun provide(char: Char, input: Double, target: Double): BinaryOperationCommand {
        return when (char) {
            '+' -> PlusOperation(input, target)
            '-' -> MinusOperation(input, target)
            '×' -> MultiplyOperation(input, target)
            '÷' -> DivisionOperation(input, target)
            '%' -> ModOperation(input, target)
            else -> throw IllegalArgumentException("not supported")
        }
    }

}