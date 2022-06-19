package com.example.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;

import com.example.demo.model.ProductInfo;
import com.example.demo.utils.StripeService;

@Service
public class ProductDAO {

	private static final Map<String, Product> products = new HashMap<String, Product>();
	
	@Resource
	private StripeService stripeService;

	public ProductDAO() {

		
	}
	
	public Product findProduct(String code) {
		return products.get(code);
	}

	public ProductInfo findProductInfo(String code) {
		Product product = this.findProduct(code);
		if (product == null) {
			return null;
		}
		return new ProductInfo(product.getCode(), product.getName(), product.getPrice(),product.getImage());
	}

	public List<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage, String likeName) {
		
		if(products.isEmpty()) {
			this.createProductMap();
		}
		return products.values().stream().map(product -> {
			return new ProductInfo(product.getCode(), product.getName(), product.getPrice(),product.getImage());
		}).collect(Collectors.toList());

	}

	public List<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage) {
		return queryProducts(page, maxResult, maxNavigationPage, null);
	}
	
	

	private void createProductMap() {
		List<com.stripe.model.Product> prodcutList = stripeService.getProducts();
		
		if(prodcutList.isEmpty()) {
			populateLocalPorducts();	
		}else {
			prodcutList.forEach(p -> {
				Product product = new Product();
				product.setCode(p.getId());
				product.setName(p.getName());
				if(p.getDefaultPrice()!=null) {
					product.setPrice(stripeService.getPrice(p.getDefaultPrice()));
				}
				
				if(!p.getImages().isEmpty()) {
					String imagePath = p.getImages().get(0);
					product.setImage(imagePath);
				}
				products.put(product.getCode(), product);
			});
		}
	}
	
	private void populateLocalPorducts() {
		Product product1 = new Product();

		product1.setCode("1");
		product1.setName("\"The Art of Doing Science and Engineering\"");
		product1.setPrice(2300);
		product1.setImage("");

		Product product2 = new Product();

		product2.setCode("2");
		product2.setName("The Making of Prince of Persia: Journals 1985-1993");
		product2.setPrice(2500);
		product2.setImage("");
		Product product3 = new Product();

		product3.setCode("3");
		product3.setName("Working in Public: The Making and Maintenance of Open Source");
		product3.setPrice(2800);
		product3.setImage("");
		products.put("1", product1);
		products.put("2", product2);
		products.put("3", product3);
	}

}