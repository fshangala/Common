package com.fshangala.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject

class UpdateActivity : AppCompatActivity() {
    private var model: LamboViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        model = ViewModelProvider(this)[LamboViewModel::class.java]
        val downloadUpdateBtn = findViewById<Button>(R.id.updateBtn)
        val textView = findViewById<TextView>(R.id.textView)
        runOnUiThread {
            val textViewtext = "Installed Version: v${BuildConfig.VERSION_NAME}"
            textView.text = textViewtext
        }

        model!!.getLatestRelease()
        model!!.releaseVersionResponse.observe(this) {
            val currentVersion = "v"+BuildConfig.VERSION_NAME
            if(it!=""){
                val update = JSONObject(JSONArray(it).getString(0))
                if (update.getString("tag_name") != currentVersion) {
                    runOnUiThread {
                        val displayText = "Installed Version: v${BuildConfig.VERSION_NAME}\n" +
                                "Latest Version: ${update.getString("tag_name")}\n" +
                                "Please install the latest version!"
                        textView.text = displayText
                    }
                    downloadUpdateBtn.isEnabled = true
                    downloadUpdateBtn.setOnClickListener {
                        downloadUpdate(update.getString("html_url"))
                    }
                }
            }
        }
        model!!.releaseVersionResponseError.observe(this) {
            if(it != ""){
                Snackbar.make(findViewById(R.id.parentLayout),it, Snackbar.LENGTH_INDEFINITE).
                setAction("Retry",View.OnClickListener {
                    model!!.getLatestRelease()
                }).show()
            }
        }
    }

    fun downloadUpdate(url: String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    fun cancel(view:View){
        finish()
    }
}