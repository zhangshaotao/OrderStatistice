package com.baihe.kafka.order;

import java.sql.SQLException;
import java.util.Properties;

import com.baihe.kafka.KafkaProperties;

public class FactoryWork {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Properties props = new Properties();
		props.put("zookeeper.connect",KafkaProperties.zkConnect);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("producer.type", "async");
		props.put("compression.codec", "1");
		props.put("metadata.broker.list", KafkaProperties.kafkaServerURL+":"+KafkaProperties.kafkaServerPort);
		
		props.put("orderNum",5);
		
		
		
		
		OrderFactory orderFactory = new OrderFactory(props);
		
		try 
		{
			orderFactory.setConn();
			//orderFactory.sendOrder();
			
			Thread thread = new Thread(orderFactory.new AutoPreduceOrder(orderFactory));
			thread.start();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}

}
