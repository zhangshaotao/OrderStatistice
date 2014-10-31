package com.baihe.kafka.order;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.baihe.bean.Order;
import com.baihe.kafka.KafkaProperties;
import com.baihe.storm.utils.JDBCUtils;

public class OrderFactory {

	private Producer<String,String> producer = null;
	private int orderNum = 10;
	private Connection conn = null;
	private Statement stmt = null;
	
	public OrderFactory(Properties prop)
	{			
		ProducerConfig producerConf = new ProducerConfig(prop);
		producer = new Producer<String,String>(producerConf);
		if(prop.get("orderNum") != null)
		{
			orderNum = (Integer)prop.get("orderNum");
		}	
	}
	
	public void sendOrder() throws SQLException
	{
		List<Order> orderList = this.produceOrder();
		String sql = null;
		
		for(Order order:orderList)
		{
			producer.send(new KeyedMessage<String, String>(KafkaProperties.order_topic,order.toString()));
			System.out.println(order.toString());
			
			sql = this.insertMysql(order);
			
			stmt.addBatch(sql);
			System.out.println(sql);
		}
		
		System.out.println("--------------------------------------");
		
		producer.close();
		stmt.executeBatch();
		conn.commit();
		
		stmt.close();
		conn.close();
		
	}
	
	public List<Order> produceOrder()
	{
		List<Order> orderList = new ArrayList<Order>();
		
		String[] date = {"2014-10-08","2014-10-09","2014-10-10","2014-10-11","2014-10-12"};
		
		int[] userids = {38660,13914,15396,48067,85478,30922,43593,28881,45842,61831,
				38621,13324,15236,45827,84178,30832,43373,28421,45652,61321};
		int[] prices = {299,399,259,499,199,99,309,359,359,209};
		int[] youhuis = {10,20,30};
		
		Random r = new Random();
		for(int i=0;i<orderNum;i++)
		{
			int id = r.nextInt(10000000);
			int userid = userids[r.nextInt(10)];
			int totalprice = prices[r.nextInt(10)];
			int youhui = youhuis[r.nextInt(3)];
			int sendpay = r.nextInt(3);
			String createDate = date[r.nextInt(5)];
			
			Order order = new Order(id, userid, totalprice, youhui, sendpay, createDate);
			
			orderList.add(order);
		}

		return orderList;
	}
	
	public String insertMysql(Order order)
	{
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("insert into test.Order(ID,UserID,TotalPrice,Youhui,Sendpay,CreateDate) values(")
		.append(order.getId())
		.append(",")
		.append(order.getUserid())
		.append(",")
		.append(order.getTotalprice())
		.append(",")
		.append(order.getYouhui())
		.append(",")
		.append(order.getSendpay())
		.append(",'")
		.append(order.getCreateDate())
		.append("')");
		
		return sqlBuffer.toString();
	}
	
	public void setConn() throws ClassNotFoundException, SQLException
	{
		this.conn = JDBCUtils.getConnection();
		this.stmt = this.conn.createStatement();
	}
	
	class AutoPreduceOrder implements Runnable
	{
		private Random random;
		private OrderFactory factory;
		
		public AutoPreduceOrder(OrderFactory factory)
		{
			this.random = new Random();
			this.factory = factory;
		}
		
		public void run()
		{
			while(true)
			{
				try 
				{
					
					List<Order> orderList = factory.produceOrder();
					String sql = null;
					
					for(Order order:orderList)
					{
						producer.send(new KeyedMessage<String, String>(KafkaProperties.order_topic,order.toString()));
						System.out.println(order.toString());
						
						sql = factory.insertMysql(order);
						
						stmt.addBatch(sql);
						System.out.println(sql);
					}
					
					System.out.println("--------------------------------------");
					
					stmt.executeBatch();
					conn.commit();
					
					Thread.sleep((random.nextInt(3)+ 2) * 1000);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				} 
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
}
