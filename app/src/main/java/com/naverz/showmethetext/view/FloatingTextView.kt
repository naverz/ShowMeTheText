package com.naverz.showmethetext.view

/**
 * ShowMeTheText
 * Copyright (c) 2022-present NAVER Z Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * */

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.naverz.showmethetext.databinding.ViewFloatingTextBinding
import com.naverz.showmethetext.dp2px
import com.naverz.showmethetext.px2dp
import com.naverz.showmethetext.px2sp
import com.naverz.showmethetext.view.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

class FloatingTextView(
    context: Context,
    val actionHandler: FloatingViewActionHandler,
    val index: Int,
    @ColorRes private val bgColor: Int
) : FrameLayout(context) {

    private val binding = ViewFloatingTextBinding.inflate(LayoutInflater.from(context), this, true)

    val layoutParams =
        WindowLayoutParamsFactory.create(xAxis = 10, yAxis = 20.dp2px, gravity = Gravity.TOP)

    private val viewJob = Job()
    private val viewScope = CoroutineScope(Dispatchers.Main + viewJob)

    init {
        init()
        observeAction()
        initViewListener()

        postDelayed({
            notifyInitViewSpec()
        }, 150L)
    }

    private fun init() {
        setBgColor(bgColor)
        setAlpha(50)
        setFont(FontStyle.DEFAULT)
    }

    private fun notifyInitViewSpec() {
        GlobalActionHandler.post(viewScope, GlobalAction.HideMainView)
        GlobalActionHandler.post(viewScope, GlobalAction.HideAllMiniView)
        GlobalActionHandler.post(viewScope, GlobalAction.HideAllTextViewExceptTextView(index))
        GlobalActionHandler.post(viewScope,
            GlobalAction.StartSettingView(
                actionHandler = actionHandler,
                size = binding.targetText.textSize.px2sp,
                lineSpace = binding.targetText.lineSpacingExtra.px2sp.toFloat(),
                width = binding.targetText.measuredWidth.px2dp,
                alpha = (binding.targetText.alpha * 100).toInt(),
                fontStyle = binding.fontStyle ?: FontStyle.DEFAULT,
                index = index,
            )
        )
    }

    private fun observeAction() {
        viewScope.launch {
            actionHandler.action.collect {
                when (it) {
                    is FloatingViewAction.ChangeTextSize -> {
                        setTextSize(it.size)
                    }

                    is FloatingViewAction.ChangeLineSpacing -> {
                        setLineSpace(it.space)
                    }

                    is FloatingViewAction.ChangeTextWidth -> {
                        setTextWidth(it.width)
                    }

                    is FloatingViewAction.ChangeBackgroundAlpha -> {
                        setAlpha(it.alpha)
                    }

                    is FloatingViewAction.ChangeFontStyle -> {
                        setFont(it.fontStyle)
                    }

                    is FloatingViewAction.MoveTop -> {
                        move(xAxisDp = 0, yAxisDp = -1)
                    }

                    is FloatingViewAction.MoveBottom -> {
                        move(xAxisDp = 0, yAxisDp = 1)
                    }

                    is FloatingViewAction.MoveLeft -> {
                        move(xAxisDp = -1, yAxisDp = 0)
                    }

                    is FloatingViewAction.MoveRight -> {
                        move(xAxisDp = 1, yAxisDp = 0)
                    }

                    is FloatingViewAction.ShowKeyboard -> {
                        showKeyboard()
                    }
                }
            }
        }
    }

    private fun setBgColor(bgColor: Int) {
        binding.targetText.setBackgroundResource(bgColor)
        binding.targetEditText.setBackgroundResource(bgColor)
    }

    private fun setFont(fontStyle: FontStyle) {
        binding.fontStyle = fontStyle
        if (fontStyle.fontRes > 0) {
            binding.targetText.typeface = ResourcesCompat.getFont(context, fontStyle.fontRes)
            binding.targetEditText.typeface = ResourcesCompat.getFont(context, fontStyle.fontRes)
        }
    }

    private fun setTextSize(sizeSp: Int) {
        binding.targetText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp.toFloat()
        )
        binding.targetEditText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp.toFloat()
        )
    }

    private fun setAlpha(alpha: Int) {
        val newAlpha = alpha / 100f
        binding.targetText.alpha = newAlpha
        binding.targetEditText.alpha = newAlpha
    }

    private fun setLineSpace(lineSpaceSp: Float) {
        val typedValue = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            lineSpaceSp,
            resources.displayMetrics
        )

        binding.targetText.setLineSpacing(typedValue, 1.0f)
        binding.targetEditText.setLineSpacing(typedValue, 1.0f)
    }

    private fun setTextWidth(widthDp: Int) {
        binding.targetText.updateLayoutParams {
            width = widthDp.dp2px
        }
        binding.targetEditText.updateLayoutParams {
            width = widthDp.dp2px
        }
    }

    private fun move(xAxisDp: Int, yAxisDp: Int) {
        layoutParams.x = layoutParams.x + xAxisDp.dp2px
        layoutParams.y = layoutParams.y + yAxisDp.dp2px

        GlobalActionHandler.post(
            viewScope,
            GlobalAction.UpdateWindowLayoutAction(
                this@FloatingTextView,
                layoutParams
            )
        )
    }

    private fun initViewListener() {
        setOnTouchListener(object : OnTouchListener {
            private var lastViewXAxis = 0
            private var lastViewYAxis = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        lastViewXAxis = layoutParams.x - event.rawX.toInt()
                        lastViewYAxis = layoutParams.y - event.rawY.toInt()
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = lastViewXAxis + event.rawX.toInt()
                        layoutParams.y = lastViewYAxis + event.rawY.toInt()

                        GlobalActionHandler.post(
                            viewScope,
                            GlobalAction.UpdateWindowLayoutAction(
                                this@FloatingTextView,
                                layoutParams
                            )
                        )

                        true
                    }

                    MotionEvent.ACTION_UP -> true

                    else -> false
                }
            }
        })

        binding.doneButton.setOnClickListener {
            binding.targetText.text = binding.targetEditText.text.toString()
            GlobalActionHandler.post(viewScope, GlobalAction.RestoreAllView)
            toggleEditText(false)
        }
    }

    fun showKeyboard() {
        GlobalActionHandler.post(viewScope, GlobalAction.HideAllViewExceptTextView(index))
        toggleEditText(true)
    }

    private fun changeFocusableFlag(focusable: Boolean) {
        layoutParams.flags = if (focusable) {
            WindowLayoutParamsFactory.FOCUSABLE_FLAG
        } else {
            WindowLayoutParamsFactory.NOT_FOCUSABLE_FLAG
        }

        GlobalActionHandler.post(viewScope, GlobalAction.UpdateWindowLayoutAction(this, layoutParams))
    }

    private fun toggleEditText(visibleEdit: Boolean) {
        binding.targetEditText.visibility = if (visibleEdit) View.VISIBLE else View.GONE
        binding.targetText.visibility = if (visibleEdit) View.GONE else View.VISIBLE
        binding.doneButton.visibility = if (visibleEdit) View.VISIBLE else View.GONE

        changeFocusableFlag(visibleEdit)

        if (visibleEdit) {
            binding.targetEditText.setSelection(binding.targetText.text.length)
            ImeUtils.showKeyboard(binding.targetEditText)
        } else {
            ImeUtils.hideKeyboard(binding.targetEditText)
        }
    }

    fun visibleOrGone(visible: Boolean) {
        binding.root.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        viewJob.cancel()
        super.onDetachedFromWindow()
    }
}