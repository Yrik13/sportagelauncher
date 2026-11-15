package com.mylauncher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid = findViewById<GridLayout>(R.id.appGrid)
        val clock = findViewById<TextView>(R.id.clock)

        // Часы
        val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        clock.text = fmt.format(Date())

        // Список всех приложений с иконками
        val pm = packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(launcherIntent, 0)

        apps.filter { it.activityInfo.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }
            .forEach { app ->
                val cell = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(20, 30, 20, 30)
                    }
                }

                val icon = ImageView(this@MainActivity).apply {
                    setImageDrawable(app.loadIcon(pm))
                    layoutParams = LinearLayout.LayoutParams(170, 170)
                }

                val label = TextView(this@MainActivity).apply {
                    text = app.loadLabel(pm)
                    textSize = 15f
                    gravity = Gravity.CENTER
                    setTextColor(0xFFFFFFFF.toInt())
                    setPadding(4, 10, 4, 0)
                }

                cell.addView(icon)
                cell.addView(label)

                cell.setOnClickListener {
                    pm.getLaunchIntentForPackage(app.activityInfo.packageName)?.let { i ->
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                    }
                }

                grid.addView(cell)
            }
    }
}
