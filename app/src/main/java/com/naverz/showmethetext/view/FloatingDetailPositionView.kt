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
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.naverz.showmethetext.databinding.ViewDetailPositionBinding
import com.naverz.showmethetext.view.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FloatingDetailPositionView(context: Context): FrameLayout(context)  {

    private val binding =
        ViewDetailPositionBinding.inflate(
            LayoutInflater.from(context), this, true
        )

    val layoutParams =
        WindowLayoutParamsFactory.create(
            xAxis = -80,
            yAxis = 150,
            gravity = Gravity.BOTTOM and Gravity.START
        )

    private val viewJob = Job()
    val viewScope = CoroutineScope(Dispatchers.Main + viewJob)

    private var currentActionHandler: FloatingViewActionHandler? = null

    init {
        visibleOrGone(false)
        initViewListener()
        setTouchListener()
    }

    private fun initViewListener() {
        binding.topButton.setOnClickListener { currentActionHandler?.post(viewScope, FloatingViewAction.MoveTop) }
        binding.bottomButton.setOnClickListener { currentActionHandler?.post(viewScope, FloatingViewAction.MoveBottom) }
        binding.leftButton.setOnClickListener { currentActionHandler?.post(viewScope, FloatingViewAction.MoveLeft) }
        binding.rightButton.setOnClickListener { currentActionHandler?.post(viewScope, FloatingViewAction.MoveRight) }
    }

    private fun setTouchListener() {
        binding.centerButton.setOnTouchListener(object : OnTouchListener {
            private var lastXAxis = 0
            private var lastYAxis = 0


            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastXAxis = layoutParams.x - event.rawX.toInt()
                        lastYAxis = layoutParams.y - event.rawY.toInt()

                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = lastXAxis + event.rawX.toInt()
                        layoutParams.y = lastYAxis + event.rawY.toInt()

                        GlobalActionHandler.post(
                            viewScope,
                            GlobalAction.UpdateWindowLayoutAction(
                                this@FloatingDetailPositionView,
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
    }

    fun setActionHandler(actionHandler: FloatingViewActionHandler) {
        currentActionHandler = actionHandler
    }

    fun visibleOrGone(visible: Boolean) {
        binding.root.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        viewJob.cancel()
        super.onDetachedFromWindow()
    }
}