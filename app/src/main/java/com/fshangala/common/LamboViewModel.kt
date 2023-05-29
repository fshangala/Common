package com.fshangala.common

import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneOffset

class LamboViewModel : ViewModel() {
    private val appClient = OkHttpClient()
    private var appSocket: WebSocket? = null

    var connectionStatus = MutableLiveData<String>("")
    var automationEvents = MutableLiveData<AutomationEvents>() //deprecated
    var lamboEvent = MutableLiveData<JSONObject>()
    var connected = MutableLiveData<Boolean>(false)
    var browserLoading = MutableLiveData<Boolean>(false)
    var oddButtons = MutableLiveData<Int>(0) //deprecated
    var userrole = MutableLiveData<String>("")
    var currentBetIndex = MutableLiveData<String>("") //deprecated
    var element = MutableLiveData<String>("")
    var currentBetIndexOdds = MutableLiveData<String>("")
    var jslog = MutableLiveData<String>("")
    var apiResponse = MutableLiveData<String>("")
    var apiResponseError = MutableLiveData<String>("")
    var releaseVersionResponse = MutableLiveData<String>("")
    var releaseVersionResponseError = MutableLiveData<String>("")

    fun createConnection(sharedPref: SharedPreferences){
        connectionStatus.postValue("Connecting...")

        val defaultid:Long = 0
        var device_id = sharedPref.getLong("device_id",defaultid)
        if (device_id == defaultid){
            val editsharedPref = sharedPref.edit()
            editsharedPref.putLong("device_id",LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
            editsharedPref.apply()
            device_id = sharedPref.getLong("device_id",defaultid)
        }

        val hostIp = sharedPref.getString("hostIp","13.233.109.76")
        val hostPort = sharedPref.getInt("hostPort",80).toString()
        val hostCode = sharedPref.getString("hostCode","sample")

        val host = "ws://$hostIp:$hostPort/ws/pcautomation/$hostCode/"
        val appRequest = Request.Builder().url(host).build()

        try {
            appClient.newWebSocket(appRequest,object: WebSocketListener(){
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    appSocket = webSocket
                    val devicetype = "${device_id}->Android:${Build.VERSION.RELEASE};Common"
                    webSocket.send("{\"event_type\":\"connection\",\"event\":\"phone_connected\",\"args\":[\"${devicetype}\"],\"kwargs\":{}}")
                    connectionStatus.postValue("Connected!")
                    connected.postValue(true)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    //automationEvents.postValue(AutomationEvents(text))
                    lamboEvent.postValue(JSONObject(text))
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    connectionStatus.postValue("Disconnected!")
                    connected.postValue(false)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    connectionStatus.postValue("Error: ${t.toString()}")
                    connected.postValue(false)
                }
            })
            //appClient.dispatcher.executorService.shutdown()
        } catch (ex: Exception){
            connectionStatus.postValue(ex.toString())
            connected.postValue(false)
        }
    }

    fun disconnect() {
        appSocket!!.close(1000,"Disconnected.")
    }

    fun sendEvent(data:String){
        if (connected.value == true){
            try {
                this.appSocket?.send(data)
            } catch (ex: Exception) {
                connectionStatus.postValue(ex.toString())
            }
        } else {
            connectionStatus.postValue("Failed! You are disconnected!")
        }
    }

    //deprecated
    fun sendCommand(data:AutomationObject){
        if (connected.value == true){
            try {
                this.appSocket?.send(data.toString())
            } catch (ex: Exception) {
                connectionStatus.postValue(ex.toString())
            }
        } else {
            connectionStatus.postValue("Failed! You are disconnected!")
        }
    }

    fun getRequest(sharedPref: SharedPreferences, endpoint:String = "/"){
        val hostIp = sharedPref.getString("hostIp","13.233.109.76")
        val hostPort = sharedPref.getInt("hostPort",80).toString()

        val host = "http://$hostIp:$hostPort/api$endpoint"
        val appRequest = Request.Builder().url(host).get().build()
        val call = appClient.newCall(appRequest)

        val thread = Thread {
            try {
                val response = call.execute()
                if (response.code == 200) {
                    apiResponse.postValue(response.body!!.string())
                } else {
                    apiResponseError.postValue(response.body!!.string())
                }
            } catch(ex:Exception) {
                apiResponseError.postValue(ex.message)
            }
        }
        thread.start()

    }

    fun getLatestRelease(){
        val host = "https://api.github.com/repos/fshangala/Common/releases"
        val appRequest = Request.Builder().url(host).get().build()
        val call = appClient.newCall(appRequest)

        val thread = Thread {
            try {
                val response = call.execute()
                if (response.code == 200) {
                    releaseVersionResponse.postValue(response.body!!.string())
                } else {
                    releaseVersionResponseError.postValue(response.body!!.string())
                }
            } catch(ex:Exception) {
                releaseVersionResponseError.postValue(ex.message)
            }
        }
        thread.start()
    }
}