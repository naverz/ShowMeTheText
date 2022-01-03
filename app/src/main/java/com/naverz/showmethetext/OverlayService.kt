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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.naverz.showmethetext.view.WindowViewManager


class OverlayService : Service() {
    private var serviceId = 0
    private var windowViewManager: WindowViewManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        serviceId = startId
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        windowViewManager = WindowViewManager(this.applicationContext)

        windowViewManager?.setOnDestroyListener {
            this@OverlayService.stopSelf(serviceId)
            windowViewManager = null
        }
        windowViewManager?.start()
    }
}