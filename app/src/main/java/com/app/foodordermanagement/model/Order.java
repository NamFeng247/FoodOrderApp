package com.app.foodordermanagement.model;

import com.app.foodordermanagement.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    public static final int STATUS_NEW = 1;
    public static final int STATUS_DOING = 2;
    public static final int STATUS_ARRIVED = 3;
    public static final int STATUS_COMPLETE = 4;

    private long id;
    private String userEmail;
    private String dateTime;
    private List<FoodOrder> foods;
    private int price;
    private int voucher;
    private int total;
    private String paymentMethod;
    private int status;
    private double rate;
    private String review;
    private Address address;

    public Order() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<FoodOrder> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodOrder> foods) {
        this.foods = foods;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getVoucher() {
        return voucher;
    }

    public void setVoucher(int voucher) {
        this.voucher = voucher;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getListFoodsName() {
        if (foods == null || foods.isEmpty()) return "";
        String result = "";
        for (FoodOrder foodOrder : foods) {
            if (StringUtil.isEmpty(result)) {
                result += foodOrder.getName();
            } else {
                result += ", " + foodOrder.getName();
            }
        }
        return result;
    }
}
