package calculator.view

import org.example.calculator.view.isInputEmpty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.swing.JLabel

class CalculatorViewKtTest {

    @Test
    fun given_zeroNumberTextLabel_when_checkInputIsEmpty_then_returnTrue() {
        // given
        val label : JLabel = JLabel("0")

        // when
        val result = label.isInputEmpty()

        // then
        assertTrue(result)
    }

    @Test
    fun given_emptyTextLabel_when_checkInputIsEmpty_then_returnTrue() {
        // given
        val label : JLabel = JLabel("")

        // when
        val result = label.isInputEmpty()

        // then
        assertTrue(result)
    }

    @Test
    fun given_AnyTextLabel_when_checkInputIsEmpty_then_returnFalse() {
        // given
        val label : JLabel = JLabel("1")

        // when
        val result = label.isInputEmpty()

        // then
        assertFalse(result)
    }
}