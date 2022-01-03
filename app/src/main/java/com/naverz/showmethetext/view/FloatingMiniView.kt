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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.naverz.showmethetext.App
import com.naverz.showmethetext.databinding.ViewMiniFloatingViewBinding
import com.naverz.showmethetext.dp2px
import com.naverz.showmethetext.view.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FloatingMiniView(
    context: Context,
    private val actionHandler: FloatingViewActionHandler,
    val index: Int,
    @ColorRes private val bgColor: Int

) : FrameLayout(context) {

    private val binding =
        ViewMiniFloatingViewBinding.inflate(LayoutInflater.from(context), this, true)

    val layoutParams = WindowLayoutParamsFactory.create(
        xAxis = 200.dp2px,
        yAxis = (-16).dp2px * index,
        gravity = Gravity.BOTTOM and Gravity.END
    )

    private val viewJob = Job()
    private val viewScope = CoroutineScope(Dispatchers.Main + viewJob)

    init {
        initColor()
        initViewListener()
        observeAction()
        visibleOrGone(false)
    }

    private fun initColor() = with(binding) {
        fontText.setTextColor(ContextCompat.getColor(App.context, bgColor))
        textSizeText.setTextColor(ContextCompat.getColor(App.context, bgColor))
        lineSpaceText.setTextColor(ContextCompat.getColor(App.context, bgColor))
        alphaText.setTextColor(ContextCompat.getColor(App.context, bgColor))
    }

    private fun initViewListener() {
        binding.root.setOnClickListener {
            visibleOrGone(false)

            GlobalActionHandler.post(viewScope, GlobalAction.HideMainView)
            GlobalActionHandler.post(viewScope, GlobalAction.HideAllMiniView)
            GlobalActionHandler.post(viewScope, GlobalAction.HideAllTextViewExceptTextView(index))

            GlobalActionHandler.post(viewScope, GlobalAction.StartSettingView(
                actionHandler= actionHandler,
                size = binding.textSize ?: 0,
                lineSpace = binding.lineSpace ?: 0f,
                width = binding.width ?: 0,
                alpha = binding.alpha ?: 0,
                fontStyle = binding.fontStyle ?: FontStyle.DEFAULT,
                index = index
            ))
        }

        binding.keyboardButton.setOnClickListener {
            GlobalActionHandler.post(viewScope, GlobalAction.ShowKeyboard(index))
        }
    }

    private fun observeAction() {
        viewScope.launch {
            actionHandler.action.collect {
                when (it) {
                    is FloatingViewAction.ChangeTextSize -> {
                        binding.textSize = it.size
                        binding.executePendingBindings()
                    }

                    is FloatingViewAction.ChangeLineSpacing -> {
                        binding.lineSpace = it.space
                        binding.executePendingBindings()
                    }

                    is FloatingViewAction.ChangeTextWidth -> {
                        binding.width = it.width
                        binding.executePendingBindings()
                    }

                    is FloatingViewAction.ChangeBackgroundAlpha -> {
                        binding.alpha = it.alpha
                        binding.executePendingBindings()
                    }

                    is FloatingViewAction.ChangeFontStyle -> {
                        binding.fontStyle = it.fontStyle
                        binding.executePendingBindings()
                    }

                    is FloatingViewAction.InitTextSpec -> {
                        binding.textSize = it.size
                        binding.lineSpace = it.lineSpace
                        binding.width = it.width
                        binding.alpha = it.alpha
                        binding.fontStyle = it.fontStyle

                        binding.executePendingBindings()
                    }
                }
            }
        }
    }

    fun visibleOrGone(visible: Boolean) {
        binding.root.visibility = if (visible) View.VISIBLE else View.GONE
    }
}