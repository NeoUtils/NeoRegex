package com.neo.regex.core.sharedui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import com.neo.regex.core.sharedui.extension.getBoundingBoxes
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.core.sharedui.model.MatchBox
import com.neo.regex.designsystem.theme.Blue100
import com.neo.regex.designsystem.theme.NeoTheme.dimensions

@Composable
actual fun TextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier,
    onFocusChange: (FocusState) -> Unit,
    matches: List<Match>,
    textStyle: TextStyle,
) = Column(modifier) {

    val mergedTextStyle = typography.bodyLarge.copy(
        fontFamily = FontFamily.Monospace
    ).merge(textStyle)

    val scrollState = rememberScrollState()

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    val interactionSource = remember { MutableInteractionSource() }

    var pressedMatchOffset by remember { mutableStateOf<Offset?>(null) }

    var selectedMatch by remember { mutableStateOf<Match?>(null) }

    LaunchedEffect(interactionSource, matches) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    pressedMatchOffset = interaction.pressPosition
                }

                is PressInteraction.Release -> {
                    selectedMatch = textLayout?.let { textLayout ->
                        matches.firstOrNull { match ->
                            textLayout
                                .getBoundingBoxes(
                                    match.range.first,
                                    match.range.last
                                )
                                .any {
                                    it.contains(interaction.press.pressPosition)
                                }
                        }
                    }

                    pressedMatchOffset = null
                }

                is PressInteraction.Cancel -> {
                    pressedMatchOffset = null
                }
            }
        }
    }

    LaunchedEffect(matches, selectedMatch) {
        selectedMatch = matches.firstOrNull { match ->
            selectedMatch == match
        }
    }

    Row(Modifier.weight(weight = 1f, fill = true)) {
        LineNumbers(
            count = textLayout?.lineCount ?: 1,
            offset = scrollState.value,
            textStyle = TextStyle(
                lineHeight = mergedTextStyle.lineHeight,
                fontSize = mergedTextStyle.fontSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            modifier = Modifier
                .background(Color.LightGray.copy(alpha = 0.4f))
                .fillMaxHeight()
        )

        // TODO(improve): it's not performant for large text
        BasicTextField(
            value = value.copy(
                composition = null
            ),
            onValueChange = onValueChange,
            textStyle = mergedTextStyle.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            interactionSource = interactionSource,
            modifier = Modifier
                .padding(start = dimensions.tiny)
                .fillMaxSize()
                .verticalScroll(scrollState) // TODO(improve): https://github.com/NeoUtils/NeoRegex/issues/15
                .onFocusChanged(onFocusChange)
                .drawBehind {
                    val matchBoxes = textLayout?.let { textLayout ->
                        matches.flatMap { match ->
                            textLayout
                                .getBoundingBoxes(
                                    match.range.first,
                                    match.range.last
                                )
                                .map {
                                    MatchBox(
                                        match,
                                        it.deflate(
                                            delta = 0.8f
                                        )
                                    )
                                }
                        }
                    } ?: listOf()

                    matchBoxes.forEach { (_, rect) ->
                        drawRect(
                            color = Blue100,
                            topLeft = Offset(rect.left, rect.top),
                            size = Size(rect.width, rect.height)
                        )
                    }

                    val matchBox = matchBoxes.firstOrNull { (match, rect) ->
                        pressedMatchOffset?.let { offset ->
                            rect.contains(offset)
                        } ?: run {
                            selectedMatch == match
                        }
                    }

                    matchBox?.let { (_, rect) ->
                        drawRect(
                            color = Color.DarkGray,
                            topLeft = Offset(rect.left, y = rect.top - scrollState.value),
                            size = Size(rect.width, rect.height),
                            style = Stroke(
                                width = 1f
                            )
                        )
                    }
                },
            onTextLayout = {
                textLayout = it
            }
        )
    }

    AnimatedContent(
        targetState = selectedMatch,
        label = "animated_match_interaction",
        transitionSpec = {
            val showUp = fadeIn() + slideIntoContainer(SlideDirection.Up)
            val hideDown = fadeOut() + slideOutOfContainer(SlideDirection.Down)

            showUp togetherWith hideDown
        },
        contentKey = { it != null }
    ) { match ->
        if (match != null) {
            MatchDetails(
                match = match,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MatchDetails(
    match: Match,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = typography.bodyLarge
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = dimensions.small
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
    ) {

        Column(
            modifier = Modifier
                .padding(dimensions.default)
                .weight(weight = 1f)
        ) {
            Text(
                text = "match ${match.number}",
                style = textStyle.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = dimensions.small,
                )
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("range: ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(match.range.toString())
                    }
                },
                style = textStyle
            )
        }

        if (match.groups.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(dimensions.default)
                    .weight(weight = 1f)
            ) {
                Text(
                    text = "groups",
                    style = textStyle.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = dimensions.small,
                    )
                )

                match.groups.forEachIndexed { index, group ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$index: ")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                append(group)
                            }
                        },
                        style = textStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
