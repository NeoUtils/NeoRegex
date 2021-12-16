package com.neo.regex

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import androidx.core.widget.addTextChangedListener
import com.neo.highlight.util.listener.HighlightTextWatcher
import com.neo.regex.databinding.ActivityMainBinding
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import com.neo.highlight.core.Highlight
import com.neo.highlight.util.scheme.*

import com.neo.highlight.util.scheme.base.BaseScheme
import com.neo.utilskt.color
import com.neo.utilskt.dialog
import com.neo.utilskt.dp
import com.neo.utilskt.runOnMainThread
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var matchersHighlight: HighlightTextWatcher
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configRegexMatchers()
        configRegexHighlighting()

    }

    private fun configRegexHighlighting() {
        val regexHighlight = HighlightTextWatcher()

        regexHighlight.range = HighlightTextWatcher.RANGE.ALL

        regexHighlight.addScheme(

            ColorScheme(
                Pattern.compile("\\|"),
                color(R.color.alternation)
            ),
            ColorScheme(
                Pattern.compile("\\[\\^|[\\[\\]]"),
                color(R.color.character_set_foreground)
            ),
            ColorScheme(
                Pattern.compile("[()]"),
                color(R.color.group_foreground)
            ),
            ColorScheme(
                Pattern.compile("\\\\[\\w\\W]"),
                color(R.color.escaped_characters)
            ),
            ColorScheme(
                Pattern.compile("\\\\[wWdDsS]"),
                color(R.color.keys)
            ),
            Scope(
                Pattern.compile("[^|]+"),
                OnClickScheme { text, _, _ ->
                    highlightMatchers(text)
                },
                BackgroundScheme(color(R.color.link))
            )
        )

        binding.etRegex.movementMethod = LinkMovementMethod.getInstance()

        binding.etRegex.addTextChangedListener(regexHighlight)
    }

    private fun highlightMatchers(text: CharSequence) {

        val highlight = Highlight()

        val scope = Scope(
            Pattern.compile(text.toString()),
            BackgroundScheme(color(R.color.link))
        )

        highlight.addScheme(scope)
        highlight.setSpan(binding.etSpan)

        runOnMainThread(500) {
            binding.etSpan.text = binding.etSpan.text
        }
    }

    private fun configRegexMatchers() {
        matchersHighlight = HighlightTextWatcher()

        matchersHighlight.range = HighlightTextWatcher.RANGE.ALL

        binding.etRegex.addTextChangedListener { textRegex ->
            matchersHighlight.clearScheme()
            matchersHighlight.removeSpan(binding.etSpan.text)

            val gradientDrawable = binding.llRegexContainer.background as GradientDrawable

            try {
                val regex = Pattern.compile(textRegex.toString())
                var color = Color.BLACK
                var count = 0

                matchersHighlight.addScheme(
                    OnMatchScheme { _, _, _ ->
                        count = 0
                    }
                )

                matchersHighlight.addScheme(
                    object : BaseScheme(regex) {

                        override fun getSpan(text: CharSequence, start: Int, end: Int): Any {

                            if (count > 250) count = 0

                            color = if (count > 220) {
                                Color.WHITE
                            } else {
                                Color.BLACK
                            }

                            val span = BackgroundColorSpan(genColor(count))

                            count += 10

                            return span
                        }

                        private fun genColor(count: Int): Int {
                            return Color.HSVToColor(floatArrayOf(count.toFloat(), 100f, 100f))
                        }
                    }.addScopeScheme(
                        object : BaseScheme(null) {
                            override fun getSpan(text: CharSequence, start: Int, end: Int): Any {
                                return ForegroundColorSpan(color)
                            }
                        },
                        OnClickScheme { text, _, _ ->
                            showRegex(text, regex)
                        }
                    )
                )

                matchersHighlight.setSpan(binding.etSpan)

                gradientDrawable.setStroke(dp(1.5f).toInt(), theme.color(R.attr.colorPrimary))
            } catch (e: Exception) {
                gradientDrawable.setStroke(dp(1.5f).toInt(), Color.RED)
            }
        }

        binding.etSpan.movementMethod = LinkMovementMethod.getInstance()

        binding.etSpan.addTextChangedListener(matchersHighlight)
    }

    private fun showRegex(text: CharSequence, regex: Pattern) {
        val regexList = regex.pattern().split("|")
        val matcher = regexList.first { text.matches(Regex(it)) }

        dialog("Regex", matcher)
    }
}