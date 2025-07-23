package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Billing;

import java.util.List;

public interface BillingService {
    Billing checkout(int userId, Billing.paymentMethod paymentMethod);
    Billing save(Billing billing);


}