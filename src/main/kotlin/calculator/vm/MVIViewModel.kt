package org.example.calculator.vm

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

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
    suspend fun sendIntent(intent: I) {
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