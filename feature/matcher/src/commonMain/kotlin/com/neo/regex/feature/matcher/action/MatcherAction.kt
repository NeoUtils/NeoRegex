package com.neo.regex.feature.matcher.action

import com.neo.regex.feature.matcher.model.Target
import com.neo.regex.feature.matcher.model.TextState

sealed class MatcherAction {

    data object Toggle : MatcherAction()

    data class TargetChange(
        val target: Target
    ) : MatcherAction()

    sealed class Input : MatcherAction() {

        abstract val textState: TextState

        data class UpdateText(
            override val textState: TextState
        ) : Input()

        data class UpdateRegex(
            override val textState: TextState
        ) : Input()
    }

    sealed class History : MatcherAction() {

        abstract val textState: Target?

        data class Undo(
            override val textState: Target? = null
        ) : History()

        data class Redo(
            override val textState: Target? = null
        ) : History()
    }
}