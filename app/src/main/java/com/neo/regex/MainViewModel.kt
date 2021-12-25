package com.neo.regex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _expressions: MutableLiveData<MutableList<Expression>> by lazy {
        MutableLiveData(mutableListOf(Expression()))
    }

    val expressions: LiveData<MutableList<Expression>> get() = _expressions

    private val _update: MutableLiveData<UpdateState> by lazy {
        updateManager
        MutableLiveData(UpdateState())
    }

    val update: LiveData<UpdateState> get() = _update

    private val updateManager: UpdateManager by lazy {
        setupUpdateManager()
    }

    private fun setupUpdateManager(): UpdateManager {
        return UpdateManager(object : UpdateManager.UpdateListener {
            override fun updated() {
                _update.value = UpdateState(
                    hasUpdate = false
                )
            }

            override fun hasUpdate(
                lastVersionCode: Int,
                lastVersionName: String,
                downloadLink: String
            ) {
                _update.value = UpdateState(
                    hasUpdate = true,
                    lastVersionCode = lastVersionCode,
                    lastVersionName = lastVersionName,
                    downloadLink = downloadLink
                )
            }
        })
    }

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
