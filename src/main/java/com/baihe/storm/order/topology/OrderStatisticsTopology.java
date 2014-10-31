package com.baihe.storm.order.topology;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

import com.baihe.kafka.KafkaProperties;
import com.baihe.storm.order.bolt.CheckOrderBolt;
import com.baihe.storm.order.bolt.SaveMysqlBolt;
import com.baihe.storm.order.bolt.TranslateBolt;
import com.google.common.collect.ImmutableList;

public class OrderStatisticsTopology {

	public final static String TOPOLOGY_NAME = "order_statistice_topology";
	
	public final static String KAFKA_SPOUT = "kafkaSpout";
	public final static String CHECK_BOLT = "checkBolt";
	public final static String TRANSLATE_BOLT = "translateBolt";
	public final static String SAVE_BOLT = "saveBolt";
	/**
	 * @param args
	 * @throws InvalidTopologyException 
	 * @throws AlreadyAliveException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException
	{
		BrokerHosts brokerHosts = new ZkHosts(KafkaProperties.zkConnect);
		SpoutConfig kafkaConfig = new SpoutConfig(brokerHosts,KafkaProperties.order_topic,"/order","id");
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		kafkaConfig.zkServers = ImmutableList.of(KafkaProperties.kafkaServerURL);
		kafkaConfig.zkPort = 2181;
		
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout(KAFKA_SPOUT, new KafkaSpout(kafkaConfig),1);
		builder.setBolt(CHECK_BOLT, new CheckOrderBolt(),1).shuffleGrouping(KAFKA_SPOUT);
		builder.setBolt(TRANSLATE_BOLT, new TranslateBolt(),1).shuffleGrouping(CHECK_BOLT);
		builder.setBolt(SAVE_BOLT, new SaveMysqlBolt(),1).shuffleGrouping(TRANSLATE_BOLT);
		
		Config conf = new Config();
		conf.setDebug(true);
		
		if(args != null && args.length > 0)
		{
			conf.setNumWorkers(4);
			
			StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());
		}
		else
		{
			conf.setMaxTaskParallelism(3);
			
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());
	        
            Thread.sleep(500000);

            cluster.shutdown();
		}
	}

}
