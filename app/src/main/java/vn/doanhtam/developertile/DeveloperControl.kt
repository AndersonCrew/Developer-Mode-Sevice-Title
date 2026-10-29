package vn.doanhtam.developertile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

internal class DeveloperControl(private val context: Context) {
    private val preferences = context.getSharedPreferences("control", Context.MODE_PRIVATE)

    fun hasPermission() = context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
        PackageManager.PERMISSION_GRANTED

    // Android can mask both Global values as 0 for third-party callers.
    // This is only the last accepted write request, never a measured device state.
    fun lastRequest(): Boolean? = if (preferences.contains("requested")) {
        preferences.getBoolean("requested", false)
    } else null

    fun apply(enabled: Boolean): String {
        if (!hasPermission()) return "Chưa có quyền. Mở Developer Tile để xem lệnh cấp quyền ADB."
        preferences.edit().remove("requested").commit()
        return try {
            val value = if (enabled) 1 else 0
            val keys = if (enabled) {
                listOf(Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, Settings.Global.ADB_ENABLED)
            } else {
                listOf(Settings.Global.ADB_ENABLED, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED)
            }
            for (key in keys) {
                if (!Settings.Global.putInt(context.contentResolver, key, value)) {
                    return "Hệ thống từ chối ghi $key; có thể chỉ một phần đã thay đổi."
                }
            }
            preferences.edit().putBoolean("requested", enabled).commit()
            "Đã gửi yêu cầu ${if (enabled) "BẬT" else "TẮT"}. Chưa xác minh trạng thái hệ thống."
        } catch (_: SecurityException) {
            "Hệ thống chặn thao tác; có thể chỉ một phần đã thay đổi. Kiểm tra quyền hoặc chính sách Samsung."
        } catch (_: RuntimeException) {
            "Không ghi được cài đặt; có thể chỉ một phần đã thay đổi. Hãy kiểm tra trong Settings."
        }
    }
}
