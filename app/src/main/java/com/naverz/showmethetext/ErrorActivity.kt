package com.naverz.showmethetext

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

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.naverz.showmethetext.databinding.ActivityErrorBinding
import com.naverz.showmethetext.view.util.ClipBoardUtils

class ErrorActivity : AppCompatActivity() {

    private var _binding: ActivityErrorBinding? = null
    private val binding: ActivityErrorBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_error)

        binding.logText.text = CustomActivityOnCrash.getStackTraceFromIntent(intent)

        binding.copyButton.setOnClickListener {
            ClipBoardUtils.copyMessage(this, binding.logText.text.toString())
            Toast.makeText(this, "복사완료", Toast.LENGTH_SHORT).show()
        }

        val config = CustomActivityOnCrash.getConfigFromIntent(intent)

        binding.restartButton.setOnClickListener {
            CustomActivityOnCrash.restartApplication(this, config!!)
        }
    }
}