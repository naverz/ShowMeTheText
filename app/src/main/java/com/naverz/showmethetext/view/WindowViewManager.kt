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
import android.view.*
import android.view.WindowManager
import android.widget.Toast
import com.naverz.showmethetext.App
import com.naverz.showmethetext.R
import com.naverz.showmethetext.view.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WindowViewManager(private val context: Context = App.context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val allActiveViews = hashMapOf<Int, View>()
    private val activeMiniViews = hashSetOf<FloatingMiniView>()
    private val activeTextViews = hashSetOf<FloatingTextView>()

    private val currentVisibleView = hashSetOf<View>()

    private val mainView: FloatingMainView by lazy { FloatingMainView(App.context) }
    private val settingView: FloatingSettingView by lazy { FloatingSettingView(App.context) }
    private val detailPositionView: FloatingDetailPositionView by lazy {
        FloatingDetailPositionView(
            App.context
        )
    }

    private val bgList =
        listOf(R.color.bg1, R.color.bg2, R.color.bg3, R.color.bg4, R.color.bg5, R.color.bg6)
    private val MAX_TEXT_COUNT = bgList.size

    private var activeTextViewCount = 0

    private var onDestroyAction: (() -> Unit)? = null

    init {
        observeGlobalAction()
    }

    private fun observeGlobalAction() {
        mainView.viewScope.launch(Dispatchers.Main) {

            GlobalActionHandler.action.collect {
                when (it) {
                    is GlobalAction.UpdateWindowLayoutAction -> {
                        windowManager.updateViewLayout(it.view, it.layoutParams)
                    }

                    is GlobalAction.StartSettingView -> {
                        settingView.start(
                            actionHandler = it.actionHandler,
                            size = it.size,
                            lineSpace = it.lineSpace,
                            width = it.width,
                            alpha = it.alpha
                        )

                        detailPositionView.setActionHandler(it.actionHandler)
                    }

                    is GlobalAction.AddText -> {
                        if (enableAddingView()) {
                            mainView.visibleOrGone(false)
                            hideAllMiniView()
                            addFloatingView()
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "텍스트는 최대 ${MAX_TEXT_COUNT}개까지만 추가할 수 있습니다!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }

                    is GlobalAction.HideAllTextViewExceptTextView -> {
                        hideAllTextViewExcept(it.index)
                    }

                    is GlobalAction.RestoreAllView -> {
                        restoreAllView()
                    }

                    is GlobalAction.TurnOff -> {
                        removeAllWindowView()
                        clearAll()
                        onDestroyAction?.invoke()
                    }

                    is GlobalAction.ShowMainView -> {
                        mainView.visibleOrGone(true)
                    }

                    is GlobalAction.HideMainView -> {
                        mainView.visibleOrGone(false)
                    }

                    is GlobalAction.ShowDetailPositionView -> {
                        detailPositionView.visibleOrGone(true)
                    }

                    is GlobalAction.HideDetailPositionView -> {
                        detailPositionView.visibleOrGone(false)
                    }

                    is GlobalAction.ShowAllTextView -> {
                        showAllTextView()
                    }

                    is GlobalAction.HideAllViewExceptTextView -> {
                        hideAllViewExceptTextView(it.visibleViewIndex)
                    }

                    is GlobalAction.ShowAllMiniView -> {
                        showAllMiniView()
                    }

                    is GlobalAction.HideAllMiniView -> {
                        hideAllMiniView()
                    }

                    is GlobalAction.ShowKeyboard -> {
                        showKeyboard(it.viewIndex)
                    }
                }
            }
        }
    }

    private fun addFloatingView() {
        val actionHandler = FloatingViewActionHandler()

        val currentBgColor = bgList[activeTextViewCount]

        FloatingTextView(
            context = App.context,
            actionHandler = actionHandler,
            index = activeTextViewCount,
            bgColor = currentBgColor
        ).let {
            addViewToWindow(it, it.layoutParams)
            activeTextViews.add(it)
        }

        FloatingMiniView(
            context = App.context,
            actionHandler = actionHandler,
            index = activeTextViewCount,
            bgColor = currentBgColor
        ).let {
            addViewToWindow(it, it.layoutParams)
            activeMiniViews.add(it)
        }

        activeTextViewCount++
    }

    private fun hideAllViewExceptTextView(targetIndex: Int) {
        allActiveViews
            .filter { it.value.visibility == View.VISIBLE }
            .map { currentVisibleView.add(it.value) }

        currentVisibleView
            .filter { it !is FloatingTextView || it.index != targetIndex }
            .map { it.visibility = View.GONE }
    }

    private fun restoreAllView() {
        currentVisibleView.forEach {
            it.visibility = View.VISIBLE
        }
        currentVisibleView.clear()
    }

    private fun enableAddingView(): Boolean {
        return activeTextViewCount < MAX_TEXT_COUNT
    }

    private fun showKeyboard(index: Int) {
        activeTextViews
            .filter { it.index == index }
            .map { it.showKeyboard() }
    }

    private fun showAllMiniView() {
        activeMiniViews.forEach { it.visibleOrGone(true) }
    }

    private fun showAllTextView() {
        activeTextViews.forEach { it.visibleOrGone(true) }
    }

    private fun hideAllTextViewExcept(visibleTextIndex: Int) {
        activeTextViews
            .filter { it.index != visibleTextIndex }
            .map { it.visibleOrGone(false) }
    }

    private fun hideAllMiniView() {
        activeMiniViews.forEach { it.visibleOrGone(false) }
    }

    fun setOnDestroyListener(action: () -> Unit) {
        onDestroyAction = action
    }

    fun start() {
        addViewToWindow(mainView, mainView.layoutParams)
        addViewToWindow(settingView, settingView.layoutParams)
        addViewToWindow(detailPositionView, detailPositionView.layoutParams)
    }

    private fun addViewToWindow(view: View, layoutParams: WindowManager.LayoutParams) {
        windowManager.addView(view, layoutParams)
        allActiveViews[view.hashCode()] = view
    }

    private fun removeAllWindowView() {
        allActiveViews.forEach { (_, view) ->
            windowManager.removeView(view)
        }
    }

    private fun clearAll() {
        allActiveViews.clear()
        activeTextViews.clear()
        activeMiniViews.clear()
        currentVisibleView.clear()
    }
}