package calculator.command.unary

import org.example.calculator.command.OperationCommand
import org.example.calculator.command.unary.FractionOperation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FractionOperationTest {

    @Test
    fun given_validInput_when_execute_then_success() {

        // given
        val input = 40.0
        val command: OperationCommand = FractionOperation(input)

        // when
        val expect = 1 / input
        val result = command()

        // then
        assertEquals(expect, result)
    }

    @Test
    fun given_validInputWithInvalidResult_when_execute_then_fail() {

        // given
        val input = 40.0
        val command: OperationCommand = FractionOperation(input)

        // when
        val expect = 2 / input
        val result = command()

        // then
        assertNotEquals(expect, result)
    }

    @Test
    fun given_invalidInputWithZero_when_execute_then_throw_exception() {
        // given
        val input = 0.0
        val command: OperationCommand = FractionOperation(input)

        // when
        val result = org.junit.jupiter.api.assertThrows<ArithmeticException> { command() }

        // then
        assertEquals(ArithmeticException::class, result::class)
    }
}