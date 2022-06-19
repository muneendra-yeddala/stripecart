package com.example.demo.dao;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;
import com.example.demo.model.CartInfo;
import com.example.demo.model.CartLineInfo;
import com.example.demo.model.CustomerInfo;


@Service
public class OrderDAO {

	
	private static final Map<String, Order> orders = new HashMap<>();

	@Autowired
	private ProductDAO productDAO;

	private int getMaxOrderNum() {
		Random r = new Random();
		int low = 10;
		int high = 100;
		int result = r.nextInt(high-low) + low;
		return result;
	}

	
	public Order saveOrder(CartInfo cartInfo) {
		
		int orderNum = this.getMaxOrderNum();
		Order order = new Order();

		order.setId(UUID.randomUUID().toString());
		order.setOrderNum(cartInfo.getOrderNum());
		order.setOrderDate(new Date());
		order.setAmount(cartInfo.getAmountTotal());

		CustomerInfo customerInfo = cartInfo.getCustomerInfo();
		order.setCustomerName(customerInfo.getName());
		order.setCustomerEmail(customerInfo.getEmail());
		order.setCustomerPhone(customerInfo.getPhone());
		order.setCustomerAddress(customerInfo.getAddress());

	

		List<CartLineInfo> lines = cartInfo.getCartLines();

		for (CartLineInfo line : lines) {
			OrderDetail detail = new OrderDetail();
			detail.setId(UUID.randomUUID().toString());
			detail.setOrder(order);
			detail.setAmount(line.getAmount());
			detail.setPrice(line.getProductInfo().getPrice());
			detail.setQuanity(line.getQuantity());

			String code = line.getProductInfo().getCode();
			Product product = this.productDAO.findProduct(code);
			detail.setProduct(product);

	
		}

		// Order Number!
		cartInfo.setOrderNum(cartInfo.getOrderNum());
		
		orders.put(order.getId(), order);
		return order;

	}


	public Order findOrder(String orderId) {
		
		return orders.get(orderId);
	}

}