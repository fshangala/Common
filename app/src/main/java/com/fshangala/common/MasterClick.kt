package com.fshangala.common

data class MasterClick(val elpath:String, val elindex:Int){
    fun js():String{
        val common = Common()
        return common.clickJs(elpath,elindex)
    }
    fun json():String{
        return "{\"event_type\":\"master\",\"event\":\"master_click\",\"args\":[\"${js()}\"],\"kwargs\":{}}"
    }
}
