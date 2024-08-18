package me.mm.sky.auto.music.tools

import android.util.Log
import android.widget.Toast
import me.mm.sky.auto.music.service.MyService

object AccessibilityUtils {
    fun isRooted(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = process.outputStream
            os.write("exit\n".toByteArray())
            os.flush()
            process.waitFor()
            return process.exitValue() == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    fun isAccessibilityServiceEnabled(serviceName: String): Boolean {
        val command = "settings get secure enabled_accessibility_services"
        val result = executeShellCommand(command)
        return result.contains(serviceName)
    }
    fun enableAccessibilityService() {
        if (isRooted()) {
            val packageName = MyService::class.java.`package`?.name

            Log.e("AccessibilityUtils", "enableAccessibilityService: $packageName")
            if (!isAccessibilityServiceEnabled(packageName!!)) {
                val command = "settings put secure enabled_accessibility_services $packageName"
                executeShellCommand(command)
                executeShellCommand("settings put secure accessibility_enabled 1")
            }
        }
    }

    private fun executeShellCommand(command: String): String {
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = process.outputStream
            os.write("$command\n".toByteArray())
            os.write("exit\n".toByteArray())
            os.flush()
            val reader = process.inputStream.bufferedReader()
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            process.waitFor()
            return output.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}