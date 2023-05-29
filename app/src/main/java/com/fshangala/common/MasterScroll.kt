package com.fshangala.common

data class MasterScroll(val x: Int, val y: Int){
    fun js():String{
        val common = Common()
        return common.scroll(x,y)
    }
    fun json():String{
        return "{\"event_type\":\"master\",\"event\":\"master_scroll\",\"args\":[\"$x\",\"$y\"],\"kwargs\":{}}"
    }
}
