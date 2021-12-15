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
import com.neo.highlight.util.scheme.*

import com.neo.highlight.util.scheme.base.BaseScheme
import com.neo.utilskt.color
import com.neo.utilskt.dp
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configRegex()

    }

    private fun configRegex() {
        val highlight = HighlightTextWatcher()

        highlight.range = HighlightTextWatcher.RANGE.ALL

        binding.etRegex.addTextChangedListener { textRegex ->
            highlight.clearScheme()
            highlight.removeSpan(binding.etSpan.text)

            val gradientDrawable = binding.llRegexContainer.background as GradientDrawable

            try {
                val regex = Pattern.compile(textRegex.toString())
                var count = 0
                var color = Color.BLACK

                highlight.addScheme(
                    OnMatchScheme { _, _, _ ->
                        count = 0
                    }
                )

                highlight.addScheme(
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
                            //mostrar qual parte do regex bateu aqui
                        }
                    )
                )

                highlight.setSpan(binding.etSpan)

                gradientDrawable.setStroke(dp(1.5f).toInt(), theme.color(R.attr.colorPrimary))
            } catch (e: Exception) {
                gradientDrawable.setStroke(dp(1.5f).toInt(), Color.RED)
            }
        }

        binding.etSpan.movementMethod = LinkMovementMethod.getInstance()

        binding.etSpan.addTextChangedListener(highlight)
    }
}