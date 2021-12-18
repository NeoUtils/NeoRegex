package com.neo.regex

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import com.neo.regex.databinding.ActivityMainBinding
import androidx.activity.viewModels
import com.neo.highlight.core.Highlight
import com.neo.highlight.util.listener.HighlightTextWatcher
import com.neo.highlight.util.scheme.*
import com.neo.highlight.util.scheme.base.BaseScheme

import com.neo.utilskt.color
import com.neo.utilskt.dialog
import com.neo.utilskt.runOnMainThread
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val expressionsAdapter: ExpressionsAdapter by lazy {
        ExpressionsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configRegexMatchers()
        configRegexHighlighting()

        setupView()
        setupObservers()
        setupListeners()

    }

    private fun setupListeners() {
        expressionsAdapter.setMoreExpressionListener {
            viewModel.addExpression()
        }

        expressionsAdapter.setRemoveExpressionListener { position ->
            viewModel.removeExpression(position)
        }


        val matchersHighlight = HighlightTextWatcher().apply {
            range = HighlightTextWatcher.RANGE.ALL
        }

        expressionsAdapter.seOnMatchListener { expressions ->
            matchersHighlight.clearScheme()
            matchersHighlight.removeSpan(binding.etSpan.text)

            val isManyExpressions = expressions.size > 1
            var color = Color.BLACK
            var matchColor = 0
            var schemeColorTemp = 0

            matchersHighlight.addScheme(
                OnMatchScheme { _, _, _ ->
                    matchColor = 0
                }
            )

            expressions.forEach {

                if (schemeColorTemp > 250) schemeColorTemp = 0

                schemeColorTemp += 10

                matchersHighlight.addScheme(
                    object : BaseScheme(Pattern.compile(it.regex)) {

                        var schemeColor = schemeColorTemp

                        override fun getSpan(text: CharSequence, start: Int, end: Int): Any {


                            if (isManyExpressions) {
                                val backgroundColorSpan = BackgroundColorSpan(genColor(schemeColor))

                                color = if (matchColor > 220) {
                                    Color.WHITE
                                } else {
                                    Color.BLACK
                                }

                                return backgroundColorSpan
                            }

                            if (matchColor > 250) matchColor = 0

                            color = if (matchColor > 220) {
                                Color.WHITE
                            } else {
                                Color.BLACK
                            }

                            val backgroundColorSpan = BackgroundColorSpan(genColor(matchColor))

                            matchColor += 10

                            return backgroundColorSpan
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
                            //showRegex(text, regex)
                        }
                    )
                )
            }


            matchersHighlight.setSpan(binding.etSpan)
        }

        binding.etSpan.movementMethod = LinkMovementMethod.getInstance()

        binding.etSpan.addTextChangedListener(matchersHighlight)
    }

    private fun setupView() = with(binding) {
        expressionsAdapter.attachOnRecyclerView(rvExpressions)
    }

    private fun setupObservers() {

        viewModel.expressions.observe(this) { expressions ->
            expressionsAdapter.setAllExpressions(expressions)
        }

    }

    private fun configRegexHighlighting() {

        expressionsAdapter.setExpressionHighlight(
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
                    //highlightMatchers(text)
                },
                BackgroundScheme(color(R.color.link))
            )
        )
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

    }

    private fun showRegex(text: CharSequence, regex: Pattern) {
        val regexList = regex.pattern().split("|")
        val matcher = regexList.first { text.matches(Regex(it)) }

        dialog("Regex", matcher)
    }
}