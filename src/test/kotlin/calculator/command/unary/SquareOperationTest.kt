package calculator.command.unary

import org.example.calculator.command.OperationCommand
import org.example.calculator.command.unary.SquareOperation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.sin

class SquareOperationTest {

    @Test
    fun given_validInput_when_execute_then_success() {

        // given
        val input = 90.0
        val command : OperationCommand = SquareOperation(input)

        // when
        val expect = input * input
        val result = command()

        // then
        assertEquals(expect, result)
    }

    @Test
    fun given_validInputWithInvalidResult_when_execute_then_fail() {

        // given
        val input = 90.0
        val command : OperationCommand = SquareOperation(input)

        // when
        val expect = sin(45.0)
        val result = command()

        // then
        assertNotEquals(expect, result)
    }
}