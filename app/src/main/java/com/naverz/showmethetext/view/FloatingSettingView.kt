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
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.naverz.showmethetext.R
import com.naverz.showmethetext.databinding.ViewFloatingSettingBinding
import com.naverz.showmethetext.view.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FloatingSettingView(
    context: Context
) : FrameLayout(context) {

    companion object {
        const val LINE_SPACE_MULTIPLE = 0.5f
    }

    private val binding =
        ViewFloatingSettingBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentActionHandler: FloatingViewActionHandler? = null

    private var visiblePositionButton = false

    private val viewJob = Job()
    private val viewScope = CoroutineScope(Dispatchers.Main + viewJob)

    val layoutParams = WindowLayoutParamsFactory.create(
        xAxis = 100,
        yAxis = 0,
        gravity = Gravity.BOTTOM and Gravity.END
    )

    init {
        initViewListeners()
        setFont(FontStyle.DEFAULT)
        visibleOrGone(false)
    }

    fun start(actionHandler: FloatingViewActionHandler, size: Int, lineSpace: Float, width: Int, alpha: Int) {
        currentActionHandler = actionHandler

        binding.textSizeBar.progress = size
        binding.lineSpaceBar.progress = (lineSpace / LINE_SPACE_MULTIPLE).toInt()
        binding.lineSpaceValue.text = "0.0"
        binding.textWidthBar.progress = width
        binding.alphaBar.progress = alpha

        currentActionHandler?.post(
            viewScope,
            FloatingViewAction.InitTextSpec(
                size,
                lineSpace,
                width,
                alpha,
                binding.fontStyle ?: FontStyle.DEFAULT
            )
        )

        visibleOrGone(true)
        visibleOrGonePositionView(visiblePositionButton)
    }

    private fun clear() = with(binding) {
        currentActionHandler = null

        textSizeBar.progress = 0
        textSizeValue.text = ""

        lineSpaceBar.progress = 0
        lineSpaceValue.text = ""

        textWidthBar.progress = 0
        textWidthValue.text = ""

        alphaBar.progress = 0
        alphaValue.text = ""
    }

    private fun initViewListeners() {
        binding.textSizeBar.setOnSeekBarChangeListener(OnlyProgressChangeListener { progress, fromUser ->
            if (fromUser) {
                currentActionHandler?.post(
                    viewScope,
                    FloatingViewAction.ChangeTextSize(progress)
                )
            }

            binding.textSizeValue.text = progress.toString()
        })

        binding.lineSpaceBar.setOnSeekBarChangeListener(OnlyProgressChangeListener { progress, fromUser ->
            val lineSpace = progress * LINE_SPACE_MULTIPLE

            if (fromUser) {
                currentActionHandler?.post(
                    viewScope,
                    FloatingViewAction.ChangeLineSpacing(lineSpace)
                )
            }

            binding.lineSpaceValue.text = lineSpace.toString()
        })

        binding.textWidthBar.setOnSeekBarChangeListener(OnlyProgressChangeListener { progress, fromUser ->
            if (fromUser) {
                currentActionHandler?.post(
                    viewScope,
                    FloatingViewAction.ChangeTextWidth(progress)
                )
            }

            binding.textWidthValue.text = progress.toString()
        })

        binding.alphaBar.setOnSeekBarChangeListener(OnlyProgressChangeListener { progress, fromUser ->
            if (fromUser) {
                currentActionHandler?.post(
                    viewScope,
                    FloatingViewAction.ChangeBackgroundAlpha(progress)
                )
            }

            binding.alphaValue.text = progress.toString()
        })

        binding.fontLayout.setOnClickListener {
            val nextFontStyle = when (binding.fontStyle ?: FontStyle.DEFAULT) {
                // fixme
                FontStyle.DEFAULT -> FontStyle.BOLD

                FontStyle.BOLD -> FontStyle.REGULAR
                FontStyle.MEDIUM -> FontStyle.BOLD
                FontStyle.REGULAR -> FontStyle.MEDIUM
            }

            setFont(nextFontStyle)

            currentActionHandler?.post(
                viewScope,
                FloatingViewAction.ChangeFontStyle(nextFontStyle)
            )
        }

        binding.confirmButton.setOnClickListener {
            GlobalActionHandler.post(viewScope, GlobalAction.ShowAllTextView)
            GlobalActionHandler.post(viewScope, GlobalAction.ShowAllMiniView)
            GlobalActionHandler.post(viewScope, GlobalAction.ShowMainView)
            GlobalActionHandler.post(viewScope, GlobalAction.HideDetailPositionView)

            visibleOrGone(false)

            clear()
        }

        binding.positionButton.setOnClickListener {
            visiblePositionButton = !visiblePositionButton
            visibleOrGonePositionView(visiblePositionButton)
        }

        binding.keyboardButton.setOnClickListener {
            currentActionHandler?.post(viewScope, FloatingViewAction.ShowKeyboard)
        }
    }

    private fun visibleOrGonePositionView(visible: Boolean) {
        if (visible) {
            binding.positionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.zepeto))
            GlobalActionHandler.post(viewScope, GlobalAction.ShowDetailPositionView)
        } else {
            binding.positionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            GlobalActionHandler.post(viewScope, GlobalAction.HideDetailPositionView)
        }
    }

    private fun setFont(fontStyle: FontStyle) {
        binding.fontStyle = fontStyle
    }

    private fun visibleOrGone(visible: Boolean) {
        binding.root.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        viewJob.cancel()
        super.onDetachedFromWindow()
    }
}