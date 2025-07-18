package com.project.pharmacy.service.payment;

import com.project.pharmacy.entity.Orders;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.utils.MoMoUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MoMoService {
    @NonFinal
    @Value("${momo.partnerCode}")
    protected String partnerCode;

    @NonFinal
    @Value("${momo.accessKey}")
    protected String accessKey;

    @NonFinal
    @Value("${momo.secretKey}")
    protected String secretKey;

    @NonFinal
    @Value("${momo.create-order}")
    protected String createOrder;

    @NonFinal
    @Value("${momo.returnUrl}")
    protected String returnUrl;

    @NonFinal
    @Value("${momo.notifyUrl}")
    protected String notifyUrl;

    @NonFinal
    @Value("${momo.requestType}")
    protected String requestType;

    OrderRepository orderRepository;

    public Map<String, Object> createPaymentMoMo(String orderId) throws IOException {

        String amount = String.valueOf(getAmount(orderId));

        JSONObject json = new JSONObject();
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", String.valueOf(System.currentTimeMillis()));
        json.put("amount", amount);
        json.put("orderId", orderId);
        json.put("orderInfo", "Thanh toan don hang " + orderId);
        json.put("returnUrl", returnUrl);
        json.put("notifyUrl", notifyUrl);
        json.put("requestType", requestType);

        String data = "partnerCode=" + partnerCode
                + "&accessKey=" + accessKey
                + "&requestId=" + json.get("requestId")
                + "&amount=" + json.get("amount")
                + "&orderId=" + json.get("orderId")
                + "&orderInfo=" + json.get("orderInfo")
                + "&returnUrl=" + returnUrl
                + "&notifyUrl=" + notifyUrl
                + "&extraData=";

        String signatureKey = MoMoUtil.computeHmacSha256(data, secretKey);
        json.put("signature", signatureKey);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(createOrder);

        StringEntity stringEntity = new StringEntity(json.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(stringEntity);

        CloseableHttpResponse res = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {

            resultJsonStr.append(line);
        }

        JSONObject object = new JSONObject(resultJsonStr.toString());
        Map<String, Object> result = new HashMap<>();
        for (Iterator<String> it = object.keys(); it.hasNext(); ) {

            String key = it.next();
            result.put(key, object.get(key));
        }

        return result;
    }

    private int getAmount(String orderId){
        Orders orders = orderRepository.findById(orderId).stream()
                .filter(orders1 -> orders1.getId().equals(orderId)
                        && orders1.getPaymentMethod().equals(PaymentMethod.MOMO)
                        && orders1.getStatus().equals(OrderStatus.PENDING))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return orders.getNewTotalPrice();
    }
}
