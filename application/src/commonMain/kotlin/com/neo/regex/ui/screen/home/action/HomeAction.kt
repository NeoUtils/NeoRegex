package com.neo.regex.ui.screen.home.action

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.domain.Target

sealed class HomeAction {

    data class TargetChange(
        val target: Target
    ) : HomeAction()

    data class UpdateRegex(
        val input: TextFieldValue
    ) : HomeAction()

    class UpdateText(
        val input: TextFieldValue
    ) : HomeAction()

    sealed class History : HomeAction() {

        abstract val target: Target?

        data class Undo(
            override val target: Target? = null
        ) : History()

        data class Redo(
            override val target: Target? = null
        ) : History()
    }
}