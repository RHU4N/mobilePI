package com.example.ceos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.PopupMenu
import android.widget.Toast

object HeaderMenuUtil {
    fun showHeaderMenu(activity: Activity, anchor: android.view.View) {
        val popup = PopupMenu(activity, anchor)
        val prefs = activity.getSharedPreferences("ceos_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)

        if (token.isNullOrBlank()) {
            popup.menuInflater.inflate(R.menu.header_menu, popup.menu)
        } else {
            popup.menuInflater.inflate(R.menu.header_menu_logged, popup.menu)
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_login -> {
                    if (activity is HeaderMenuListener) {
                        (activity as HeaderMenuListener).showLoginDialog()
                    } else {
                        val intent = Intent(activity, login::class.java)
                        activity.startActivity(intent)
                    }
                    true
                }
                R.id.action_register -> {
                    if (activity is HeaderMenuListener) {
                        (activity as HeaderMenuListener).showRegisterDialog()
                    } else {
                        val intent = Intent(activity, cadastro::class.java)
                        activity.startActivity(intent)
                    }
                    true
                }
                R.id.action_logout -> {
                    prefs.edit().remove("auth_token").remove("user_email").remove("user_name").apply()
                    Toast.makeText(activity, "Deslogado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, home::class.java)
                    activity.startActivity(intent)
                    // finish current activity if it's not home so UI updates
                    if (activity !is home) activity.finish()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}
