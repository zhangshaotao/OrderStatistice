package com.baihe.storm.order.bolt;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.baihe.storm.utils.DateUtils;

public class CheckOrderBolt extends BaseBasicBolt
{

	public void execute(Tuple tuple, BasicOutputCollector collector)
	{
		String orderString = tuple.getString(0);
		
		if(StringUtils.isNotEmpty(orderString) && orderString.length() > 0)
		{
			String[] orderStringArr = orderString.split("\t");
			
			String id = orderStringArr[0];
			String userid = orderStringArr[1];
			String totalprice = orderStringArr[2];
			String youhui = orderStringArr[3];
			String sendpay = orderStringArr[4];
			String createDate = orderStringArr[5];
			
			if(DateUtils.isMatchedDate(createDate, "2014-10-10"))
			{
				collector.emit(new Values(id,userid,totalprice,youhui,sendpay,createDate));
			}
			
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		declarer.declare(new Fields("id","userid","totalprice","youhui","sendpay","createdate"));
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// TODO Auto-generated method stub
		super.prepare(stormConf, context);
	}

}
