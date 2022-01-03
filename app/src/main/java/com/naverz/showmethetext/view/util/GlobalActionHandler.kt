package com.naverz.showmethetext.view.util

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

import android.view.View
import android.view.WindowManager
import com.naverz.showmethetext.view.FontStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object GlobalActionHandler {

    private val _action = MutableSharedFlow<GlobalAction>()
    val action = _action.asSharedFlow()

    fun post(scope: CoroutineScope, action: GlobalAction) {
        scope.launch {
            _action.emit(action)
        }
    }
}

sealed class GlobalAction {

    data class UpdateWindowLayoutAction(
        val view: View,
        val layoutParams: WindowManager.LayoutParams
    ) : GlobalAction()

    data class StartSettingView(
        val actionHandler: FloatingViewActionHandler,
        val size: Int,
        val lineSpace: Float,
        val width: Int,
        val alpha: Int,
        val fontStyle: FontStyle,
        val index: Int
    ) : GlobalAction()

    data class ShowKeyboard(val viewIndex: Int) : GlobalAction()
    data class HideAllViewExceptTextView(val visibleViewIndex: Int) : GlobalAction()
    data class HideAllTextViewExceptTextView(val index: Int) : GlobalAction()
    object RestoreAllView : GlobalAction()

    object AddText : GlobalAction()
    object HideAllMiniView : GlobalAction()
    object HideMainView : GlobalAction()
    object ShowMainView : GlobalAction()
    object ShowDetailPositionView : GlobalAction()
    object HideDetailPositionView : GlobalAction()
    object ShowAllMiniView : GlobalAction()
    object ShowAllTextView : GlobalAction()
    object TurnOff : GlobalAction()
}