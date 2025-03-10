# MVI Calculator
MVI 패턴을 기반으로 한 누적 계산기

![Image](https://github.com/user-attachments/assets/6b4e8dda-7447-4240-be6d-242c11d4b39f)

## Info
MVI 패턴 + Command 패턴을 이용한 구조로 작성됨

## Using
* Kotlin-Coroutines (Flow, Channel..)
* Swing
* UI - https://shgdx.tistory.com/3 참조 + 내부 로직 변화
## Structure

### Command
기본적인 수치연산 로직을 구현해놓은 클래스입니다. BinaryCommand, UnaryCommand와 같이 Command 패턴으로 구성되어 있습니다.
#### BinaryCommand
2가지 Input을 받는 커맨드입니다. 덧셈, 뺄셈, 나눗셈, 곱셈, 나머지 연산이 구현되어 있습니다.
```kotlin
/** 
 * 두개의 연산대상을 가지는 경우의 커맨드.
 * @property input 연산 기준 대상
 * @property target 계산할 대상
 */
abstract class BinaryOperationCommand(private val input : Double, private val target : Double) : OperationCommand {

    /**
     * 각 구현체가 가져할 처리 로직
     */
    protected abstract fun process(input : Double, target: Double) : Double

    override fun invoke(): Double {
        return process(input, target)
    }
}
```
각 구현체는 process를 override하여 연산을 수행합니다.
* PlusOperation
```kotlin
/**
 * 덧셈 계산하는 커맨드
 */
class PlusOperation(input: Double, target: Double) : BinaryOperationCommand(input, target) {
    override fun process(input: Double, target: Double): Double {
        return input + target
    }
}
```
#### UnaryCommand
1개의 input을 받는 커맨드입니다. sin, sqrt, square, fraction(분수화)를 구현했습니다.
```kotlin
/**
 * 한개의 연산대상을 가지는 경우의 커맨드.
 * @property input 단일 연산대상
 */
abstract class UnaryOperationCommand(private val input : Double) : OperationCommand {

    /**
     * 각 구현체가 가져할 처리 로직
     */
    protected abstract fun process(input : Double) : Double

    override fun invoke(): Double {
        return process(input)
    }
}
```
해당 클래스도 동일하게 process를 override해서 처리할 수 있습니다.

### Factory
각 Command가 상속/구현 구조로 작성되었기 때문에 공통 클래스인 OperationCommand로 리턴하는 팩토리를 작성할 수 있습니다.
```kotlin

class UnaryCommandFactory private constructor() {

    companion object {
        val instance : UnaryCommandFactory = UnaryCommandFactory()
    }

    fun provide(operation : String, input : Double) : UnaryOperationCommand {
        return when (operation) {
            "¹/x" -> FractionOperation(input)
            "x²" -> SquareOperation(input)
            "²√x" -> SqrtOperation(input)
            else -> throw IllegalArgumentException("not supported")
        }
    }
    
}
```
다만, 내부 로직상 unary는 input 1개, bin은 input 2개를 받으므로 공용 클래스인 operationCommand를 쓰기 보단, 적절하게 각 abstract class인 Unary / Binary Command를 리턴하는 팩토리를 생성하였습니다.

### ViewModel
```kotlin
/**
 * MVI 패턴에서의 ViewModel
 */
abstract class MVIViewModel<S : IState, I : IIntent, E : IEffect>(initial: S) {

    // 뷰가 처리할 상태
    private val _state: MutableStateFlow<S> = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    // 뷰에게 전달될 이펙트
    private val _effect: Channel<E> = Channel()
    val effect: Flow<E> = _effect.consumeAsFlow()

    // 현재 모델의 상태
    val currentState: S
        get() = _state.value

    /**
     * View -> VM으로 인텐트 전달
     * @param intent 전달된 인텐트
     */
    fun sendIntent(intent: I) {
        handleIntent(intent)
    }

    /**
     * 뷰로 이펙트를 전달합니다.
     * @param effect 전달될 이펙트
     */
    suspend fun sendEffect(effect: E) {
        _effect.send(effect)
    }

    /**
     * 해당 VM에서 처리할 인텐트 로직
     * @param intent 입력된 인텐트
     */
    abstract fun handleIntent(intent: I)

    /**
     * MVI에서 Model은 불변이므로 새롭게 정의된 상태를 할당하는 reduce
     * @param reducer 해당 모델의 상태를 새롭게 정의하여 생성합니다.
     */
    protected fun reduce(reducer: S.() -> S) {
        _state.value = _state.value.reducer()
    }


}

/**
 * MVI State
 */
interface IState

/**
 * MVI Intent
 */
interface IIntent

/**
 * MVI SideEffect
 */
interface IEffect
```
기본적인 MVI 패턴에서의 ViewModel입니다. 알림과 같은 SideEffect는 계속해서 갱신되는 Hot-Stream인 Channel로, 상태를 지속적으로 유지하고 필요할때마다 제공되야 하는 State는 StateFlow로 구성하였습니다.

만약 좀더 갱신되어야 한다면 Intent의 손실을 막기 위해 SharedFlow로 작성하여 내부에서 안전하게 저장해두는것도 좋을것 같습니다. (0개 이상 저장가능)

* StateFlow (여러명이서 받을 수 있음. 1개이상 보증) - 상태 저장에 적합 (Android에서 화면회전시 새롭게 observe 되더라도 상태 제공해줘야 함)
* SharedFlow (여러명이서 받을 수 있음. 0개이상) - 내부적으로 정보 저장에 적합하나, 현재는 그냥 처리해도 될듯하여 핸들링함
* Channel (Hot-Stream, 1명만) - 1회성 이벤트에 적합함. 만약 여러명이 observe가 가능하다면 화면 회전시 그만큼 더 Alert가 발생함. Hot-Stream이므로 1회성 구현에 더 적합하기도 함

```kotlin


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

```
State, Intent, SideEffect 구현체입니다. 일부 인텐트는 중복되었다 간주 할 수 있으므로 하나로 합쳐도 좋을 것 같습니다. (사칙연산 -> BinaryIntent(Char)형식으로)

만약 계산기의 정보를 서버에 저장해야 한다(할일은 없겠지만..) 하면 Model을 생성한 뒤, Repository 패턴으로 접근하면 될것 같습니다.
State는 뷰를 위한 Model이기 때문에, State를 바로 Model로써 사용하면 안되기 때문입니다.

```kotlin
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
  ```
이렇게 구현된 인텐트는 handleIntent를 override 함으로써 처리됩니다.
### View
MVI 패턴에서의 View는 MVVM 패턴과 동일하게 Flow / LiveData를 observe한 뒤, 처리하는 방식으로 구성됩니다.
```kotlin
    // observe
        viewModel.state.onEach {
            // result
            result.text = it.currentInput
            // exp
            expression.text = it.previousOperation.joinToString(separator = "")
        }.launchIn(CoroutineScope(Dispatchers.Default))

        viewModel.effect.onEach {
            when(it) {
                is CalculatorEffect.CreateAlert -> JOptionPane.showMessageDialog(mainPanel, it.message, "경고", JOptionPane.WARNING_MESSAGE)
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))

```
현재 프로젝트는 Swing을 사용했기 때문에, Android에서 사용하는 Main Dispatcher를 사용할경우 해당 Dispatcher가 없다고 오류가 발생합니다.

따라서 Default Dispatcher를 State Listening에 사용하였고, SideEffect의 경우 IO Dispatcher를 사용하였는데, 만약 Swing또한 안드로이드처럼 Main Thread에서만 UI 갱신 / Alert등을 발생시킬 수 있다면 Default Dispatcher를 사용하거나, MainThread에 맞는 Custom Dispatcher를 만들어야 할 것 같습니다.
