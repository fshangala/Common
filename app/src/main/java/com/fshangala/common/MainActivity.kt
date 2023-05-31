package com.fshangala.common

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var model: LamboViewModel? = null
    var sharedPref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this)[LamboViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)

        model!!.getLatestRelease()
        model!!.releaseVersionResponse.observe(this) {
            val currentVersion = "v"+BuildConfig.VERSION_NAME
            if(it!=""){
                val update = JSONObject(JSONArray(it).getString(0))
                if (update.getString("tag_name") != currentVersion) {
                    openUpdate()
                }
            }
        }
        model!!.releaseVersionResponseError.observe(this) {
            val currentVersion = "v"+BuildConfig.VERSION_NAME
            //Log.d("UPDATE",it)
        }
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
                model!!.getRequest(sharedPref!!,"/betsite/")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openConfig(){
        val intent = Intent(this,ConfigActivity::class.java)
        startActivity(intent)
    }
    private fun openUpdate(){
        val intent = Intent(this,UpdateActivity::class.java)
        startActivity(intent)
    }
}