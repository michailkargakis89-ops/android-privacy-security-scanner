package com.example.privacyscanner

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnScan = findViewById<Button>(R.id.btnScan)
        val tvResults = findViewById<TextView>(R.id.tvResults)

        btnScan.setOnClickListener {
            tvResults.text = "Scanning...\n\n"
            scanApps(tvResults)
        }
    }

    private fun scanApps(tvResults: TextView) {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val stringBuilder = StringBuilder()

        var riskyAppsCount = 0

        for (appInfo in packages) {
            // Skip system apps to focus on user-installed apps
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                val appName = pm.getApplicationLabel(appInfo).toString()

                try {
                    val packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS)
                    val permissions = packageInfo.requestedPermissions

                    var isRisky = false
                    var riskReasons = ""

                    if (permissions != null) {
                        for (permission in permissions) {
                            if (permission.contains("CAMERA")) {
                                isRisky = true
                                riskReasons += "- Has Camera Access\n"
                            }
                            if (permission.contains("RECORD_AUDIO")) {
                                isRisky = true
                                riskReasons += "- Can Record Audio\n"
                            }
                            if (permission.contains("ACCESS_FINE_LOCATION")) {
                                isRisky = true
                                riskReasons += "- Tracks Exact Location\n"
                            }
                            if (permission.contains("READ_CONTACTS")) {
                                isRisky = true
                                riskReasons += "- Reads Contact List\n"
                            }
                        }
                    }

                    if (isRisky) {
                        riskyAppsCount++
                        stringBuilder.append("⚠️ App: $appName\n$riskReasons\n")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        if (riskyAppsCount == 0) {
            stringBuilder.append("✅ Great! No highly risky apps found.")
        } else {
            stringBuilder.insert(0, "Found $riskyAppsCount apps with sensitive permissions:\n\n")
        }

        tvResults.text = stringBuilder.toString()
    }
}