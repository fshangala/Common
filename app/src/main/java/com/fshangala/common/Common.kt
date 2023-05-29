package com.fshangala.common

class Common {
    fun clickPositionListener():String{
        return "document.onmousemove = (event) =>{\n" +
                "  var elx = event.clientX;\n" +
                "  var ely = event.clientY;\n" +
                "\n" +
                "  window.lambo.getClickPosition(elx,ely);\n" +
                "}"
    }
    fun clickon(x:Int, y:Int):String{
        return "function clickon(x,y){\n" +
                "  var el = document.elementFromPoint(x,y);\n" +
                "  var event = new MouseEvent( \"click\", { clientX: x, clientY: y, bubbles: true } );\n" +
                "  el.dispatchEvent(event);\n" +
                "}\n" +
                "clickon($x,$y);"
    }
    fun scrollListener():String{
        return "document.onscroll = (event) => {\n" +
                "  var scrollx = document.scrollingElement.scrollLeft;\n" +
                "  var scrolly = document.scrollingElement.scrollTop;\n" +
                "  window.lambo.getScrollPosition(scrollx,scrolly);\n" +
                "}"
    }

    fun scroll(x: Int, y: Int):String {
        return "document.scrollingElement.scroll($x,$y);"
    }
}