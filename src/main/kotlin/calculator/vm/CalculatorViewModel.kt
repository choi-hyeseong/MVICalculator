package org.example.calculator.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.calculator.command.bin.BinaryCommandFactory
import org.example.calculator.command.unary.*

/**
 * 계산기 뷰와 모델 사이 로직 처리하는 뷰모델
 */
class CalculatorViewModel : MVIViewModel<CalculatorState, CalculatorIntent, CalculatorEffect>(CalculatorState.initial()) {

    // factory instance
    private val binaryFactory : BinaryCommandFactory = BinaryCommandFactory.instance
    private val unaryFactory : UnaryCommandFactory = UnaryCommandFactory.instance

    override fun handleIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.InputNumberIntent -> handleInputNumber(intent.number)
            CalculatorIntent.InverseNumberIntent -> handleInverseNumber()
            CalculatorIntent.DeleteOneNumberIntent -> handleDeleteOneNumber()
            CalculatorIntent.ClearCurrentInputIntent -> handleClearCurrentInput()
            CalculatorIntent.ClearAllIntent -> handleClearAll()
            CalculatorIntent.FractionIntent -> handleFraction()
            CalculatorIntent.SqrtIntent ->  handleSqrt()
            CalculatorIntent.SquaredIntent -> handleSquare()
            CalculatorIntent.DivisionIntent -> handleOperator('÷')
            CalculatorIntent.MinusIntent -> handleOperator('-')
            CalculatorIntent.ModIntent -> handleOperator('%')
            CalculatorIntent.MultiplyIntent -> handleOperator('×')
            CalculatorIntent.PlusIntent -> handleOperator('+')
            CalculatorIntent.CalculateIntent -> handleCalculate()
        }
    }

    // 모든 이력 삭제
    private fun handleClearAll() {
        reduce { CalculatorState.initial() }
    }

    // 현재 입력값 전체 삭제 설정
    private fun handleClearCurrentInput() {
        reduce { copy(currentInput = "") }
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
        // 아무 값도 없는 초기인 경우 및 입력값이 소수점이 아닌경우 해당값 할당 + Not A Number / infinite 인경우도 초기화
        val nextInput : String = if (currentText.isEmpty() || currentText.toDoubleOrNull()?.isFinite() == false)
            input.toString()
        // 이미 있는경우 추가
        else
            currentText + input.toString()
        reduce { copy(currentInput = nextInput)}

    }


    // 입력값 반전 버튼 구현
    private fun handleInverseNumber()  {
        val currentText = currentState.currentInput
        // 입력값이 없는경우와 소수점인경우 리턴
        if (currentText.isEmpty() || currentText == ".")
            return

        // 반전
        val nextInput = if (currentText.startsWith("-"))
            currentText.substring(1)
        else
            "-$currentText"
        // 할당
        reduce { copy(currentInput = nextInput) }
    }

    // 분수화 처리
    private fun handleFraction() {
        handleUnary("¹/x")
    }

    // 제곱 처리
    private fun handleSquare() {
        handleUnary("x²")
    }

    // 제곱근 처리
    private fun handleSqrt() {
        handleUnary("²√x")
    }

    // 단일 연산을 통합해서 처리하는 메소드.
    private fun handleUnary(operation : String) {
        val currentText = currentState.currentInput
        // 입력값이 없는경우, 소수점만 있는 경우, 0만 있는경우 리턴
        if (currentText.isEmpty() || currentText == "." || currentText == "0")
            return

        val command = unaryFactory.provide(operation, currentText.toDouble())
        kotlin.runCatching {
            command()
        }.onSuccess {
            reduce { copy(currentInput = it.toString()) }
        }.onFailure {
            handleError(it.localizedMessage)
        }
    }

    // 연산자 입력시 처리할 메소드, 그냥 view에서 char받아도 됐을거 같기도..
    private fun handleOperator(input : Char) {
        val operator = currentState.operator
        val currentText = currentState.currentInput
        // 현재 입력값이 올바르지 않은 경우 리턴
        if (currentText == ".")
            return

        // 현재 입력값이 비어있는 경우
        if (currentText.isEmpty()) {
            if (operator != null)
                // 연산자 변경이 가능하다면 변경
                reduce { copy(operator = input, previousOperation = listOf(previousOperation[0], input.toString())) }
            return
        }

        // 이 아래로는 입력값이 올바름.

        // 만약 operator가 지정되어 있지 않은경우
        if (operator == null) {
            reduce { copy(accumulate = currentInput.toDouble(), currentInput = "", operator = input, previousOperation = listOf(currentInput, input.toString())) }
            return
        }

        // operator가 지정되어 있는경우 계산 처리
        handleCalculate()
        // 이후 operator 할당 - 기존 값 계산하면 result쪽에 등록됨. 이후 다시 operator를 처리하면 위로 올라감
        handleOperator(input)

    }

    private fun handleCalculate() {
        val operator = currentState.operator
        val currentText = currentState.currentInput
        val accumulate = currentState.accumulate
        // 현재 입력값이 올바르지 않은 경우, 연산자도 안정해진 경우, 리턴
        if (currentText.isEmpty() || currentText == "." || operator == null)
            return
        // 커맨드
        val command = binaryFactory.provide(operator, accumulate, currentText.toDouble())
        kotlin.runCatching {
            command()
        }.onSuccess {
            reduce { copy(currentInput = it.toString(), accumulate = 0.0, operator = null, previousOperation = listOf()) } // 값 할당 및 초기화
        }.onFailure {
            handleError(it.localizedMessage)
        }
    }


    // 에러처리 메소드
    private fun handleError(message : String) {
        CoroutineScope(Dispatchers.IO).launch {
            sendEffect(CalculatorEffect.CreateAlert(message))
        }
    }

}

/**
 * MVI의 State 구현체
 * @property currentInput 현재 계산식의 입력값입니다.
 * @property accumulate 이전 계산의 누적된 값입니다.
 * @property operator 계산할 부호입니다.
 * @property previousOperation 이전의 계산 결과입니다.
 */
data class CalculatorState(val currentInput: String, val accumulate: Double, val operator: Char?, val previousOperation : List<String>) : IState {

    companion object {
        fun initial() = CalculatorState("", 0.0, null, listOf())
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

    /**
     * 유저가 초기화 버튼을 누르는 경우
     */
    data object ClearCurrentInputIntent : CalculatorIntent()

    /**
     * 모든 입력 삭제
     */
    data object ClearAllIntent : CalculatorIntent()

    /**
     * 유저가 분수화 버튼 누르는 경우
     */
    data object FractionIntent : CalculatorIntent()

    /**
     * 유저가 제곱 버튼을 누르는 경우
     */
    data object SquaredIntent : CalculatorIntent()

    /**
     * 유저가 제곱근 버튼을 누르는 경우
     */
    data object SqrtIntent : CalculatorIntent()

    /**
     * 유저가 덧셈 버튼 누를때
     */
    data object PlusIntent : CalculatorIntent()

    /**
     * 뺄셈 버튼 누를때
     */
    data object MinusIntent : CalculatorIntent()

    /**
     * 곱셈버튼 누를때
     */
    data object MultiplyIntent : CalculatorIntent()

    /**
     * 나눗셈 버튼 누를때
     */
    data object DivisionIntent : CalculatorIntent()

    /**
     * 나머지 버튼 누를때
     */
    data object ModIntent : CalculatorIntent()

    /**
     * 계산 버튼 누를때
     */
    data object CalculateIntent : CalculatorIntent()
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