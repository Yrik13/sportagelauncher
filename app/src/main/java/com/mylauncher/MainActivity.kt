package com.mylauncher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clock = findViewById<TextView>(R.id.clock)
        val dateText = findViewById<TextView>(R.id.dateText)
        val speedValue = findViewById<TextView>(R.id.speedValue)
        val appsScroll = findViewById<ScrollView>(R.id.appsScroll)

        // Часы и дата
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFmt = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
        val tick = object : Runnable {
            override fun run() {
                clock.text = timeFmt.format(Date())
                dateText.text = dateFmt.format(Date())
                clock.postDelayed(this, 5000)
            }
        }
        tick.run()

        // GPS Скорость
        val gps = GpsSpeed(this) { speed ->
            runOnUiThread { speedValue.text = speed.toString() }
        }
        gps.start()

        // Кнопка переключения
        findViewById<Button>(R.id.toggleBtn).setOnClickListener {
            if (appsScroll.visibility == View.VISIBLE) {
                appsScroll.visibility = View.GONE
            } else {
                loadApps()
                appsScroll.visibility = View.VISIBLE
            }
        }
    }

    private fun loadApps() {
        val grid = findViewById<GridLayout>(R.id.appGrid)
        if (grid.childCount > 0) return

        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }
            .forEach { app ->
                val cell = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(16, 16, 16, 16)
                    }
                }
                val icon = ImageView(this).apply {
                    setImageDrawable(app.loadIcon(pm))
                    layoutParams = LinearLayout.LayoutParams(140, 140)
                }
                val label = TextView(this).apply {
                    text = app.loadLabel(pm)
                    textSize = 14f
                    gravity = Gravity.CENTER
                    setTextColor(0xFFFFFFFF.toInt())
                }
                cell.addView(icon)
                cell.addView(label)
                cell.setOnClickListener {
                    pm.getLaunchIntentForPackage(app.activityInfo.packageName)?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                }
                grid.addView(cell)
            }
    }
}


