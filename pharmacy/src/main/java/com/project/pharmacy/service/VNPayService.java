package com.project.pharmacy.service;

import com.project.pharmacy.configuration.VNPayConfig;
import com.project.pharmacy.entity.Orders;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import com.project.pharmacy.enums.Level;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
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

    UserRepository userRepository;

    OrderRepository orderRepository;

    public String createPaymentVNPay(HttpServletRequest req){
        long amount = getAmount(req.getParameter("orderId")) * 100L;

        String bankCode = req.getParameter("bankCode");
        //String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

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
        String vnp_SecureHash = VNPayConfig.hmacSHA512(secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }

    private int getAmount(String orderId){
        Orders orders = orderRepository.findById(orderId).stream()
                .filter(orders1 -> orders1.getId().equals(orderId)
                        && orders1.getPaymentMethod().equals(PaymentMethod.VNPAY)
                        && orders1.getStatus().equals(OrderStatus.PENDING))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return orders.getTotalPrice();
    }

    public void callBack(String responseCode, String orderId){
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User user = orders.getUser();

        if (user == null){
            if(responseCode.equals("00")){
                orders.setStatus(OrderStatus.SUCCESS);
            } else {
                orders.setStatus(OrderStatus.FAILED);
            }

            orderRepository.save(orders);
        }
        else {
            if(responseCode.equals("00")){
                orders.setStatus(OrderStatus.SUCCESS);
                user.setPoint(user.getPoint() + orders.getTotalPrice()/1000);
                if (user.getPoint() >= 8000){
                    user.setLevel(Level.KIMCUONG);
                }
                else {
                    if (user.getPoint() >= 6000){
                        user.setLevel(Level.BACHKIM);
                    }
                    else if (user.getPoint() >= 4000){
                        user.setLevel(Level.VANG);
                    }
                    else if (user.getPoint() >= 2000) {
                        user.setLevel(Level.BAC);
                    }
                    else user.setLevel(Level.DONG);
                }
                userRepository.save(user);
            }
            else {
                orders.setStatus(OrderStatus.FAILED);
            }

            orderRepository.save(orders);
        }
    }
}
