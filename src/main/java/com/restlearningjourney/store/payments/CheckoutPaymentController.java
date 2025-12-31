package com.restlearningjourney.store.payments;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutPaymentController {

    @GetMapping("/checkout-success")
    public String checkoutSuccess(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "checkout-success";
    }

    @GetMapping("/checkout-cancel")
    public String checkoutCancel() {
        return "checkout-cancel";
    }
}
