package com.project.pharmacy.utils;

public class EmailTemplateUtils {

    public static String buildOtpEmail(String otp) {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                "<h2 style=\"color: #2c3e50;\">XÁC MINH EMAIL</h2>" +
                "<p>OTP của bạn là:</p>" +
                "<div style=\"font-size: 24px; font-weight: bold; color: #e74c3c; margin: 10px 0;\">" +
                otp + "</div>" +
                "<p>OTP sẽ hết hạn trong vòng <strong>5 phút</strong>.</p>" +
                "<p><strong>Vui lòng không chia sẻ mã này với bất kỳ ai.</strong></p>" +
                "<br><em>PHARMACY TEAM</em>" +
                "</div>";
    }

    public static String buildOutOfStockEmail(String couponId) {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                "<h2 style=\"color: #e74c3c;\">XIN LỖI QUÝ KHÁCH!</h2>" +
                "<p>Chúng tôi xin lỗi vì 1 trong số sản phẩm bạn đặt hiện đã hết hàng.</p>" +
                "<p>Rất tiếc khi đơn hàng có thể sẽ giao chậm hơn thời gian dự kiến</p>" +
                "<p>Để thay lời xin lỗi, chúng tôi gửi tặng bạn một mã giảm giá:</p>" +
                "<div style=\"background-color: #f1f1f1; padding: 15px; border-radius: 8px; margin: 10px 0;\">" +
                "<strong>Mã giảm giá ID:</strong> " + couponId + "<br>" +
                "<span style=\"color: #888;\">Có hiệu lực trong 7 ngày tới</span>" +
                "</div>" +
                "<p>Cảm ơn bạn đã thông cảm và tiếp tục ủng hộ!</p>" +
                "<br><em>PHARMACY TEAM</em>" +
                "</div>";
    }
}
