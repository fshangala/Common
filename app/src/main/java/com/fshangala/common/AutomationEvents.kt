package com.fshangala.common

import org.json.JSONArray
import org.json.JSONObject

class AutomationEvents(json:String): JSONObject(json) {
    val eventType:String = this.optString("event_type")
    val eventName:String = this.optString("event")
    val eventArgs: JSONArray = this.optJSONArray("args")
}
