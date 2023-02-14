package com.unitecker.app.services;

public interface CallBackBilling {
    void onPurchase();
    void onNotPurchase();
    void onNotLogin();
}
