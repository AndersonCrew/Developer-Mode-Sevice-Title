package vn.doanhtam.developertile

import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.TileService
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val component get() = ComponentName(this, DeveloperTileService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val padding = (24 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding * 2, padding, padding)
        }
        fun text(value: String) = TextView(this).apply {
            text = value
            textSize = 17f
            setPadding(0, 12, 0, 12)
            layout.addView(this)
        }
        text("Developer Tile").textSize = 26f
        status = text("")
        text("Thiết lập lần đầu: bật Developer options và USB debugging, kết nối máy tính và chấp nhận khóa RSA. Chạy lệnh sau trên máy tính:")
        text("adb shell pm grant vn.doanhtam.developertile android.permission.WRITE_SECURE_SETTINGS").setTextIsSelectable(true)
        text("Sau đó thêm ô Dev Mode vào bảng cài đặt nhanh. Mỗi lần chạm sẽ luân phiên gửi yêu cầu bật/tắt cả hai. Khi tắt USB debugging, kết nối ADB sẽ ngắt.\n\nAndroid có thể che trạng thái thật bằng giá trị 0; app chỉ nhớ yêu cầu gần nhất. Nếu đổi trong Settings, dùng nút Bật hoặc Tắt dưới đây để đồng bộ lại. Quyền ADB không bảo đảm Samsung cho phép ghi; nếu bị chặn, app sẽ báo lỗi. Không dùng Shizuku vì dịch vụ phụ thuộc ADB có thể ngắt khi tắt debugging.")
        fun button(title: String, action: () -> Unit) {
            layout.addView(Button(this).apply { text = title; setOnClickListener { action() } })
        }
        button("Thêm ô vào cài đặt nhanh") {
            if (Build.VERSION.SDK_INT >= 33) {
                getSystemService(StatusBarManager::class.java).requestAddTileService(
                    component, "Dev Mode", Icon.createWithResource(this, R.drawable.ic_developer_off),
                    mainExecutor
                ) { result -> status.text = "Kết quả thêm ô: $result. Có thể thêm thủ công bằng nút sửa trên bảng kéo xuống." }
            } else status.text = "Kéo bảng cài đặt nhanh xuống → Sửa → thêm Dev Mode."
        }
        button("Bật Developer options + USB debugging") { apply(true) }
        button("Tắt Developer options + USB debugging") { apply(false) }
        setContentView(ScrollView(this).apply { addView(layout) })
    }

    override fun onResume() {
        super.onResume()
        status.text = if (DeveloperControl(this).hasPermission()) {
            "Đã có WRITE_SECURE_SETTINGS. Trạng thái thực tế chưa xác minh."
        } else "Chưa cấp WRITE_SECURE_SETTINGS."
    }

    private fun apply(enabled: Boolean) {
        status.text = DeveloperControl(this).apply(enabled)
        TileService.requestListeningState(this, component)
    }
}
