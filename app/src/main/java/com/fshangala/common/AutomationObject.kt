package com.fshangala.common

data class AutomationObject(val event_type:String, val event:String, val args: Array<String>) {
    override fun toString(): String {
        var stringArgs = ""
        args!!.forEachIndexed(){ i: Int, any: Any ->
            if (i==0){
                stringArgs += "\"${any.toString()}\""
            } else {
                stringArgs += ",\"${any.toString()}\""
            }
        }
        return "{\"event_type\":\"$event_type\",\"event\":\"$event\",\"args\":[$stringArgs],\"kwargs\":{}}"
    }
}