# Dev Mode

Ô Quick Settings cho Android để gửi lệnh bật/tắt **Developer options + USB debugging** ngay từ bảng kéo xuống.

## Tính năng

- Chạm ô **Dev Mode** để luân phiên bật/tắt cả hai cài đặt.
- Icon nút nguồn nhỏ gọn: vòng liền khi bật, vòng đứt khi tắt.
- Dòng phụ **Developer enabled / Developer disabled** và màu ô theo yêu cầu thành công gần nhất.
- Màn hình app có nút Bật/Tắt riêng và nút thêm ô nhanh.
- Yêu cầu mở khóa điện thoại trước khi thao tác từ màn hình khóa.
- Không dùng root, Shizuku hoặc dịch vụ chạy liên tục; cần cấp quyền ADB lần đầu.

## Trạng thái trên Android 17

Android có thể trả `0` khi app bên thứ ba đọc `ADB_ENABLED` và `DEVELOPMENT_SETTINGS_ENABLED`. Vì vậy, ô hiển thị **yêu cầu ghi thành công gần nhất**, không phải trạng thái hệ thống đã xác minh.

Nếu bạn đổi cài đặt trực tiếp trong Settings, hãy dùng nút Bật hoặc Tắt trong app để đồng bộ lại ô. Khi chưa gửi yêu cầu lần nào, ô hiện **Chưa xác định**. Lệnh ghi có thể bị chính sách thiết bị hoặc firmware từ chối; quyền ADB không bảo đảm tương thích với mọi máy.

Người dùng đã xác nhận bật/tắt và giao diện hoạt động trên **Samsung Galaxy S25 Ultra, One UI 9.0**. Chưa có kiểm thử tự động hoặc xác nhận trên các thiết bị khác.

## Mở và build

1. Clone repo rồi mở thư mục gốc bằng Android Studio.
2. Chọn Gradle distribution **Wrapper** và Gradle JDK **17**.
3. Cài Android SDK Platform **36** nếu chưa có, rồi Sync.
4. Chọn thiết bị và Run, hoặc chạy:

```sh
./gradlew :app:assembleDebug
```

Windows dùng `gradlew.bat :app:assembleDebug`.

APK debug: `app/build/outputs/apk/debug/app-debug.apk`.

Build dùng Gradle **8.13**, Android Gradle Plugin **8.13.2**, Kotlin **2.2.21**. Hỗ trợ Android **8.0 / API 26** trở lên; compile/target SDK **36**. Chạy trên Android 17 vẫn cần kiểm tra chính sách của thiết bị.

## Cấp quyền lần đầu

1. Cài app lên điện thoại.
2. Bật Developer options và USB debugging trong Settings.
3. Kết nối USB với máy tính, chấp nhận hộp thoại cho phép USB debugging.
4. Chạy với Android SDK Platform Tools:

```sh
adb shell pm grant vn.doanhtam.developertile android.permission.WRITE_SECURE_SETTINGS
```

Trên macOS, nếu `adb` chưa có trong PATH:

```sh
~/Library/Android/sdk/platform-tools/adb shell pm grant vn.doanhtam.developertile android.permission.WRITE_SECURE_SETTINGS
```

5. Mở app → **Thêm ô vào cài đặt nhanh**. Hoặc sửa bảng Quick Settings và thêm **Dev Mode** thủ công.
6. Dùng nút Bật/Tắt trong app một lần để đặt trạng thái yêu cầu ban đầu.

Không cần cấp lại quyền sau mỗi lần dùng. Gỡ app rồi cài lại sẽ cần cấp lại. Tắt USB debugging sẽ ngắt kết nối ADB; máy tính mới vẫn phải được người dùng cho phép kết nối.

## Kiểm tra thủ công

Sau khi bật, đọc giá trị từ ADB trên máy tính:

```sh
adb shell settings get global development_settings_enabled
adb shell settings get global adb_enabled
```

Kết quả mong đợi khi bật: `1` và `1`. Khi tắt, ADB có thể mất kết nối ngay; kiểm tra trực tiếp trong Settings.

Nếu ô không xuất hiện hoặc nhãn/icon chưa cập nhật sau khi nâng cấp, thử xóa ô rồi thêm lại.

## Tài liệu Android

- [Quick Settings tiles](https://developer.android.com/develop/ui/views/quicksettings-tiles)
- [Settings.Global và giới hạn đọc trạng thái](https://developer.android.com/reference/android/provider/Settings.Global#ADB_ENABLED)
- [WRITE_SECURE_SETTINGS](https://developer.android.com/reference/android/Manifest.permission#WRITE_SECURE_SETTINGS)

## Bảo mật

App không yêu cầu quyền Internet. Chỉ bật USB debugging khi cần và chỉ cho phép máy tính bạn tin cậy. Repo không chứa SDK path cục bộ, keystore hoặc APK build.
