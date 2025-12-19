# PERSONAL_MOBILE_APP_BAI09

Tài liệu hướng dẫn cho hai phần chính của dự án:
- BACKEND: Socket.IO server (Java / Spring Boot)
- SOCKET: Ứng dụng Android (Android Studio)

**Mục lục**
- **Tổng Quan**: Mô tả ngắn về dự án
- **BACKEND (Socket.IO)**: Yêu cầu, cấu hình, chạy server, API
- **SOCKET (Android)**: Yêu cầu, cấu hình, build & chạy
- **Khắc phục sự cố**
- **Đóng góp & Giấy phép**

**Tổng Quan**
- **Mô tả**: Ứng dụng hỗ trợ giao tiếp thời gian thực giữa khách hàng và quản lý bằng Socket.IO. Backend là server Java (Spring Boot) cung cấp REST API và Socket.IO event hub. Phần client là ứng dụng Android trong thư mục `SOCKET/app`.

**BACKEND (Socket.IO)**
- **Vị trí mã nguồn**: `BACKEND/`
- **Yêu cầu**:
	- **JDK**: Java 11+ (hoặc phiên bản tương thích với project)
	- **Maven**: dùng `mvn` để build
	- (Tùy chọn) Python cho một vài script thử nghiệm: `serve-test.py`
- **Biến môi trường & cấu hình**:
	- Kiểm tra `BACKEND/src/main/resources/application.properties` cho các cấu hình cổng, datasource và JWT keys.
	- Giá trị mặc định có thể nằm trong `target/classes/application.properties` sau khi build.
- **Build**:
	- Từ thư mục `BACKEND/` chạy:
		```bash
		mvn clean package
		```
- **Chạy server (nhanh)**:
	- Sử dụng các script có sẵn trong `BACKEND/`:
		- `run.bat` — chạy server chính
		- `run-server.bat` / `run-socketio-server.bat` — các biến thể chạy server hoặc Socket.IO server
	- Hoặc chạy jar đã build:
		```bash
		java -jar target/<artifact>.jar
		```
- **Endpoints & Events**:
	- REST: kiểm tra các controller trong `BACKEND/src/main/java/com/example/support/...` (ví dụ `AuthController.java`, `ManagerController.java`).
	- Socket.IO: server lắng nghe các event chat, message, join/leave. Xem `SocketIOServer.java`, `SimpleSocketIOServer.java`, hoặc `CompleteSocketIOServer.java` để biết chi tiết event và namespace.
- **Kiểm thử nhanh**:
	- Trang thử nghiệm: `BACKEND/test-login.html`, `BACKEND/test-manager-login.html` có thể dùng để thử REST và socket.

**SOCKET (Android)**
- **Vị trí mã nguồn**: `SOCKET/app/`
- **Yêu cầu**:
	- Android Studio (Arctic Fox hoặc mới hơn được khuyến nghị)
	- Android SDK phù hợp (API level theo `build.gradle.kts` trong `app`)
- **Cấu hình**:
	- Địa chỉ server (URL Socket.IO / REST) nên được cấu hình trong mã nguồn hoặc file `strings.xml`/resource tương tự. Tìm chuỗi server URL trong `SOCKET/app/src/main/res/values/strings.xml` hoặc trong mã Java/Kotlin kết nối Socket.
	- Quyền cần thiết: Internet permission trong `AndroidManifest.xml` đã có sẵn tại `SOCKET/app/src/main/AndroidManifest.xml`.
- **Mở project & build**:
	- Mở `SOCKET/` trong Android Studio (Open -> chọn folder `SOCKET`)
	- Đồng bộ Gradle, sau đó build/run trên emulator hoặc thiết bị thật.
- **Run trên thiết bị**:
	- Chạy app `app` module, chọn device, nhấn Run.
	- Kiểm tra Logcat để xác thực kết nối SocketIO và event messages.

**Khắc phục sự cố**
- **Không kết nối được Socket**:
	- Kiểm tra URL server và cổng (mặc định thường 8080 hoặc cấu hình trong `application.properties`).
	- Kiểm tra tường lửa / quyền mạng trên máy host.
	- Xem log server và logcat client để tìm lỗi handshake hoặc CORS.
- **Lỗi build Backend**:
	- Chạy `mvn -X package` để debug chi tiết.
- **Lỗi build Android**:
	- Đảm bảo SDK và phiên bản Gradle tương thích; chạy `./gradlew assembleDebug` để xem lỗi CLI.

**Đóng góp**
- Fork repository, tạo feature branch, mở Pull Request. Mô tả rõ thay đổi và cách kiểm thử.

**Giấy phép**
- Chưa chỉ định; thêm file `LICENSE` nếu cần.

---

Nếu bạn muốn, tôi có thể:
- Thêm ví dụ cấu hình môi trường (sample `.env` hoặc `application-dev.properties`).
- Thêm hướng dẫn chi tiết hơn cho từng script chạy sẵn.
