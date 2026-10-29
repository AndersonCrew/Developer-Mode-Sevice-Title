package vn.doanhtam.developertile

import android.os.Build
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

class DeveloperTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        refresh()
    }

    override fun onClick() {
        super.onClick()
        if (isLocked) unlockAndRun { toggle() } else toggle()
    }

    private fun toggle() {
        val control = DeveloperControl(this)
        Toast.makeText(this, control.apply(control.lastRequest() != true), Toast.LENGTH_LONG).show()
        refresh()
    }

    private fun refresh() {
        val control = DeveloperControl(this)
        qsTile?.apply {
            // Show the last accepted request; Android may conceal the actual state.
            val permitted = control.hasPermission()
            val requested = control.lastRequest()
            val enabled = permitted && requested == true
            state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            icon = Icon.createWithResource(this@DeveloperTileService,
                if (enabled) R.drawable.ic_developer else R.drawable.ic_developer_off)
            label = "Dev Mode"
            val status = if (!permitted) "Cần cấp quyền ADB" else when (requested) {
                true -> "Developer enabled"
                false -> "Developer disabled"
                null -> "Chưa xác định"
            }
            if (Build.VERSION.SDK_INT >= 29) subtitle = status
            contentDescription = "$label. $status. Chưa xác minh trạng thái thực tế."
            updateTile()
        }
    }
}
