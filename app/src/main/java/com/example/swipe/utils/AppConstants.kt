package com.example.swipe.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class AppConstants {
    companion object {
        const val BASE_URL = "https://app.getswipe.in/"
        const val SHARED_PREF_NAME = "SWIPE_PREFS"
        const val TOKEN = "token"

        fun snackBarTemplate(view: View, text: String): Snackbar {
            return Snackbar.make(view,text, Snackbar.LENGTH_INDEFINITE)
        }
    }
}