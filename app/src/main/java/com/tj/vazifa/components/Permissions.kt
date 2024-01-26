package com.tj.vazifa.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@RequiresApi(Build.VERSION_CODES.R)
fun isGrantedPermissionWRITE_EXTERNAL_STORAGE(activity: Activity?): Boolean {
    val version = Build.VERSION.SDK_INT
    return if (version <= 32) {
        val isAllowPermissionApi28 = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        Log.e(
            "general_em",
            "isGrantedPermissionWRITE_EXTERNAL_STORAGE() - isAllowPermissionApi28: $isAllowPermissionApi28"
        )
        isAllowPermissionApi28
    } else {
        val isAllowPermissionApi33 = Environment.isExternalStorageManager()
        Log.e(
            "general_em",
            "isGrantedPermissionWRITE_EXTERNAL_STORAGE() - isAllowPermissionApi33: $isAllowPermissionApi33"
        )
        isAllowPermissionApi33
    }
}

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permissions() {
    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MEDIA_CONTENT_CONTROL
        )
    ){

    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissions.launchMultiplePermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    })
}