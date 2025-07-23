package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.CartItems;
import com.universityproject.webapp.foodstore.entity.Donations;

public interface DonationsService {
    CartItems donateProduct(int buyerId, int productId, int quantity , int charityId) throws Exception;
}