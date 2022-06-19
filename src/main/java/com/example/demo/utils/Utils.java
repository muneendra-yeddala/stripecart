package com.example.demo.utils;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.model.CartInfo;

public class Utils {

   // Products in the cart, stored in Session.
   public static CartInfo getCartInSession(HttpServletRequest request) {

      CartInfo cartInfo = (CartInfo) request.getSession().getAttribute(request.getSession().getId());

   
      if (cartInfo == null) {
         cartInfo = new CartInfo();
         
         request.getSession().setAttribute(request.getSession().getId(), cartInfo);
      }

      return cartInfo;
   }

   public static void removeCartInSession(HttpServletRequest request) {
      request.getSession().removeAttribute(request.getSession().getId());
   }

   public static void storeLastOrderedCartInSession(HttpServletRequest request, CartInfo cartInfo) {
      request.getSession().setAttribute("lastOrderedCart", cartInfo);
   }

   public static CartInfo getLastOrderedCartInSession(HttpServletRequest request) {
      return (CartInfo) request.getSession().getAttribute("lastOrderedCart");
   }
   
}