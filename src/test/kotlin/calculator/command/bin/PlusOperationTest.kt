package calculator.command.bin

import org.example.calculator.command.OperationCommand
import org.example.calculator.command.bin.PlusOperation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PlusOperationTest {

    @Test
    fun given_validInput_when_execute_then_success() {

        // given
        val input = 40.0
        val target = 50.0
        val command: OperationCommand = PlusOperation(input, target)

        // when
        val expect = input + target
        val result = command()

        // then
        assertEquals(expect, result)
    }

    @Test
    fun given_validInputWithInvalidResult_when_execute_then_fail() {

        // given
        val input = 40.0
        val target = 50.0
        val command: OperationCommand = PlusOperation(input, target)

        // when
        val expect = input - target
        val result = command()

        // then
        assertNotEquals(expect, result)
    }
}
