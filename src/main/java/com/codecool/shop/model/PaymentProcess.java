package com.codecool.shop.model;

import com.codecool.shop.controller.AdminLog;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class PaymentProcess extends AbstractProcess {
    private int processId;
    private static AtomicInteger idCounter = new AtomicInteger(10000);

    public PaymentProcess() {
        this.processId = idCounter.getAndIncrement();
    }

    protected void action(Order order) {
        order.pay(this);
        HashMap<String, String> buyerInfo = order.getCheckoutProcess().getBuyerInfo();
        JSONArray buyerJsonArray = new JSONArray();
        for (HashMap.Entry<String, String> pair : buyerInfo.entrySet()) {
            buyerJsonArray.add(pair.getKey() +":"+ pair.getValue());
        }
        String orderString = order.getCartItems().toJSONString();
        AdminLog cartLog = CheckoutProcess.getCartLog();
        cartLog.logStringToAdminLog(order.getId(), orderString);
        cartLog.logStringToAdminLog(order.getId(), buyerJsonArray.toJSONString());
        cartLog.logStringToAdminLog(order.getId(),"Order with ID: " + order.getId() + " payment successful.");
    }
}
