package com.baihe.storm.order.bolt;

import java.util.List;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TranslateBolt extends BaseBasicBolt{

	public void execute(Tuple tuple, BasicOutputCollector collector)
	{
		List orderList = tuple.getValues();
		
		String id = (String)orderList.get(0);
		String userid = (String)orderList.get(1);
		String totalprice = (String)orderList.get(2);
		String youhui = (String)orderList.get(3);
		String sendpay = (String)orderList.get(4);
		String createDate = (String)orderList.get(5);
		
		if("0".equals(sendpay))
		{
			sendpay = "1201";
		}
		else if("1".equals(sendpay))
		{
			sendpay = "1202";
		}
		else if("2".equals(sendpay))
		{
			sendpay = "1203";
		}
		
		System.out.println("-------list---"+orderList.toString()+"---sendpay---"+sendpay);
		
		collector.emit(new Values(id,userid,totalprice,youhui,sendpay,createDate));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		declarer.declare(new Fields("id","userid","totalprice","youhui","sendpay","createDate"));
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		super.prepare(stormConf, context);
		System.out.println("transbolt begin!");
	}

	
}
