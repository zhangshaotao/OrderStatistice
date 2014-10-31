package com.baihe.storm.order.bolt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.lang.StringUtils;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import com.baihe.storm.utils.Constant;
import com.baihe.storm.utils.JDBCUtils;
import com.baihe.storm.utils.MemcacheUtils;

public class SaveMysqlBolt extends BaseBasicBolt{
	
	private static final long serialVersionUID = -9135266042594721255L;
	private static MemcachedClient memClient = null;

	private static List<String> memKeyList = null;
	private static Map<String,String> memMap = null;
	
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	
	public void execute(Tuple tuple,BasicOutputCollector collector) 
	{
		/*List orderList = tuple.getValues();
		
		System.out.println("---------save----------"+orderList.toString()+"--------save----------");
		
		String id = (String)orderList.get(0);
		String userid = (String)orderList.get(1);
		String totalprice = (String)orderList.get(2);
		String youhui = (String)orderList.get(3);
		String sendpay = (String)orderList.get(4);
		String createDate = (String)orderList.get(5);*/
		
		//"id","userid","totalprice","youhui","sendpay","createDate"
		
		String id = (String)tuple.getStringByField("id");
		String userid = (String)tuple.getStringByField("userid");
		String totalprice = (String)tuple.getStringByField("totalprice");
		String youhui = (String)tuple.getStringByField("youhui");
		String sendpay = (String)tuple.getStringByField("sendpay");
		//String createDate = (String)tuple.getStringByField("createDate");
		
		System.out.println("----"+id+"----"+userid+"----"+totalprice+"----"+youhui+"----"+sendpay+"----");
		//----9044129----4043----324----49----1----
		try 
		{
			saveCounterMember(userid,sendpay,totalprice,youhui);
		} 
		catch (TimeoutException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void prepare(Map map, TopologyContext topologyContext)
	{
		System.out.println("savebolt begin!");
		try
		{
			memClient = MemcacheUtils.getMemcacheClient();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		memKeyList = new ArrayList<String>();
		memMap = new HashMap<String,String>();
		
		Timer timer = new Timer();
		timer.schedule(new SaveMysqlBolt.cacheTimer(), new Date(), 5000);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		
	}

	public void saveCounterMember(String userid,String sendpay,String totalprice,String youhui) 
		throws TimeoutException, InterruptedException, MemcachedException, IOException
	{
		String memKey = userid+"_"+sendpay;
		
		if(memClient == null)
		{
			memClient = MemcacheUtils.getMemcacheClient();
		}
		String memValue = memClient.get(memKey);
		
		boolean isHasUser = false;
		
		if(memValue != null)
		{
			isHasUser = true;
		}
		else
		{
			memClient.add(memKey, 0, memKey);
		}
		System.out.println("----"+userid+"----"+totalprice+"----"+youhui+"----"+sendpay+"----");
		
		saveMap(sendpay,totalprice,youhui,isHasUser);
	}

	private void saveMap(String sendpay, String totalprice, String youhui,boolean isHasUser) 
		throws TimeoutException, InterruptedException, MemcachedException, IOException
	{
		System.out.println("----"+totalprice+"----"+youhui+"----"+sendpay+"----");
		
		if(memMap == null){
			memMap = new HashMap<String, String>();
		}
		if(memClient == null)
		{
			memClient = MemcacheUtils.getMemcacheClient();
		}
		
		String value = memMap.get(sendpay);
		//String value = memberMap.get(sendpay);
		if(value != null)
		{
			String[] vals = value.split(",");
			int id_num = Integer.valueOf(vals[0]) + 1;
			double tp = Double.valueOf(vals[1]) + Double.valueOf(totalprice);
			double etp = Double.valueOf(vals[2]) + (Double.valueOf(totalprice)-Double.valueOf(youhui));
			int counter_user = Integer.valueOf(vals[3]) + (isHasUser ? 0 : 1);
			value =  id_num + "," + tp + "," + etp + "," + counter_user;
		}
		else
		{
			value =  1 + "," + totalprice + "," + (Double.valueOf(totalprice)-Double.valueOf(youhui)) + "," + (isHasUser ? 0 : 1);
		}
		
		System.out.println("sendpay = "+sendpay +"  value = "+value);
		memMap.put(sendpay,value);
		//memClient.add(sendpay, 0, value);
		
		/*try 
		{
			saveMysql();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}*/
	}
	
	public void saveMysql(Map<String,String> map) throws SQLException, ClassNotFoundException, TimeoutException, InterruptedException, MemcachedException
	{
		//Iterator<String> memKeyIter = memKeyList.iterator();
		for(Map.Entry<String, String> entry:map.entrySet())
		{
			String sendpay = entry.getKey();
			String value = entry.getValue();
			
			/*String sendpay = memKeyIter.next();
			String value = memClient.get(sendpay);*/
			
			String[] values = value.split(",");
			
			int orderNums = Integer.parseInt(values[0]);
			double pTotalPrice = Double.parseDouble(values[1]);
			double yTotalPrice = Double.parseDouble(values[2]);
			int orderUserNum = Integer.parseInt(values[3]);
			
			String querySql = "select ID,OrderNums,PTotalPrice,YTotalPrice,OrderUserNum from TotalOrder where SendPay='"+sendpay+"'";
			
			rs = stmt.executeQuery(querySql);
			
			
			if(rs.next())
			{
				
				
				/*
				 * OrderTotal orderTotal = null;
				 * String id = rs.getString("ID");
				int OrderNums = rs.getInt("OrderNums");
				double PTotalPrice = rs.getDouble("PTotalPrice");
				double YTotalPrice = rs.getDouble("YTotalPrice");
				int orderUserNums = rs.getInt("orderUserNums");
				
				orderTotal = new OrderTotal(id, OrderNums, PTotalPrice, YTotalPrice, orderUserNums, sendpay);
				*/
				
				String id = rs.getString("ID");
				orderNums = orderNums + rs.getInt("OrderNums");
				pTotalPrice = pTotalPrice + rs.getDouble("PTotalPrice");
				yTotalPrice = yTotalPrice + rs.getDouble("YTotalPrice");
				orderUserNum = orderUserNum + rs.getInt("OrderUserNum");
				
				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("update TotalOrder set OrderNums=")
				.append(orderNums)
				.append(",PTotalPrice=")
				.append(pTotalPrice)
				.append(",YTotalPrice=")
				.append(yTotalPrice)
				.append(",OrderUserNum=")
				.append(orderUserNum)
				.append(" where id=")
				.append(id);
				
				stmt.executeUpdate(sqlBuffer.toString());
				conn.commit();
			
			}
			else
			{
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("insert into TotalOrder(OrderNums,PTotalPrice,YTotalPrice,OrderUserNum,sendpay) values(")
				.append(orderNums)
				.append(",")
				.append(pTotalPrice)
				.append(",")
				.append(yTotalPrice)
				.append(",")
				.append(orderUserNum)
				.append(",'")
				.append(sendpay)
				.append("')");
				
				stmt.executeUpdate(sqlBuffer.toString());
				conn.commit();
			}
		}
	}
	
	class cacheTimer extends TimerTask
	{

		@Override
		public void run()
		{
			//isOpen = false;
			Map<String, String> tmpMap = new HashMap<String, String>();
			tmpMap.putAll(memMap);
			
			System.out.println("----------tmpmap------"+tmpMap.toString());
			
			memMap = new HashMap<String, String>();
			
			//putDataMap();
			//isOpen = true;
			try 
			{
				conn = JDBCUtils.getConnection();
				stmt = conn.createStatement();
				
				saveMysql(tmpMap);
				
				//memClient.flushAll();
				
				if(rs != null)
					rs.close();
				if(stmt != null)
					stmt.close();
				if(conn != null)
					conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} 
			catch (TimeoutException e)
			{
				e.printStackTrace();
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			} 
			catch (MemcachedException e)
			{
				e.printStackTrace();
			}
			
		}
		
	}
}
