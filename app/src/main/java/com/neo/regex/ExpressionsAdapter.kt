package com.neo.regex

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.neo.highlight.core.Highlight
import com.neo.highlight.core.Scheme
import com.neo.regex.databinding.ItemExpressionBinding
import com.neo.regex.util.genColor
import com.neo.regex.util.genHSV
import com.neo.utilskt.color
import com.neo.utilskt.dp
import java.util.regex.Pattern

class ExpressionsAdapter : RecyclerView.Adapter<ExpressionsAdapter.Holder>() {

    private var expressions: List<Expression> = listOf()
    private val highlight by lazy { Highlight() }

    private var moreExpressionListener: (() -> Unit)? = null
    private var removeExpressionListener: ((Int) -> Unit)? = null
    private var onMatchListener: ((List<Expression>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemExpressionBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val expression = expressions[position]
        val isLastItem = position == itemCount - 1
        val context = holder.itemView.context.theme

        holder.clearListeners()

        holder.moreExpressionBtnIsVisible(isLastItem)

        holder.setExpressionHighlight(highlight)

        if (itemCount > 1) {
            expression.hsv = genHSV(position * 10, true)
        } else {
            expression.hsv = null
        }

        holder.setExpression(expression.regex)

        val genRegex = {

            try {
                expression.pattern = null
                expression.pattern = Pattern.compile(expression.regex)

                expression.hsv?.let { hsv ->
                    holder.setColor(genColor(hsv))
                } ?: run {
                    holder.setColor(context.color(R.attr.colorPrimary))
                }

            } catch (e: Exception) {
                holder.showError(e.message)
            }
        }

        genRegex.invoke()

        holder.setExpressionChangeListener {
            expression.regex = it
            genRegex.invoke()
            onMatchListener?.invoke(expressions)
        }

        if (isLastItem) {
            holder.setMoreExpressionListener(moreExpressionListener)
        } else {
            holder.setMoreExpressionListener(null)
        }

    }

    override fun getItemCount(): Int {
        return expressions.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAllExpressions(expressions: MutableList<Expression>) {
        this.expressions = expressions
        notifyDataSetChanged()
        onMatchListener?.invoke(expressions)
    }

    fun setMoreExpressionListener(listener: () -> Unit) {
        moreExpressionListener = listener
    }

    fun setRemoveExpressionListener(listener: (Int) -> Unit) {
        removeExpressionListener = listener
    }

    fun attachOnRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = this
        ItemTouchHelper(ExpressionSwipe()).attachToRecyclerView(recyclerView)
    }

    fun setExpressionHighlight(vararg schemes: Scheme) {
        highlight.schemes = schemes.toList()
    }

    fun seOnMatchListener(listener: (List<Expression>) -> Unit) {
        onMatchListener = listener
    }

    class Holder(private val binding: ItemExpressionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = itemView.context
        private val gradientDrawable = binding.llRegexContainer.background as GradientDrawable

        private var highlight: Highlight? = null
        private var expressionChangeListener: ((String) -> Unit)? = null
        private var moreExpressionListener: (() -> Unit)? = null

        init {
            configView()
        }

        private fun configView() {
            itemView.layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(context.dp(4))
            }

            binding.etExpression.addTextChangedListener {
                expressionChangeListener?.invoke(it?.toString() ?: "")
                highlight?.apply {
                    removeSpan(it)
                    setSpan(it)
                }
            }

            binding.etExpression.movementMethod = LinkMovementMethod.getInstance()

            binding.ibAddExpressionBtn.setOnClickListener {
                moreExpressionListener?.invoke()
            }

        }

        fun moreExpressionBtnIsVisible(isVisible: Boolean) {
            val paddingDefault = context.dp(14)
            binding.ibAddExpressionBtn.visibility = if (isVisible) {
                binding.etExpression.setPadding(
                    paddingDefault,
                    paddingDefault,
                    0,
                    paddingDefault
                )
                View.VISIBLE
            } else {
                binding.etExpression.setPadding(paddingDefault)
                View.GONE
            }
        }

        fun setMoreExpressionListener(listener: (() -> Unit)?) {
            moreExpressionListener = listener
        }

        fun setExpressionHighlight(highlight: Highlight) {
            this.highlight = highlight
        }

        fun setExpressionChangeListener(listener: ((String) -> Unit)) {
            expressionChangeListener = listener
        }

        fun setExpression(regex: String) {
            binding.etExpression.setText(regex)
        }

        fun clearListeners() {
            expressionChangeListener = null
            moreExpressionListener = null
        }

        fun showError(message: String?) {
            gradientDrawable.setStroke(context.dp(1.5f).toInt(), Color.RED)
            binding.etExpression.error = message
        }

        fun setColor(color: Int) {
            gradientDrawable.setStroke(
                context.dp(1.5f).toInt(),
                color
            )

            binding.ibAddExpressionBtn.setColorFilter(color)
            binding.etExpression.error = null
        }
    }

    inner class ExpressionSwipe : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = if (itemCount != 1) makeFlag(ACTION_STATE_SWIPE, RIGHT) else 0

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == RIGHT) {
                removeExpressionListener?.invoke(viewHolder.adapterPosition)
                if (viewHolder is Holder) {
                    viewHolder.clearListeners()
                }
            }
        }
    }
}
