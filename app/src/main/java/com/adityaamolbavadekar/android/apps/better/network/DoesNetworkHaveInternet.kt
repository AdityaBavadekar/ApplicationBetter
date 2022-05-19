/*******************************************************************************
 * Copyright (c) 2021. Aditya Bavadekar
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 ******************************************************************************/

package com.adityaamolbavadekar.android.apps.better.network

import com.hypertrack.hyperlog.HyperLog
import java.net.InetSocketAddress
import java.net.Socket

object DoesNetworkHaveInternet {

    fun execute(): Boolean {
        return try {
            HyperLog.i(TAG, "PINGING google.")
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            HyperLog.i(TAG, "PING success.")
            true
        } catch (e: Exception) {
            HyperLog.e(TAG, "No Internet connection. $e")
            false
        }
    }

    const val TAG = "DoesNetworkHaveInternet"
}
