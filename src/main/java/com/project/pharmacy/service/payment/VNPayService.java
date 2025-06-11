package com.project.pharmacy.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.project.pharmacy.dto.response.payment.RefundPaymentResponse;
import com.project.pharmacy.utils.VNPayUtil;
import com.project.pharmacy.entity.Orders;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService {
    @NonFinal
    @Value("${vnpay.vnp_TmnCode}")
    protected String vnp_TmnCode;

    @NonFinal
    @Value("${vnpay.secretKey}")
    protected String secretKey;

    @NonFinal
    @Value("${vnpay.vnp_Version}")
    protected String vnp_Version;

    @NonFinal
    @Value("${vnpay.vnp_Command}")
    protected String vnp_Command;

    @NonFinal
    @Value("${vnpay.vnp_PayUrl}")
    protected String vnp_PayUrl;

    @NonFinal
    @Value("${vnpay.vnp_ReturnUrl}")
    protected String vnp_ReturnUrl;

    @NonFinal
    @Value("${vnpay.vnp_ApiUrl}")
    protected String vnp_ApiUrl;

    OrderRepository orderRepository;

    //Tạo đơn hàng
    public String createPaymentVNPayWeb(HttpServletRequest req){
        Orders orders = orderRepository.findById(req.getParameter("orderId")).stream()
                .filter(orders1 -> orders1.getId().equals(req.getParameter("orderId"))
                        && orders1.getPaymentMethod().equals(PaymentMethod.VNPAY)
                        && orders1.getStatus().equals(OrderStatus.PENDING))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        long amount = orders.getNewTotalPrice() * 100L;

        String bankCode = req.getParameter("bankCode");
        //String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayUtil.getIpAddress(req);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_Locale", "vn");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", req.getParameter("orderId"));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + req.getParameter("orderId"));
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        orders.setLinkOrder(paymentUrl);
        orderRepository.save(orders);

        return paymentUrl;
    }

    //Hoàn tiền
    public RefundPaymentResponse refundVNPay(HttpServletRequest req) {
        Orders orders = orderRepository.findById(req.getParameter("orderId"))
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        String linkOrder = orders.getLinkOrder();
        if (linkOrder == null) {
            throw new AppException(ErrorCode.ORDER_LINK_NOT_FOUND);
        }

        try {
            Map<String, String> params = parseQueryParams(linkOrder);

            String vnp_RequestId = VNPayUtil.getRandomNumber(8);
            String vnp_Version = "2.1.0";
            String vnp_Command = "refund";
            String vnp_TransactionType = "02";
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_TransactionDate = params.get("vnp_CreateDate");
            String vnp_CreateBy = "Pharmacy";

            String vnp_Amount = params.get("vnp_Amount");
            String vnp_OrderInfo = "Hoàn tiền giao dịch OrderId: " + vnp_TxnRef;
            String vnp_TransactionNo = "";

            String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7")).getTime());

            String vnp_IpAddr = VNPayUtil.getIpAddress(req);

            String rawData = String.join("|",
                    vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                    vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo,
                    vnp_TransactionDate, vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo
            );

            String vnp_SecureHash = VNPayUtil.hmacSHA512(secretKey, rawData);

            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("vnp_RequestId", vnp_RequestId);
            jsonRequest.addProperty("vnp_Version", vnp_Version);
            jsonRequest.addProperty("vnp_Command", vnp_Command);
            jsonRequest.addProperty("vnp_TmnCode", vnp_TmnCode);
            jsonRequest.addProperty("vnp_TransactionType", vnp_TransactionType);
            jsonRequest.addProperty("vnp_TxnRef", vnp_TxnRef);
            jsonRequest.addProperty("vnp_Amount", vnp_Amount);
            jsonRequest.addProperty("vnp_OrderInfo", vnp_OrderInfo);
            jsonRequest.addProperty("vnp_TransactionDate", vnp_TransactionDate);
            jsonRequest.addProperty("vnp_CreateBy", vnp_CreateBy);
            jsonRequest.addProperty("vnp_CreateDate", vnp_CreateDate);
            jsonRequest.addProperty("vnp_IpAddr", vnp_IpAddr);
            jsonRequest.addProperty("vnp_SecureHash", vnp_SecureHash);

            // Gửi request đến VNPay
            URL url = new URL(vnp_ApiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.toString().getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseStr.append(line);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseStr.toString(), RefundPaymentResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.VNPAY_REFUND_FAILED, e.getMessage());
        }
    }

    public static Map<String, String> parseQueryParams(String url) throws Exception {
        Map<String, String> queryPairs = new HashMap<>();
        URI uri = new URI(url);
        String query = uri.getQuery();

        if (query == null) {
            // Trong một số trường hợp URI.getQuery() trả về null nếu URL không đúng định dạng
            query = url.substring(url.indexOf('?') + 1);
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
            queryPairs.put(key, value);
        }
        return queryPairs;
    }
}
