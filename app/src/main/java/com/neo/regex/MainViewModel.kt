package com.neo.regex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _expressions: MutableLiveData<MutableList<Expression>> by lazy {
        MutableLiveData(mutableListOf(Expression()))
    }

    val expressions: LiveData<MutableList<Expression>> get() = _expressions

    fun addExpression() {
        _expressions.value = _expressions.value?.apply {
            add(Expression())
        }
    }

    fun removeExpression(position: Int) {
        _expressions.value = _expressions.value?.apply {
            removeAt(position)
        }
    }

}
