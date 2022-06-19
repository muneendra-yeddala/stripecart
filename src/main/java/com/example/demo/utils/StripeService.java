package com.example.demo.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.CartInfo;
import com.example.demo.model.CustomerInfo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.CaptureMethod;
import com.stripe.param.ProductListParams;

@Service
public class StripeService {
	
	Logger LOG = LoggerFactory.getLogger(StripeService.class);

	@Value("${STRIPE_PUBLIC_KEY}")
	private String stripePublicKey;

	@Value("${STRIPE_SECRET_KEY}")
	private String stripeSecretKey;

	public List<Product> getProducts() {

		try {
			Stripe.apiKey = stripeSecretKey;
			ProductListParams params = ProductListParams.builder().setActive(Boolean.TRUE).setLimit(Long.valueOf(3))
					.build();

			ProductCollection products = Product.list(params);
			return products.getData();
		} catch (Exception e) {
			LOG.error("",e);
		}
		return List.of();

	}

	public Long getPrice(String id) {

		try {
			Stripe.apiKey = stripeSecretKey;
			Price price = Price.retrieve(id );
			return price.getUnitAmount();
		} catch (Exception e) {
			LOG.error("",e);
		}
		return Long.valueOf(2000);

	}

	public String createIntent(CartInfo cartInfo) throws StripeException {
		Stripe.apiKey = stripeSecretKey;

		String customerId = this.createCustomer(cartInfo.getCustomerInfo());
		PaymentIntentCreateParams paramsIntent = PaymentIntentCreateParams.builder()
				.setAmount((long) cartInfo.getAmountTotal()).setCurrency("jpy").setCustomer(customerId)
				.setConfirm(Boolean.TRUE).setCaptureMethod(CaptureMethod.MANUAL)
				.setDescription("Order for Customer " + cartInfo.getCustomerInfo().getEmail()).build();

		PaymentIntent charge = PaymentIntent.create(paramsIntent);
		return charge.getId();
	}

	public String captureIntent(String id) throws StripeException {
		Stripe.apiKey = stripeSecretKey;

		PaymentIntent intent = PaymentIntent.retrieve(id);

		PaymentIntent updatedCharge = intent.capture();
		return updatedCharge.getId();
	}

	private String createCustomer(CustomerInfo customer) throws StripeException {
		Stripe.apiKey = stripeSecretKey;

		CustomerCreateParams params = CustomerCreateParams.builder().setEmail(customer.getEmail())
				.setSource(customer.getPaymentToken()).build();
		Customer customerStripe = Customer.create(params);
		return customerStripe.getId();
	}

	public String createCharge(CartInfo cartInfo) throws StripeException {
		Stripe.apiKey = stripeSecretKey;
		String customerId = this.createCustomer(cartInfo.getCustomerInfo());
		ChargeCreateParams params = ChargeCreateParams.builder().setAmount((long) cartInfo.getAmountTotal())
				.setCurrency("jpy").setCustomer(customerId)
				// .setSource(cartInfo.getCustomerInfo().getPaymentToken())
				.setCapture(Boolean.FALSE).setDescription("Order for Customer " + cartInfo.getCustomerInfo().getEmail())
				.build();
//		
		Charge charge = Charge.create(params);
		return charge.getId();
	}

}
