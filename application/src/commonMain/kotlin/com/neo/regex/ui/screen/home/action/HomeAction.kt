package com.neo.regex.ui.screen.home.action

import com.neo.regex.core.domain.model.Target
import com.neo.regex.core.domain.model.Input as InputModel

sealed class HomeAction {

    data class TargetChange(
        val target: Target
    ) : HomeAction()

    sealed class Input : HomeAction() {

        abstract val input: String

        data class UpdateText(
            override val input: String
        ) : Input()

        data class UpdateRegex(
            override val input: String
        ) : Input()
    }

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