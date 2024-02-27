/*
 * Copyright (C) 2018 Lucio
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
 */

package bas.droid.core.content

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Message
import androidx.annotation.RequiresPermission
import bas.droid.core.util.isNetworkConnected

/**
 * 网络改变广播接收器
 */
open class NetworkChangedReceiver private constructor(val listener: OnNetworkChangedListener) : BroadcastReceiver() {

    /**
     * 网络改变回调
     */
    interface OnNetworkChangedListener {
        fun onNetworkLost()
        fun onNetworkConnected()
    }

    private val _handler = Handler { msg ->
        when (msg.what) {
            WHAT_NETWORK_CONNECTED -> listener.onNetworkConnected()
            WHAT_NETWORK_LOST -> listener.onNetworkLost()
        }
        true
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(ctx: Context, intent: Intent) {
        try {
            // 不是网络状态变化的不做处理
            if (intent.action != ConnectivityManager.CONNECTIVITY_ACTION)
                return
            val isConnected = ctx.isNetworkConnected()
            //移除原有的消息
            this._handler.removeMessages(WHAT_NETWORK_CONNECTED)
            this._handler.removeMessages(WHAT_NETWORK_LOST)
            this._handler.removeMessages(WHAT_NETWORK_ERROR)
            val msg: Message = this._handler.obtainMessage(if (isConnected) WHAT_NETWORK_CONNECTED else WHAT_NETWORK_LOST)

            //延迟发送消息
            this._handler.sendMessageDelayed(msg, MESSAGE_DELAY_MILLISECOND)
        } catch (e: Throwable) {
            e.printStackTrace()
            val msg = this._handler.obtainMessage(WHAT_NETWORK_ERROR, e)
            this._handler.sendMessageDelayed(msg, MESSAGE_DELAY_MILLISECOND)
        }

    }

    companion object {

        private const val WHAT_NETWORK_LOST = 404
        private const val WHAT_NETWORK_CONNECTED = 100
        private const val WHAT_NETWORK_ERROR = -1

        private const val MESSAGE_DELAY_MILLISECOND = 500L

        /**
         * 注册广播
         * @param context
         * @param listener
         * @return
         */
        @JvmStatic
        @RequiresPermission(value = Manifest.permission.ACCESS_NETWORK_STATE)
        fun registerReceiver(context: Context, listener: OnNetworkChangedListener): NetworkChangedReceiver {
            val netReceiver = NetworkChangedReceiver(listener)
            val mFilter = IntentFilter()
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(netReceiver, mFilter)
            return netReceiver
        }

        /**
         * 解除广播
         * @param context
         * @param receiver
         */
        @JvmStatic
        fun unregisterReceiver(context: Context, receiver: BroadcastReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}
