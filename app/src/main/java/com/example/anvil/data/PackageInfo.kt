package com.example.anvil.data

import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import com.example.anvil.ui.AnvilViewModel


class AppInfo(viewModel: AnvilViewModel, context: Context) {

    var installedPackages: List<PackageInfo> = viewModel.installedPackages


    private val packageManager = context.packageManager!!

    fun getPackages(): List<PackageInfo> {
        installedPackages = packageManager.getInstalledPackages(0)
        return installedPackages
    }

    fun getIconId(packageInfo: PackageInfo): Int {
        return packageInfo.applicationInfo!!.icon
    }

    fun getAppName(packageInfo: PackageInfo): String {
        return packageInfo.applicationInfo!!.loadLabel(packageManager).toString()
    }

    fun getPackageName(packageInfo: PackageInfo): String {
        return packageInfo.applicationInfo!!.packageName
    }

    fun getPackageIcon(packageInfo: PackageInfo): Drawable {
        return packageInfo.applicationInfo!!.loadIcon(packageManager)
    }

    fun getPackageIconByName(packageName: String): Drawable {
        return packageManager.getApplicationIcon(packageName)
    }


}


