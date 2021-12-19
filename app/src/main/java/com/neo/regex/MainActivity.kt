package com.neo.regex

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import com.neo.regex.databinding.ActivityMainBinding
import androidx.activity.viewModels
import com.neo.highlight.util.listener.HighlightTextWatcher
import com.neo.highlight.util.scheme.*
import com.neo.highlight.util.scheme.base.BaseScheme
import com.neo.regex.utils.genColor
import com.neo.regex.utils.genHSV

import com.neo.utilskt.color
import com.neo.utilskt.dialog
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

        init()

    }

    private fun init() {
        configExpressionAdapter()

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

            var matches = 0

            matchersHighlight.addScheme(
                OnMatchScheme { _, _, _ ->
                    matches = 0
                }
            )

            expressions.forEach {

                it.pattern?.let { pattern ->

                    var textColor = Color.BLACK

                    matchersHighlight.addScheme(
                        object : BaseScheme(pattern) {

                            override fun getSpan(text: CharSequence, start: Int, end: Int): Any {

                                val hsv = it.hsv ?: genHSV(matches * 15, 250, true)

                                matches++

                                textColor = if (hsv > 220) {
                                    Color.WHITE
                                } else {
                                    Color.BLACK
                                }

                                val backgroundColor = if (hsv == 250) {
                                    theme.color(R.attr.colorPrimary)
                                } else {
                                    genColor(hsv)
                                }
                                return BackgroundColorSpan(backgroundColor)
                            }

                        }.addScopeScheme(
                            object : BaseScheme(null) {
                                override fun getSpan(
                                    text: CharSequence,
                                    start: Int,
                                    end: Int
                                ): Any {
                                    return ForegroundColorSpan(textColor)
                                }
                            }
                        )
                    )

                }
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
            try {
                expressionsAdapter.setAllExpressions(expressions)
            } catch (e: Exception) {
                dialog("Error", e.message ?: "Não foi possível renderizar")
            }
        }

    }

    private fun configExpressionAdapter() {

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
            )
        )
    }
}