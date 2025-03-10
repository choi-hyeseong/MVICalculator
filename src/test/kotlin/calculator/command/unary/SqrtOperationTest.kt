package calculator.command.unary

import org.example.calculator.command.OperationCommand
import org.example.calculator.command.unary.SqrtOperation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class SqrtOperationTest {

    @Test
    fun given_validInput_when_execute_then_success() {

        // given
        val input = 40.0
        val command: OperationCommand = SqrtOperation(input)

        // when
        val expect = sqrt(input)
        val result = command()

        // then
        assertEquals(expect, result)
    }

    @Test
    fun given_validInputWithInvalidResult_when_execute_then_fail() {

        // given
        val input = 40.0
        val command: OperationCommand = SqrtOperation(input)

        // when
        val expect = input / 2
        val result = command()

        // then
        assertNotEquals(expect, result)
    }

    @Test
    fun given_invalidInputWithNegative_when_execute_then_throw_exception() {
        // given
        val input = -5.0
        val command: OperationCommand = SqrtOperation(input)

        // when
        val result = org.junit.jupiter.api.assertThrows<ArithmeticException> { command() }

        // then
        assertEquals(ArithmeticException::class, result::class)
    }
}