package com.baihe.storm.utils;

public class Constant {

	public static String MEMCACHE_HOST_PORT="storm01:11211";
	public static String MEMCACHE_HOST="storm01";
	public static int MEMCACHE_PORT=11211;
	public static int[] MEMCACHE_WEIGHT=new int[]{1};
	
	public static String MYSQL_DRIVER="com.mysql.jdbc.Driver";
	public static String MYSQL_URL ="jdbc:mysql://192.168.206.1:3306/test?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false";
	public static String MYSQL_USERNAME="root";
	public static String MYSQL_PASSWORD="root";
	
	
	public static String LOCKS_ORDER = "/locks/order";
	public static String ZK_HOST_PORT = "storm01:2181";
	
	
}
