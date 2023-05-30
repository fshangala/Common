package com.fshangala.common

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var model: LamboViewModel? = null
    private var sharedPref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this)[LamboViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)

        model!!.getLatestRelease()
        model!!.releaseVersionResponse.observe(this) {
            val currentVersion = "v"+BuildConfig.VERSION_NAME
            if(it!=""){
                val versions = JSONArray(it)
                if (versions.length() > 0){
                    val update = JSONObject(versions.getString(0))
                    if (update.getString("tag_name") != currentVersion) {
                        openUpdate()
                    }
                }
            }
        }
        model!!.releaseVersionResponseError.observe(this) {
            //val currentVersion = "v"+BuildConfig.VERSION_NAME
            //Log.d("UPDATE",it)
        }

        loadPreferences()
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
    fun savePreferences(view: View){
        val betsite_url = findViewById<EditText>(R.id.betsite_url)
        val stakeInput = findViewById<EditText>(R.id.stakeInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)

        val editSharedPref = sharedPref!!.edit()
        editSharedPref.putString("betsite_url",betsite_url.text.toString())
        editSharedPref.putString("stake",stakeInput.text.toString())
        editSharedPref.putString("username",usernameInput.text.toString())
        editSharedPref.putString("password",passwordInput.text.toString())
        editSharedPref.apply()

        if(usernameInput.text.toString() == "admin" && passwordInput.text.toString() == "admin"){
            openMain()
        } else {
            openSlave()
        }
    }

    private fun loadPreferences(){
        val betsite_url = findViewById<EditText>(R.id.betsite_url)
        //val stakeInput = findViewById<EditText>(R.id.stakeInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)

        betsite_url.post {
            betsite_url.setText(sharedPref!!.getString("betsite_url",""))
        }
        usernameInput.post {
            usernameInput.setText(sharedPref!!.getString("username",""))
        }
    }

    private fun openMain(){
        val intent = Intent(this,SiteActivity::class.java)
        startActivity(intent)
    }
    private fun openSlave(){
        val intent = Intent(this,SlaveActivity::class.java)
        startActivity(intent)
    }

    private fun openConfig(){
        val intent = Intent(this,ConfigActivity::class.java)
        startActivity(intent)
    }
    private fun openUpdate(){
        val intent = Intent(this,UpdateActivity::class.java)
        startActivity(intent)
    }
    fun checkForUpdates(view: View){
        openUpdate()
    }
}