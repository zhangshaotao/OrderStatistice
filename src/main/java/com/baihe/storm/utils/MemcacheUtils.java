package com.baihe.storm.utils;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemcacheUtils {
	
	private static MemcachedClient memClient = null;
	
	public static MemcachedClient getMemcacheClient() throws IOException
	{
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(Constant.MEMCACHE_HOST_PORT));
		memClient = builder.build();
		
		System.out.println(memClient);
		return memClient;
	}
	
	public static void main(String[] args) {
		//test();
		flushMem();
	}
	
	public static void test()
	{
		MemcachedClient memcachedClient = null;
		try {
			memcachedClient = MemcacheUtils.getMemcacheClient();
		
			System.out.println(memcachedClient);
		
			memcachedClient.set("hello", 0, "Hello,xmemcached");
		    String value = memcachedClient.get("hello");
		    System.out.println("hello=" + value);
		
		    //尝试添加 看看返回什么
		    boolean  flag = memcachedClient.add("hello", 0, "Hello,xmemcached");
		    System.out.println("flag==" + flag);
		
		    memcachedClient.delete("hello");
		    value = memcachedClient.get("121709_300");
		    System.out.println("value=" + value);
		
		    HashMap map = new HashMap();
		    map.put("key" ,"123");
		    memcachedClient.set("map",0,map) ;
		
		    System.out.println(memcachedClient.get("map").getClass());
        
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
	
	public static void flushMem()
	{
		try
		{
			MemcacheUtils.getMemcacheClient().flushAll();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
}
