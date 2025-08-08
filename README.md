# 💊 Pharmacy Project - Hệ Thống Quản Lý Nhà Thuốc

Pharmacy là một hệ thống quản lý nhà thuốc được xây dựng bằng Spring Boot. Dự án này hỗ trợ quản lý sản phẩm, đơn thuốc, người dùng, báo cáo doanh thu, kết nối với dịch vụ bên ngoài (qua Feign Clients), xác thực OAuth2 và JWT, cũng như tích hợp Cloudinary để lưu trữ hình ảnh.

---

## 🚀 Tính năng chính

- ✅ Quản trị viên có thể thực hiện đầy đủ các chức năng CRUD và thống kê doanh thu theo thời gian
- ✅ Người dùng không cần đăng ký tài khoản vẫn có thể quản lý giỏ hàng và mua hàng thông qua HttpSession
- ✅ Tích hợp đăng nhập bằng Google thông qua OAuth2
- ✅ Tích hợp nhiều phương thức thanh toán: MOMO, ZALOPAY, VNPAY
- ✅ Tính toán phí vận chuyển và ước lượng thời gian giao hàng thông qua API Giao Hàng Nhanh
- ✅ Hỗ trợ tính năng trò chuyện và nhận thông báo real-time qua WebSocket
- ✅ Hỗ trợ tạo đơn hàng và sinh link thanh toán khi người dùng tiến hành mua hàng

---

## 🛠️ Công nghệ sử dụng

![Cấu trúc dự án](https://github.com/user-attachments/assets/e1732bfb-be10-45ea-8ef2-47ef191dc3d7)

---

- **Ngôn ngữ:** Java 17
- **Framework:** Spring Boot 3.3.2
- **Cơ sở dữ liệu:** MySQL
- **Thư viện chính:**
  - Spring Data JPA
  - Spring Security + OAuth2
  - Feign Client
  - MapStruct
  - Lombok
  - Cloudinary
  - Spring WebSocket
  - Jakarta XML Binding
  - JSON (org.json)

---

## 📄 API Documentation

👉 Link tài liệu API với Postman: https://documenter.getpostman.com/view/35578029/2sB3BDKWu7

---

## ⚙️ Cài đặt và chạy dự án

### Yêu cầu:

- Java 17 trở lên
- Maven 3.8+
- MySQL đang chạy
- Tài khoản Cloudinary (nếu dùng ảnh)
- File cấu hình `application.yml` hoặc `application.properties`

### Các bước cài đặt:

1. **Clone repository:**
   ```bash
   git clone https://github.com/your-username/pharmacy.git
   cd pharmacy
2. **Khởi chạy project**
   ```bash
   ./mvnw spring-boot:run
