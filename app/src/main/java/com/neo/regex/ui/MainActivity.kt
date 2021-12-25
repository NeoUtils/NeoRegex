package com.neo.regex.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import com.neo.regex.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import com.neo.highlight.util.listener.HighlightTextWatcher
import com.neo.highlight.util.scheme.*
import com.neo.highlight.util.scheme.base.BaseScheme
import com.neo.regex.utils.genColor
import com.neo.regex.utils.genHSV

import com.neo.utilskt.color
import com.neo.utilskt.dialog
import java.util.regex.Pattern
import android.content.Intent
import android.net.Uri
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.neo.regex.BuildConfig
import com.neo.regex.adapter.ExpressionsAdapter
import com.neo.regex.R
import com.neo.regex.model.Update
import com.neo.utilskt.visibility
import java.lang.RuntimeException


class MainActivity : AppCompatActivity() {

    //lateinit
    private lateinit var binding: ActivityMainBinding

    //val
    private val viewModel: MainViewModel by viewModels()

    //val lazy
    private val expressionsAdapter: ExpressionsAdapter by lazy {
        getExpressionAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        binding.toolbar.setOnClickListener {
            throw RuntimeException("Test Crash")
        }

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

        binding.content.etSpan.addTextChangedListener(setupMatchHighlight())
    }

    private fun setupMatchHighlight(): HighlightTextWatcher {

        val matchersHighlight = HighlightTextWatcher().apply {
            range = HighlightTextWatcher.RANGE.ALL
        }

        expressionsAdapter.seOnMatchListener { expressions ->
            matchersHighlight.clearScheme()
            matchersHighlight.removeSpan(binding.content.etSpan.text)

            var matches = 0

            matchersHighlight.addScheme(
                OnMatchScheme { _, _, _ ->
                    matches = 0
                }
            )

            expressions.forEachIndexed { index, it ->

                it.pattern?.let { pattern ->

                    var textColor = Color.BLACK

                    matchersHighlight.addScheme(
                        object : BaseScheme(pattern) {

                            override fun getSpan(text: CharSequence, start: Int, end: Int): Any {

                                val hsv = if (expressions.size == 1) {
                                    genHSV(matches * 15, 250, true)
                                } else {
                                    genHSV(index * 15, 250, true)
                                }

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

            matchersHighlight.setSpan(binding.content.etSpan)
        }

        return matchersHighlight
    }

    private fun setupView() = with(binding.content) {

        expressionsAdapter.attachOnRecyclerView(rvExpressions)

        configDrawerMenu()
    }

    private fun configDrawerMenu() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun setupObservers() {

        viewModel.expressions.observe(this) { expressions ->
            try {
                expressionsAdapter.setAllExpressions(expressions)
            } catch (e: Exception) {
                dialog("Error", e.message!!)
            }
        }

        viewModel.update.observe(this) { update ->
            with(binding.navBar) {
                val visible = update.hasUpdate != null

                if (visible) {

                    val hasUpdate = update.hasUpdate == true

                    if (hasUpdate) {
                        ivIcon.setImageResource(R.drawable.ic_update)

                        color(R.color.yellow).let { color ->
                            ivIcon.setColorFilter(color)
                            tvLastVersion.setTextColor(color)
                        }

                        val version = "v" + update.lastVersionName!!
                        tvLastVersion.text = version

                        tvMessage.text = getString(R.string.text_has_update)

                        cdUpdate.setOnClickListener {
                            val downloadLink = update.downloadLink

                            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                                param(FirebaseAnalytics.Param.ITEM_ID, it.id.toString())
                                param(
                                    FirebaseAnalytics.Param.ITEM_NAME,
                                    tvLastVersion.text.toString()
                                )
                                param(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
                            }

                            goToUrl(downloadLink!!)
                        }

                        //showUpdateDialog(update)

                    } else {
                        ivIcon.setImageResource(R.drawable.ic_checked)

                        color(R.color.green).let { color ->
                            ivIcon.setColorFilter(color)
                            tvLastVersion.setTextColor(color)
                        }

                        val version = "v" + BuildConfig.VERSION_NAME
                        tvLastVersion.text = version

                        tvMessage.text = getString(R.string.text_updated)

                        cdUpdate.setOnClickListener(null)
                    }

                    tvUpdateBtn.visibility(hasUpdate)
                }

                cdUpdate.visibility(visible)
            }
        }

    }

    private fun showUpdateDialog(update: Update) {
        val sharedPreferences =
            getSharedPreferences("update_manager", MODE_PRIVATE)

        val savedLastVersionCode =
            sharedPreferences
                .getInt("last_version_code", 1)

        if (savedLastVersionCode < update.lastVersionCode!!) {
            dialog("Update", "Você tem uma atualziação disponível")
        }

        sharedPreferences
            .edit()
            .putInt("last_version_code", update.lastVersionCode).apply()
    }

    private fun goToUrl(url: String) {
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }.onFailure {
            Firebase.crashlytics.recordException(it)
        }
    }

    private fun getExpressionAdapter(): ExpressionsAdapter {
        return ExpressionsAdapter().apply {
            configRegexHighlight(this)
        }
    }

    private fun configRegexHighlight(
        expressionsAdapter: ExpressionsAdapter
    ) {
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

