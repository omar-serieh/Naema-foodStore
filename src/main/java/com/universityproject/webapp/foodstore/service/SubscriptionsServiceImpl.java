package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Subscriptions;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.SubscriptionRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class SubscriptionsServiceImpl implements SubscriptionsService {

    @Autowired
    private SubscriptionRepository subscriptionsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public void extendSubscription(int userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        Optional<Subscriptions> optional = subscriptionsRepository.findByUserId(user);

        Subscriptions subscription = optional.orElseGet(() -> {
            Subscriptions newSub = new Subscriptions();
            newSub.setUserId(user);
            newSub.setStatus(true);
            return newSub;
        });

        Date now = new Date();
        Date currentEnd = subscription.getEndDate();
        Calendar calendar = Calendar.getInstance();

        if (currentEnd != null && currentEnd.after(now)) {
            calendar.setTime(currentEnd); // تمديد من تاريخ الانتهاء الحالي
        } else {
            calendar.setTime(now);
            if (subscription.getStartDate() == null) {
                subscription.setStartDate(now); // تحفظ تاريخ أول اشتراك
            }
        }

        calendar.add(Calendar.DAY_OF_MONTH, 30);
        subscription.setEndDate(calendar.getTime());

        // تفعيل الاشتراك وحفظ التحديثات
        subscription.setStatus(true);
        subscriptionsRepository.save(subscription);

        user.setSubscriptionStatus(true);
        usersRepository.save(user);
    }
}
