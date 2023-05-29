package com.fshangala.common

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class SlaveActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var model: LamboViewModel? = null
    private var status: TextView? = null
    private var oddStatus: TextView? = null
    private var sharedPref: SharedPreferences? = null
    private var toast: Toast? = null
    private var betsiteUrl: String? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slave)

        model = ViewModelProvider(this)[LamboViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)

        webView = findViewById(R.id.webView)

        webView!!.settings.javaScriptEnabled  = true
        webView!!.settings.domStorageEnabled = true
        webView!!.settings.allowContentAccess = true
        webView!!.settings.allowFileAccess = true
        //webView!!.addJavascriptInterface(LamboJsInterface(),"lambo")
        model = ViewModelProvider(this)[LamboViewModel::class.java]
        status = findViewById(R.id.status)
        oddStatus = findViewById(R.id.odd)

        betsiteUrl = sharedPref!!.getString("betsite_url","google.com")
        startBrowser()

        model!!.connectionStatus.observe(this) {
            toast = Toast.makeText(this,it,Toast.LENGTH_SHORT)
            toast!!.show()
        }
        model!!.browserLoading.observe(this){ isLoading ->
            if (isLoading == true) {
                runOnUiThread {
                    val statusText = "Loading..."
                    status!!.text = statusText
                }
            } else {
                runOnUiThread {
                    val statusText = "Loaded!"
                    status!!.text = statusText
                }
            }
        }
        model!!.lamboEvent.observe(this) {
            when (it.optString("event")) {
                "master_position" -> {
                    val masterClickXY = MasterClickXY(it.optJSONArray("args")!!.getInt(0),it.optJSONArray("args")!!.getInt(1))
                    webView!!.post {
                        webView!!.evaluateJavascript(masterClickXY.js()){}
                    }
                    model!!.jslog.postValue("pos"+it.optJSONArray("args")?.toString())
                }
                "master_scroll" -> {
                    val masterScroll = MasterScroll(it.optJSONArray("args")!!.getInt(0),it.optJSONArray("args")!!.getInt(1))
                    webView!!.post {
                        webView!!.evaluateJavascript(masterScroll.js()){}
                    }
                    model!!.jslog.postValue("scr"+it.optJSONArray("args")?.toString())
                }
                "master_back" -> {
                    webView!!.post {
                        webView!!.evaluateJavascript(Common().back()){}
                    }
                    toast = Toast.makeText(this, it.optString("event"),Toast.LENGTH_LONG)
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
        model!!.userrole.postValue("Slave")
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

    private fun startBrowser(){
        webView!!.loadUrl(betsiteUrl!!)
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