package group5.freelancejob.controllers;

import group5.freelancejob.services.AccountService;
import group5.freelancejob.services.PaymentHistoryService;
import group5.freelancejob.services.PaymentService;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private final PaymentService _paymentService;
    private final PaymentHistoryService _paymentHistoryService;
    private final AccountService _accountService;

    @Value("${zpay.key2}")
    private String key2;

    public PaymentController(PaymentService paymentService, PaymentHistoryService paymentHistoryService, AccountService accountService) {
        _paymentService = paymentService;
        _paymentHistoryService = paymentHistoryService;
        _accountService = accountService;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestParam("accountId") Long accountId) {
        return ResponseEntity.ok(Collections.singletonMap("balance", Math.round(_accountService.getBalance(accountId))));
    }

    @GetMapping("/payment-history")
    public ResponseEntity<?> getPaymentHistory(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(_paymentHistoryService.getPaymentHistoryById(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<?> createOrder(@RequestBody TopupBody body) throws IOException {
        Map[] items = {
                new HashMap<String, String>() {{
                    put("item_name", "Topup");
                    put("item_price", body.getAmount());
                }}
        };

        String orderUrl = _paymentService.createOrder(body.getUsername(), body.getUserId(), body.getAmount(),
                items);
        return ResponseEntity.ok().body(Collections.singletonMap("url", orderUrl));
    }

    @PostMapping("/zalocallback")
    @CrossOrigin(origins = "*")
    public String zaloCallBack(@RequestBody String jsonStr) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Mac HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(key2.getBytes(), "HmacSHA256"));
        JSONObject result = new JSONObject();
        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // kiểm tra callback hợp lệ (đến từ ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback không hợp lệ
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                // thanh toán thành công
                // merchant cập nhật trạng thái cho đơn hàng
                JSONObject data = new JSONObject(dataStr);
                logger.info("update order's status = success where app_trans_id = " +
                        data.getString("app_trans_id"));

                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // ZaloPay server sẽ callback lại (tối đa 3 lần)
            result.put("return_message", ex.getMessage());
        }
        logger.info("ZaloPay callback result: " + result.toString());
        return result.toString();
    }
}

@Getter
class TopupBody {
    private String username;
    private Long userId;
    private String amount;
}