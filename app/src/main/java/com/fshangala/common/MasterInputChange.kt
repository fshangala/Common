package com.fshangala.common

data class MasterInputChange(val path:String, val index:Int, val value:String){
    fun js():String{
        val common = Common()
        return common.updateInput(path,index,value)
    }
    fun json():String{
        return "{\"event_type\":\"master\",\"event\":\"master_input_change\",\"args\":[\"$path\",\"$index\",\"$value\"],\"kwargs\":{}}"
    }
}
