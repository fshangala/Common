package com.fshangala.common

class Common {
    fun clickPositionListener():String{
        return "document.onmousemove = (event) =>{\n" +
                "  var elx = event.clientX;\n" +
                "  var ely = event.clientY;\n" +
                "  var el = document.elementFromPoint(elx,ely);\n" +
                "  if(el.tagName.toLowerCase() === \"input\") {\n" +
                "    el.addEventListener(\"change\",(e)=>{\n" +
                "      var fel;\n" +
                "      var feli;\n" +
                "      var el = e.target\n" +
                "      el.setAttribute(\"lamboclicked\",1)\n" +
                "      var stack = []\n" +
                "      while (el !== null) {\n" +
                "          stack.push(el)\n" +
                "          el = el.parentElement\n" +
                "      }\n" +
                "      stack.pop()\n" +
                "      var stack1 = stack.map((item)=>{\n" +
                "          var a = item\n" +
                "          var a_id = a.id\n" +
                "          var a_name = a.tagName.toLowerCase()\n" +
                "          var b = a_name\n" +
                "          if (a_id !== '') {\n" +
                "            b += \"#\"+a_id\n" +
                "          }\n" +
                "          return b\n" +
                "      })\n" +
                "      var elpath = stack1.reverse().join(\" \")\n" +
                "      \n" +
                "      var elems = document.querySelectorAll(elpath)\n" +
                "      elems.forEach((elem,index)=>{\n" +
                "          if(elem.hasAttribute(\"lamboclicked\")){\n" +
                "              fel = elem\n" +
                "              feli = index\n" +
                "          }\n" +
                "      })\n" +
                "      fel.removeAttribute(\"lamboclicked\")\n" +
                "      var elvalue = fel.value;\n" +
                "      \n" +
                "      window.lambo.inputChange(elpath,feli,elvalue);\n" +
                "      //console.log(elvalue)\n" +
                "    });\n" +
                "  }\n" +
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

    fun back():String {
        return "history.back();"
    }

    fun updateInput(path: String, index: Int, value: String):String{
        return "var input = document.querySelectorAll($path)[$index]\n" +
                "input.value = \"$value\"\n" +
                "input.dispatchEvent(new Event('input', { bubbles: true}));\n" +
                "input.dispatchEvent(new Event('change', { bubbles: true}));"
    }
}