package com.fshangala.common

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var model: LamboViewModel? = null
    private var sharedPref: SharedPreferences? = null
    private var openButton: Button? = null
    private var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this)[LamboViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", MODE_PRIVATE)
        openButton = findViewById(R.id.openButton)

        model!!.getLatestRelease()
        model!!.loading.observe(this) {
            openButton!!.isEnabled = !it
        }
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
        model!!.loginResponse.observe(this) {
            if (it != ""){
                openButton!!.isEnabled = true
                val response = JSONObject(it)
                val token = response.optString("token")
                if(token != "") {
                    val editSharedPref = sharedPref!!.edit()
                    editSharedPref.putString("token",token)
                    editSharedPref.apply()
                    model!!.loggedinUser(sharedPref!!,token)
                } else {
                    model!!.loading.postValue(false)
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }
            }
        }
        model!!.loginResponseError.observe(this) {
            if (it != "") {
                model!!.loading.postValue(false)
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
        }
        model!!.loggedinUserResponse.observe(this) {
            if (it != "") {
                model!!.loading.postValue(false)
                val response = JSONObject(it)
                val username = response.optString("username")
                if (username != "") {
                    val isStaff = response.optBoolean("is_staff")
                    if (isStaff) {
                        openMain()
                    } else {
                        openSlave()
                    }
                } else {
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }
            }
        }
        model!!.loggedinUserError.observe(this) {
            if (it != "") {
                model!!.loading.postValue(false)
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
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
        val betsiteUrl = findViewById<EditText>(R.id.betsite_url)
        val stakeInput = findViewById<EditText>(R.id.stakeInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)

        val editSharedPref = sharedPref!!.edit()
        editSharedPref.putString("betsite_url",betsiteUrl.text.toString())
        editSharedPref.putString("stake",stakeInput.text.toString())
        editSharedPref.putString("username",usernameInput.text.toString())
        editSharedPref.putString("password",passwordInput.text.toString())
        editSharedPref.apply()

        model!!.login(sharedPref!!,usernameInput.text.toString(),passwordInput.text.toString())
        model!!.loading.postValue(true)

        /*if(usernameInput.text.toString() == "admin" && passwordInput.text.toString() == "admin"){
            openMain()
        } else {
            openSlave()
        }*/
    }

    private fun loadPreferences(){
        val betsiteUrl = findViewById<EditText>(R.id.betsite_url)
        //val stakeInput = findViewById<EditText>(R.id.stakeInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)

        betsiteUrl.post {
            betsiteUrl.setText(sharedPref!!.getString("betsite_url",""))
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