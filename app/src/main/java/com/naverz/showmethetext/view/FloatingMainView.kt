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
import com.naverz.showmethetext.databinding.ViewOverlayMainBinding
import com.naverz.showmethetext.view.util.GlobalAction
import com.naverz.showmethetext.view.util.GlobalActionHandler
import com.naverz.showmethetext.view.util.WindowLayoutParamsFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FloatingMainView(context: Context) : FrameLayout(context) {
    private val binding =
        ViewOverlayMainBinding.inflate(
            LayoutInflater.from(context), this, true
        )

    val layoutParams =
        WindowLayoutParamsFactory.create(
            xAxis = 150,
            yAxis = 150,
            gravity = Gravity.BOTTOM and Gravity.END
        )

    private val viewJob = Job()
    val viewScope = CoroutineScope(Dispatchers.Main + viewJob)

    init {
        initViewListener()
    }

    private fun initViewListener() = with(binding) {
        addButton.setOnClickListener {
            GlobalActionHandler.post(viewScope, GlobalAction.AddText)
        }

        closeButton.setOnClickListener {
            GlobalActionHandler.post(viewScope, GlobalAction.TurnOff)
        }

        invisibleButton.setOnClickListener {
            mainPanel.visibility = View.GONE
            visibleButton.visibility = View.VISIBLE

            GlobalActionHandler.post(viewScope, GlobalAction.HideAllMiniView)
        }

        visibleButton.setOnClickListener {
            visibleButton.visibility = View.GONE
            mainPanel.visibility = View.VISIBLE

            GlobalActionHandler.post(viewScope, GlobalAction.ShowAllMiniView)
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