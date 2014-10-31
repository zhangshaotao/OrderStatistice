package com.baihe.bean;

public class Order {
	private int id;
	private int userid;
	private int totalprice;
	private int youhui;
	private int sendpay;
	private String createDate;
	
	public Order(){}
	
	public Order(int id, int userid, int totalprice, int youhui, int sendpay, String createDate) {
		super();
		this.id = id;
		this.userid = userid;
		this.totalprice = totalprice;
		this.youhui = youhui;
		this.sendpay = sendpay;
		this.createDate = createDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getTotalprice() {
		return totalprice;
	}
	public void setTotalprice(int totalprice) {
		this.totalprice = totalprice;
	}
	public int getYouhui() {
		return youhui;
	}
	public void setYouhui(int youhui) {
		this.youhui = youhui;
	}
	public int getSendpay() {
		return sendpay;
	}
	public void setSendpay(int sendpay) {
		this.sendpay = sendpay;
	}

	@Override
	public String toString() {
		
		StringBuffer orderBuffer = new StringBuffer();
		orderBuffer.append(String.valueOf(id))
		.append("\t")
		.append(String.valueOf(userid))
		.append("\t")
		.append(String.valueOf(totalprice))
		.append("\t")
		.append(String.valueOf(youhui))
		.append("\t")
		.append(String.valueOf(sendpay))
		.append("\t")
		.append(createDate);
		
		return orderBuffer.toString();
	}
	

}
