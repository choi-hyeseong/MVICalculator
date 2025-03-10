package calculator.command.bin

import org.example.calculator.command.bin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BinaryCommandFactoryTest {

    lateinit var factory : BinaryCommandFactory

    @BeforeEach
    fun before() {
        factory = BinaryCommandFactory.instance
    }

    // 올바른 값 주면 올바른 커맨드 리턴
    @Test
    fun givenValidInput_thenReturnValidCommand() {
        // given
        val dummyInput = 1.0
        val dummyTarget = 4.0

        // then

        // plus
        assertEquals(PlusOperation::class, factory.provide('+', dummyInput, dummyTarget)::class)
        // minus
        assertEquals(MinusOperation::class, factory.provide('-', dummyInput, dummyTarget)::class)
        // multiply
        assertEquals(MultiplyOperation::class, factory.provide('×', dummyInput, dummyTarget)::class)
        // division
        assertEquals(DivisionOperation::class, factory.provide('÷', dummyInput, dummyTarget)::class)
        // mod
        assertEquals(ModOperation::class, factory.provide('%', dummyInput, dummyTarget)::class)
    }

    // 지원하지 않는 경우
    @Test
    fun givenInvalidParam_thenThrowException() {
        val dummyInput = 1.0
        val dummyTarget = 4.0

        assertThrows<IllegalArgumentException> { factory.provide('X', dummyInput, dummyTarget) }
    }
}