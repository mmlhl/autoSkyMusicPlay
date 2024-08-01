package me.mm.sky.auto.music.floatwin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

object FloatViewModel : ViewModel() {
    private val _floatState = MutableStateFlow(FloatState())
    val floatState = _floatState
    fun updateFloatState(floatSateEnum: FloatSateEnum) {
        _floatState.value = _floatState.value.copy(
            floatSateEnum = floatSateEnum
        )


    }
}