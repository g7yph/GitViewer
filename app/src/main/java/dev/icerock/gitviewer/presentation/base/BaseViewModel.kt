package dev.icerock.gitviewer.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal abstract class BaseViewModel<State : Any, Action, Event>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(value = initialState)
    private val _uiAction = MutableSharedFlow<Action?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    protected var uiState: State
        get() = _uiState.value
        set(value) {
            _uiState.update { value }
        }

    protected var uiAction: Action?
        get() = _uiAction.replayCache.last()
        set(value) {
            _uiAction.tryEmit(value)
        }

    fun uiStates(): StateFlow<State> = _uiState.asStateFlow()
    fun uiActions(): SharedFlow<Action?> = _uiAction.asSharedFlow()

    abstract fun onEvent(uiEvent: Event)

    fun clearAction() {
        uiAction = null
    }
}
