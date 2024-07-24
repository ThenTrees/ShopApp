package com.thentrees.shopapp.services.vnpay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.configuration.VNPAYConfig;
import com.thentrees.shopapp.constant.OrderStatus;
import com.thentrees.shopapp.constant.TypeOfPayment;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.models.Order;
import com.thentrees.shopapp.models.Payment;
import com.thentrees.shopapp.repositories.OrderRepository;
import com.thentrees.shopapp.repositories.PaymentRepository;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    @Value("${payment.vnPay.secretKey}")
    private String secretKey;

    @Value("${payment.vnPay.version}")
    private String vnp_Version;

    @Value("${payment.vnPay.command}")
    private String vnp_Command;

    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode;
    // amount
    @Value("${payment.vnPay.bankCode}")
    private String vnp_BankCode;
    // create date
    @Value("${payment.vnPay.vnp_PayUrl}")
    private String vnp_PayUrl;

    @Value("${payment.vnPay.vnp_CurrCode}")
    private String vnp_CurrCode;

    @Value("${payment.vnPay.vnp_ReturnUrl}")
    private String vnp_ReturnUrl;

    @Value("${payment.vnPay.orderType}")
    private String orderType;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    public ResponseObject createPayment(Long orderId, HttpServletRequest req) throws UnsupportedEncodingException {
        log.info("Creating payment for order: {}", vnp_PayUrl);
        String vnp_TxnRef = VNPAYConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPAYConfig.getIpAddress(req);

        Order order = findOrder(orderId);

        int price = (int) order.getTotalMoney();
        log.info("Price: {}", price);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(price * 100));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_BankCode", vnp_BankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        Payment payment = Payment.builder()
                .vnpCommand(vnp_Command)
                .vnpAmount(price)
                .vnpBankCode(vnp_BankCode)
                .vnpOrderInfo(vnp_TxnRef + "info")
                .vnpOrderType(orderType)
                .vnpTxnRef(vnp_TxnRef)
                .order(order)
                .status(TypeOfPayment.UNPAID)
                .build();
        paymentRepository.save(payment);

        return ResponseObject.builder()
                .message("Get payment url successfully")
                .code(HttpStatus.OK.value())
                .data(paymentUrl)
                .build();
    }

    @Override
    public ResponseObject callback(HttpServletRequest req) throws UnsupportedEncodingException {
        String status = req.getParameter("vnp_ResponseCode");
        Payment payment = paymentRepository.findByVnpTxnRef(req.getParameter("vnp_TxnRef"));
        ResponseObject rs = new ResponseObject();

        switch (status) {
            case "00":
                log.info("Payment success");
                payment.setStatus(TypeOfPayment.PAID);
                paymentRepository.save(payment);
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_SUCCESS));
                rs.setCode(00);
                break;
            case "01":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_INCOMPLETE));
                rs.setCode(01);
                break;
            case "02":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_DEFECTIVE));
                rs.setCode(02);
                break;
            case "04":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_ISLAND));
                rs.setCode(04);
                break;
            case "05":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_PROCESSING));
                rs.setCode(05);
                break;
            case "06":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_REQUEST_REFUND));
                rs.setCode(06);
                break;
            case "07":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_SUSPECTED));
                rs.setCode(07);
                break;
            case "09":
                rs.setMessage(localizationUtils.getLocalizationMessage(MessageKeys.PAYMENT_REFUND_DENIED));
                rs.setCode(9);
                break;
        }
        return ResponseObject.builder()
                .message(rs.getMessage())
                .code(rs.getCode())
                .build();
    }

    @Override
    public ResponseObject ipnPayment(HttpServletRequest request) throws UnsupportedEncodingException {
        log.info("IPN payment");
        Map<String, String> fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue =
                    URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        // Check checksum
        String signValue = VNPAYConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            boolean checkOrderId =
                    paymentRepository.existsByVnpTxnRef(vnp_TxnRef); // vnp_TxnRef exists in your database

            Payment payment = paymentRepository.findByVnpTxnRef(vnp_TxnRef);
            Order order = orderRepository.findByVnp_TxnRef(vnp_TxnRef);

            boolean checkAmount = checkAmount((int) order.getTotalMoney(), (int)
                    payment.getVnpAmount()); // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the
            //            amount of the code (vnp_TxnRef) in the Your database).
            boolean checkOrderStatus = payment.getStatus() == TypeOfPayment.PAID; // PaymnentStatus = 0 (pending)
            if (checkOrderId) {
                if (checkAmount) {
                    if (checkOrderStatus) {
                        if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                            order.setStatus(OrderStatus.SHIPPED);
                            orderRepository.save(order);
                            // Here Code update PaymnentStatus = 1 into your Database
                        } else {
                            // Here Code update PaymnentStatus = 2 into your Database
                        }
                        log.info("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                    } else {

                        log.info("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                    }
                } else {
                    log.info("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                }
            } else {
                log.info("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
            }
        } else {
            log.info("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        }
        return ResponseObject.builder()
                .message("IPN payment")
                .code(HttpStatus.OK.value())
                .build();
    }

    private boolean checkAmount(int price, int vnp_Amount) {
        return price == vnp_Amount;
    }

    private Order findOrder(long orderId) {
        return orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
    }

    private boolean validateSignature(Map<String, String[]> parameterMap) {
        // Thực hiện logic xác nhận chữ ký (signature) của VNPay
        // Trả về true nếu hợp lệ, false nếu không hợp lệ
        return true; // Đây chỉ là ví dụ, bạn cần triển khai logic thực tế
    }

    private void updateTransactionStatus(String orderId, String status) {
        // Thực hiện cập nhật trạng thái giao dịch trong cơ sở dữ liệu của bạn
    }
}
