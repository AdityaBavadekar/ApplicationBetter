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

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.hypertrack.hyperlog.HyperLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectionLiveData(context: Context) : LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    override fun onActive() {
        //        super.onActive()
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(networkRequest, networkCallback)

    }

    override fun onInactive() {
        //        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            HyperLog.i(TAG, "onAvailable: $network")
            val networkCapabilities = cm.getNetworkCapabilities(network)
            val isInternet =
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (isInternet == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute()
                    if (hasInternet) {
                        HyperLog.i(TAG, "onAvailable: adding network. $network")
                        validNetworks.add(network)
                        checkValidNetworks()
                    }
                }
                validNetworks.add(network)
            } else {
                checkValidNetworks()
            }
        }

        override fun onLost(network: Network) {
            HyperLog.w(TAG, "onLost: $network")
            validNetworks.remove(network)
            checkValidNetworks()
        }

    }

    companion object {
        const val TAG = "ConnectionLiveData"
    }

}