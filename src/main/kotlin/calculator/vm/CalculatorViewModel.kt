package org.example.calculator.vm

import org.example.calculator.view.isInputEmpty
import java.awt.event.ActionListener
import javax.swing.JLabel

/**
 * 계산기 뷰와 모델 사이 로직 처리하는 뷰모델
 */
class CalculatorViewModel(initial: CalculatorState) : MVIViewModel<CalculatorState, CalculatorIntent, CalculatorEffect>(initial) {

    override fun handleIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.InputNumberIntent -> handleInputNumber(intent.number)
            CalculatorIntent.InverseNumberIntent -> handleInverseNumber()
            CalculatorIntent.DeleteOneNumberIntent -> handleDeleteOneNumber()
        }
    }

    // 입력값 한개 삭제 구현
    private fun handleDeleteOneNumber() {
        val currentText = currentState.currentInput

        // 입력값이 없는경우 리턴
        if (currentText.isEmpty())
            return

        var nextInput = currentText.substring(0, currentText.length - 1)
        // 부호부 있는경우 삭제
        if (nextInput == "-")
            nextInput = ""
        reduce { copy(currentInput = nextInput) }

    }

    // 숫자 버튼 클릭시 뷰에 띄우기
    private fun handleInputNumber(input : Char) {
        val currentText = currentState.currentInput

        // 점이 있는경우 추가 안되게
        if (currentText.contains(".") && input == '.')
            return
        // 아무 값도 없는 초기인 경우 및 입력값이 소수점이 아닌경우 해당값 할당
        val nextInput : String = if (currentText.isEmpty())
            input.toString()
        // 이미 있는경우 추가
        else
            currentText + input.toString()
        reduce { copy(currentInput = nextInput)}

    }


    // 입력값 반전 버튼 구현
    private fun handleInverseNumber()  {
        val currentText = currentState.currentInput
        // 입력값이 없는경우 리턴
        if (currentText.isEmpty())
            return
        // 소수점만 있는경우 리턴
        if (currentText == ".")
            return

        // 반전
        val nextInput = if (currentText.startsWith("-"))
            currentText.substring(1)
        else
            "-$currentText"
        // 할당
        reduce { copy(currentInput = nextInput) }

    }

}

/**
 * MVI의 State 구현체
 * @property currentInput 현재 계산식의 입력값입니다.
 * @property accumulate 이전 계산의 누적된 값입니다.
 * @property operator 계산할 부호입니다.
 */
data class CalculatorState(val currentInput: String, val accumulate: Int, val operator: Char?) : IState {

    companion object {
        fun initial() = CalculatorState("", 0, null)
    }
}

/**
 * MVI의 Intent 구현체
 */
sealed class CalculatorIntent : IIntent {
    /**
     * 유저가 숫자 키패드에서 입력한경우의 인텐트 (0~9 / .)
     */
    data class InputNumberIntent(val number : Char) : CalculatorIntent()

    /**
     * 유저가 반전 버튼을 누르는 경우
     */
    data object InverseNumberIntent : CalculatorIntent()

    /**
     * 유저가 하나 삭제 버튼을 누르는 경우
     */
    data object DeleteOneNumberIntent : CalculatorIntent()
}

/**
 * MVI의 SideEffect 구현체
 */
sealed class CalculatorEffect : IEffect {
    /**
     * SideEffect - 1, Alert 생성하기
     * @property message 전달될 메시지
     */
    data class CreateAlert(val message: String) : CalculatorEffect()
}