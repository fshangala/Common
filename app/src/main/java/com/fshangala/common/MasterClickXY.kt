package com.fshangala.common

data class MasterClickXY(val x:Int, val y:Int){
    fun js():String{
        val common = Common()
        return common.clickon(x,y)
    }
    fun json():String{
        return "{\"event_type\":\"master\",\"event\":\"master_position\",\"args\":[\"$x\",\"$y\"],\"kwargs\":{}}"
    }
}
