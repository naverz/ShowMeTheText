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

import com.naverz.showmethetext.view.FontStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FloatingViewActionHandler {
    private val _action = MutableSharedFlow<FloatingViewAction>()
    val action = _action.asSharedFlow()

    fun post(scope: CoroutineScope, action: FloatingViewAction) {
        scope.launch {
            _action.emit(action)
        }
    }
}

sealed class FloatingViewAction {
    data class ChangeTextSize(val size: Int) : FloatingViewAction()

    data class ChangeLineSpacing(val space: Float) : FloatingViewAction()

    data class ChangeTextWidth(val width: Int) : FloatingViewAction()

    data class ChangeBackgroundAlpha(val alpha: Int) : FloatingViewAction()

    data class ChangeFontStyle(val fontStyle: FontStyle) : FloatingViewAction()


    data class InitTextSpec(
        val size: Int,
        val lineSpace: Float,
        val width: Int,
        val alpha: Int,
        val fontStyle: FontStyle,
    ) : FloatingViewAction()

    object ShowKeyboard : FloatingViewAction()
    object MoveTop : FloatingViewAction()
    object MoveBottom : FloatingViewAction()
    object MoveLeft : FloatingViewAction()
    object MoveRight : FloatingViewAction()
}