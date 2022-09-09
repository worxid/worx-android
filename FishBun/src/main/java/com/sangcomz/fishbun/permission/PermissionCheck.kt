package com.sangcomz.fishbun.permission

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sangcomz.fishbun.R

/**
 * Created by sangc on 2015-10-12.
 */
class PermissionCheck(private val context: Context) {
    private fun checkPermission(permissionList: List<String>, requestCode: Int): Boolean {
        if (context !is Activity) return false

        val needRequestPermissionList = permissionList
            .map { it to ContextCompat.checkSelfPermission(context, it) }
            .filter { it.second != PackageManager.PERMISSION_GRANTED }
            .map { it.first }
            .toTypedArray()

        return if (needRequestPermissionList.isEmpty()) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    needRequestPermissionList.first()
                )
            ) {
                ActivityCompat.requestPermissions(context, needRequestPermissionList, requestCode)
            } else {
                ActivityCompat.requestPermissions(context, needRequestPermissionList, requestCode)
            }
            false
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkStoragePermission(requestCode: Int): Boolean {
        return checkPermission(
            arrayListOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
            requestCode
        )
    }

    fun showPermissionDialog() {
        Toast.makeText(context, R.string.msg_permission, Toast.LENGTH_SHORT).show()
    }

}