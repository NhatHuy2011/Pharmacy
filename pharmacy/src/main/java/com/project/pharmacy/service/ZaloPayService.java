package com.project.pharmacy.service;

import com.project.pharmacy.configuration.ZaloPayConfig;
import com.project.pharmacy.entity.Orders;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.repository.UserRepository;
import jakarta.xml.bind.DatatypeConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ZaloPayService {
    @NonFinal
    @Value("${zalopay.app_id}")
    protected String app_id;

    @NonFinal
    @Value("${zalopay.app_user}")
    protected String app_user;

    @NonFinal
    @Value("${zalopay.key1}")
    protected String key1;

    @NonFinal
    @Value("${zalopay.key2}")
    protected String key2;

    @NonFinal
    @Value("${zalopay.create-order}")
    protected String createOrder;

    UserRepository userRepository;

    private String getCurrentTimeString(String format) {

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public Map<String, Object> createOrder(String orderId) throws IOException, JSONException {

        String amount = String.valueOf(getAmount(orderId));

        Map<String, Object> order = new HashMap<String, Object>(){{
            put("appid", app_id);
            put("apptransid", getCurrentTimeString("yyMMdd") +"_"+ new Date().getTime()); // translation missing: vi.docs.shared.sample_code.comments.app_trans_id
            put("apptime", System.currentTimeMillis()); // miliseconds
            put("appuser", app_user);
            put("amount", amount);
            put("description", "Pharmacy - Payment for order: " + orderId);
            put("bankcode", "");
            put("item", "[]");
            put("embeddata", "{}");
            put("callback_url", "https://e1e1-171-248-172-160.ngrok-free.app/api/v1/pharmacy/zalopay/callback");
        }};

        String data = order.get("appid") +"|"+ order.get("apptransid") +"|"+ order.get("appuser") +"|"+ order.get("amount")
                +"|"+ order.get("apptime") +"|"+ order.get("embeddata") +"|"+ order.get("item");
        order.put("mac", ZaloPayConfig.HMacHexStringEncode(ZaloPayConfig.HMACSHA256, key1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(createOrder);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {

            resultJsonStr.append(line);
        }

        JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        for (Iterator it = jsonResult.keys(); it.hasNext(); ) {

            String key = (String) it.next();
            finalResult.put(key, jsonResult.get(key));
        }

        return finalResult;
    }

    private int getAmount(String orderId){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Orders orders = user.getOrders().stream()
                .filter(orders1 -> orders1.getId().equals(orderId)
                        && orders1.getPaymentMethod().equals(PaymentMethod.ZALOPAY)
                        && orders1.getStatus().equals(OrderStatus.PENDING))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return orders.getTotalPrice();
    }

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public Object doCallBack(JSONObject result, String jsonStr) throws JSONException, NoSuchAlgorithmException, InvalidKeyException {

        Mac HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(key2.getBytes(), "HmacSHA256"));

        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // check if the callback is valid (from ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback is invalid
                result.put("returncode", -1);
                result.put("returnmessage", "mac not equal");
            } else {
                // payment success
                // merchant update status for order's status
                JSONObject data = new JSONObject(dataStr);
                logger.info("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));

                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // callback again (up to 3 times)
            result.put("return_message", ex.getMessage());
        }

        return result;
    }
}
