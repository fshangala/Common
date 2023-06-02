package com.fshangala.common

class Common {
    fun clickJs(elpath:String,elindex:Int):String{
        return "document.querySelectorAll(\'$elpath\')[$elindex].click();"
    }
    fun inputJs(data:Any,elpath:String,elindex:Int):String{
        return "var input = document.querySelectorAll(\'$elpath\')[$elindex];\n" +
                "input.value = \'$data\';\n" +
                "input.dispatchEvent(new Event('input', { bubbles: true}));\n" +
                "input.dispatchEvent(new Event('change', { bubbles: true}));"
    }
    fun eventListenerJs():String{
        return "document.addEventListener(\"click\",(event)=>{\n" +
                "    var fel;\n" +
                "    var feli;\n" +
                "    var el = event.target;\n" +
                "    el.setAttribute(\"lamboclicked\",1);\n" +
                "    var stack = [];\n" +
                "    while (el !== null) {\n" +
                "        stack.push(el);\n" +
                "        el = el.parentElement;\n" +
                "    }\n" +
                "    stack.pop();\n" +
                "    var stack1 = stack.map((item)=>{\n" +
                "        var a = item;\n" +
                "        var a_cc = [];\n" +
                "        a.classList.forEach((item)=>{\n" +
                "            a_cc.push(item);\n" +
                "        });\n" +
                "        var a_id = a.id;\n" +
                "        var a_name = a.tagName.toLowerCase();\n" +
                "        b = a_name;\n" +
                "        if (a_id === '') {\n" +
                "            if (a_cc.length > 0) {\n" +
                "                b += \".\"+a_cc.join(\".\");\n" +
                "            }\n" +
                "        } else {\n" +
                "            b += \"#\"+a_id;\n" +
                "        };\n" +
                "        return b;\n" +
                "    });\n" +
                "    var elpath = stack1.reverse().join(\" \");\n" +
                "    \n" +
                "    var elems = document.querySelectorAll(elpath);\n" +
                "    elems.forEach((elem,index)=>{\n" +
                "        if(elem.hasAttribute(\"lamboclicked\")){\n" +
                "            fel = elem;\n" +
                "            feli = index;\n" +
                "        }\n" +
                "    });\n" +
                "    fel.removeAttribute(\"lamboclicked\");\n" +
                "    var isinput = fel.tagName.toLowerCase() === \"input\";\n" +
                "    //console.log(fel);\n" +
                "    //console.log(feli);\n" +
                "    //console.log(isinput);\n" +
                "    \n" +
                "    window.lambo.performClick(elpath,feli,fel.tagName.toLowerCase());\n" +
                "});"
    }
}