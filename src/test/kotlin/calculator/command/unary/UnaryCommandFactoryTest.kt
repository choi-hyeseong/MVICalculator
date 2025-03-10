package calculator.command.unary

import org.example.calculator.command.unary.FractionOperation
import org.example.calculator.command.unary.SqrtOperation
import org.example.calculator.command.unary.SquareOperation
import org.example.calculator.command.unary.UnaryCommandFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UnaryCommandFactoryTest {

    /**
     *             "¹/x" -> FractionOperation(input)
     *             "x²" -> SquareOperation(input)
     *             "²√x" -> SqrtOperation(input)
     */

    lateinit var factory : UnaryCommandFactory

    @BeforeEach
    fun before() {
        factory = UnaryCommandFactory.instance
    }

    // 올바른 값 주면 올바른 커맨드 리턴
    @Test
    fun givenValidInput_thenReturnValidCommand() {
        // given
        val dummyInput = 1.0

        // then

        // 분수
        assertEquals(FractionOperation::class, factory.provide("¹/x", dummyInput)::class)
        // 제곱
        assertEquals(SquareOperation::class, factory.provide("x²", dummyInput)::class)
        // 제곱근
        assertEquals(SqrtOperation::class, factory.provide("²√x", dummyInput)::class)
    }

    // 지원하지 않는 경우
    @Test
    fun givenInvalidParam_thenThrowException() {
        val dummyInput = 1.0

        org.junit.jupiter.api.assertThrows<IllegalArgumentException> { factory.provide("/", dummyInput) }
    }
}