package com.example.study_project.common

import android.Manifest

class Constants {
    companion object {
        const val API_BASE_URL = "https://api.waqi.info"

        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        const val REQUEST_PERMISSION_CODE = 9999
    }
}