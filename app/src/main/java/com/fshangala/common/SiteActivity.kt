package com.fshangala.common

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDateTime
import java.time.ZoneOffset

class SiteActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var model: LamboViewModel? = null
    private var status: TextView? = null
    private var oddStatus: TextView? = null
    private var sharedPref: SharedPreferences? = null
    var toast: Toast? = null
    private var betsite_url: String? = null
    private var pelpath:String? = null
    private var pelindex:Int? = null
    private var pelement:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        model = ViewModelProvider(this)[LamboViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)

        webView = findViewById(R.id.webView)


        true.also {
            webView!!.settings.javaScriptEnabled = it
            webView!!.settings.domStorageEnabled = it
            webView!!.settings.allowContentAccess = it
            webView!!.settings.allowFileAccess = it
        }
        webView!!.addJavascriptInterface(LamboJsInterface(),"lambo")
        model = ViewModelProvider(this)[LamboViewModel::class.java]
        status = findViewById(R.id.status)
        oddStatus = findViewById(R.id.odd)

        betsite_url = sharedPref!!.getString("betsite_url","google.com")
        startBrowser()

        model!!.connectionStatus.observe(this) {
            toast = Toast.makeText(this,it,Toast.LENGTH_SHORT)
            toast!!.show()
        }
        model!!.browserLoading.observe(this){ isLoading ->
            if (isLoading == true) {
                runOnUiThread {
                    status!!.text = "Loading..."
                }
                model!!.jslog.postValue("Page loading...")
            } else {
                runOnUiThread {
                    status!!.text = "Loaded!"
                }
                webView!!.post {
                    webView!!.evaluateJavascript(Common().clickPositionListener()){}
                }
            }
        }

        model!!.lamboEvent.observe(this) {
            when (it.optString("event")) {
                "master_click" -> {
                    toast = Toast.makeText(this, it.optJSONArray("args")?.getString(2),Toast.LENGTH_LONG)
                    toast!!.show()
                }
                "master_position" -> {
                    toast = Toast.makeText(this, it.optJSONArray("args")?.toString(),Toast.LENGTH_LONG)
                    toast!!.show()
                }
                else -> {
                    toast = Toast.makeText(this, it.optString("event"),Toast.LENGTH_LONG)
                    toast!!.show()
                }
            }
        }
        model!!.userrole.observe(this) {
            val element = model!!.element.value
            val jslog = model!!.jslog.value
            val stake = sharedPref!!.getString("stake","200")
            runOnUiThread {
                val displayText = "Role:$it; Element:$element; Stake:$stake; $jslog"
                oddStatus!!.text = displayText
            }
        }
        model!!.element.observe(this) {
            val userrole = model!!.userrole.value
            val jslog = model!!.jslog.value
            val stake = sharedPref!!.getString("stake","200")
            runOnUiThread {
                val displayText = "Role:$userrole; Element:$it; Stake:$stake; $jslog"
                oddStatus!!.text = displayText
            }
        }
        model!!.jslog.observe(this) {
            val userrole = model!!.userrole.value
            val element = model!!.element.value
            val stake = sharedPref!!.getString("stake","200")
            runOnUiThread {
                val displayText = "Role:$userrole; Element:$element; Stake:$stake; $it"
                oddStatus!!.text = displayText
            }
        }
        model!!.createConnection(sharedPref!!)
        model!!.userrole.postValue("Master")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lambomenu,menu)

        model!!.connected.observe(this){
            if (it){
                menu.getItem(1).setIcon(R.mipmap.reset_green_round)
            } else {
                menu.getItem(1).setIcon(R.mipmap.reset_red_round)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.preferencesBtn -> {
                openConfig()
            }

            R.id.reconnectBtn -> {
                model!!.createConnection(sharedPref!!)
            }

            R.id.reloadBrowserBtn -> {
                webView!!.reload()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val masterClick = MasterClick(pelpath!!,pelindex!!,pelement!!)
                model!!.sendEvent(masterClick.json())
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                model!!.sendCommand(AutomationObject("bet","place_bet", arrayOf()))
            }
        }
        return true
    }

    private inner class LamboJsInterface {
        @JavascriptInterface
        fun performClick(elpath: String, elindex: Int, element: String){
            pelpath = elpath
            pelindex = elindex
            pelement = element
            if (pelement!! == "input"){
                inputStake(elpath,elindex)
            }
            val masterClick = MasterClick(pelpath!!,pelindex!!,pelement!!)
            model!!.sendEvent(masterClick.json())
            model!!.element.postValue(pelement!!)
        }
        @JavascriptInterface
        fun getClickPosition(x: Int, y: Int){
            val masterClickXY = MasterClickXY(x,y)
            model!!.sendEvent(masterClickXY.json())
            model!!.jslog.postValue("$x,$y")
        }
        @JavascriptInterface
        fun getOdds(odds: String){
            model!!.currentBetIndexOdds.postValue(odds)
        }
    }

    private fun inputStake(elpath: String, elindex: Int){
        val common = Common()
        webView!!.post {
            webView!!.evaluateJavascript(common.inputJs(sharedPref!!.getString("stake","200")!!,elpath,elindex)){}
        }
    }

    private fun startBrowser(){
        webView!!.loadUrl(betsite_url!!)
        webView!!.webViewClient = object : WebViewClient(){

            override fun onPageFinished(view: WebView?, url: String?) {
                model!!.browserLoading.value = false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                model!!.browserLoading.value = true
            }
        }
    }

    private fun openConfig(){
        val intent = Intent(this,ConfigActivity::class.java)
        startActivity(intent)
    }
}