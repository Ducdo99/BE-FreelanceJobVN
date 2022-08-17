package group5.freelancejob.services;

import group5.freelancejob.utils.PaymentHistoryStatus;
import group5.freelancejob.utils.zcrypto.HMACUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

@Service
public class PaymentService {
    private final Logger logger = Logger.getLogger(PaymentService.class.getName());

    @Autowired
    private JobScheduler jobScheduler;
    @Autowired
    private PaymentHistoryService _paymentHistoryService;

    @Value("${zpay.app-id}")
    private String appId;

    @Value("${zpay.key1}")
    private String key1;
    @Value("${zpay.key2}")
    private String key2;
    @Value("${zpay.endpoint}")
    private String endpoint;

    private final String zQueryEndpoint = "https://sb-openapi.zalopay.vn/v2/query";


    public String getCurrentTimeString(String format, String additionalTime) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        if (additionalTime != null) {
            cal.add(Calendar.MINUTE, Integer.parseInt(additionalTime));
        }
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public String createOrder(String userName, Long accId, String amount, Map[] item) throws IOException {
        Random rand = new Random();
        int random_id = rand.nextInt(100000000);
        String transId = getCurrentTimeString("yyMMdd", null) + "_" + random_id;
        String description = "FVN - Nạp tiền cho đơn #" + random_id;
        Map embed_data = new HashMap<>() {{
            put("bankgroup", "ATM");
            put("redirecturl", "http://localhost:8080/setting/payment-history");
        }};

        Map<String, Object> order = new HashMap<>() {{
            put("app_id", appId);
            put("app_trans_id", transId); // translation missing: vi.docs.shared.sample_code.comments.app_trans_id
            put("app_time", System.currentTimeMillis()); // miliseconds
            put("app_user", userName);
            put("amount", amount);
            put("description", description);
            put("bank_code", "");
            put("item", new JSONArray(item).toString());
            put("embed_data", new JSONObject(embed_data).toString());
            put("callback_url", "http://localhost:8080/payment/zalocallback");
        }};


        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|" + order.get("amount")
                + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|" + order.get("item");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(endpoint);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        _paymentHistoryService.createOrderHistory(accId, amount, String.valueOf(random_id), description);


        JSONObject result =
                makeRequestToZPay(client, post, params);

        String dataForChecking = appId + "|" + transId + "|" + key1;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, dataForChecking);

        LocalDateTime then = LocalDateTime.now();

        // While loop should be better
        // but dunno how to delay while loop
        // to limit the request rate
        // so using recurring for now
        jobScheduler.scheduleRecurrently(String.valueOf(random_id), Duration.ofSeconds(45)
                , () -> checkForPayment(String.valueOf(random_id), accId, appId, transId, mac, then));

        return (String) result.get("order_url");
    }

    @Job(name = "PaymentCheck", retries = 0)
    // TransactionId is both orderId and background job id
    public void checkForPayment(String transactionId, Long accId, String appId, String appTransID, String mac, LocalDateTime then) throws InterruptedException, URISyntaxException, IOException {


        try {
            logger.info("Checking for payment for order #" + transactionId + " at " + then);

            int respCode = getZPayOrderStatus(appId, appTransID, mac);
            if (ChronoUnit.MINUTES.between(then, LocalDateTime.now()) > 20) {
                updatePaymentStatus(transactionId, accId, false);
                throw new InterruptedException("Payment timeout");
            }

            if (respCode == 2) {
                //TODO: Notify user that payment is failed
                updatePaymentStatus(transactionId, accId, false);
                throw new InterruptedException("Payment failed");
            }

            if (respCode == 1) {
                //TODO: Notify user that payment is success
                updatePaymentStatus(transactionId, accId, true);
                throw new InterruptedException("Payment success");
            }
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
            deleteDoneJob(transactionId);
            throw e;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private void deleteDoneJob(String id) {
        jobScheduler.delete(id);
    }

    private int getZPayOrderStatus(String appId, String appTransID, String mac) throws URISyntaxException, IOException {
        List<NameValuePair> zQueryParams = new ArrayList<>();
        zQueryParams.add(new BasicNameValuePair("app_id", appId));
        zQueryParams.add(new BasicNameValuePair("app_trans_id", appTransID));
        zQueryParams.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(zQueryEndpoint);
        uri.addParameters(zQueryParams);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        var result = makeRequestToZPay(client, post, zQueryParams);

        return (int) result.get("return_code");
    }

    private JSONObject makeRequestToZPay(CloseableHttpClient client, HttpPost post, List<NameValuePair> params) throws IOException {
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        for (String key : result.keySet()) {
            System.out.format("%s = %s\n", key, result.get(key));
        }
        return result;
    }

    private void updatePaymentStatus(String transactionId, Long accId, boolean isSuccess) {
        if (isSuccess) {
            _paymentHistoryService.updateOrderHistory(transactionId, accId, PaymentHistoryStatus.SUCCESS);
        } else {
            _paymentHistoryService.updateOrderHistory(transactionId, accId, PaymentHistoryStatus.FAILED);
        }
    }
}

