package com.baihe.bean;

public class OrderTotal {

	private String ID;
	private int orderNums;
	private double pTotalPrice;
	private double yTotalPrice;
	private int orderMember;
	private String sendpay;
	
	public OrderTotal(String iD, int orderNums, double pTotalPrice,
			double yTotalPrice, int orderMember, String sendpay) {
		super();
		ID = iD;
		this.orderNums = orderNums;
		this.pTotalPrice = pTotalPrice;
		this.yTotalPrice = yTotalPrice;
		this.orderMember = orderMember;
		this.sendpay = sendpay;
	}
	
	public OrderTotal() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public int getOrderNums() {
		return orderNums;
	}
	public void setOrderNums(int orderNums) {
		this.orderNums = orderNums;
	}
	public double getpTotalPrice() {
		return pTotalPrice;
	}
	public void setpTotalPrice(double pTotalPrice) {
		this.pTotalPrice = pTotalPrice;
	}
	public double getyTotalPrice() {
		return yTotalPrice;
	}
	public void setyTotalPrice(double yTotalPrice) {
		this.yTotalPrice = yTotalPrice;
	}
	public int getOrderMember() {
		return orderMember;
	}
	public void setOrderMember(int orderMember) {
		this.orderMember = orderMember;
	}
	public String getSendpay() {
		return sendpay;
	}
	public void setSendpay(String sendpay) {
		this.sendpay = sendpay;
	}
}
