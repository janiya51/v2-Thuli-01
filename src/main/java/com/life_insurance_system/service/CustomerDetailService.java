package com.life_insurance_system.service;

import com.life_insurance_system.model.CustomerDetail;
import com.life_insurance_system.model.User;
import com.life_insurance_system.repository.CustomerDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerDetailService {

    private final CustomerDetailRepository customerDetailRepository;

    @Autowired
    public CustomerDetailService(CustomerDetailRepository customerDetailRepository) {
        this.customerDetailRepository = customerDetailRepository;
    }

    public List<CustomerDetail> getPendingCustomerDetails() {
        return customerDetailRepository.findAll().stream()
                .filter(CustomerDetail::isPendingReview)
                .collect(Collectors.toList());
    }

    public CustomerDetail getCustomerDetailByUser(User user) {
        return customerDetailRepository.findAll().stream()
                .filter(cd -> cd.getUser().getUserId() == user.getUserId())
                .findFirst().orElse(null);
    }

    public void updateCustomerDetail(CustomerDetail customerDetail) {
        customerDetailRepository.save(customerDetail);
    }

    public void approveCustomerUpdate(int customerDetailId) {
        CustomerDetail customerDetail = customerDetailRepository.findById(customerDetailId).orElse(null);
        if (customerDetail != null) {
            customerDetail.setPendingReview(false);
            customerDetailRepository.save(customerDetail);
        }
    }
}