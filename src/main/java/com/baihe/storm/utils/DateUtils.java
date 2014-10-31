package com.baihe.storm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static final String C_DATE_PATTON_DEFAULT = "yyyy-MM-dd";
	
	public static boolean isMatchedDate(String createDate,String startDate)
	{
		boolean flag = false;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat(C_DATE_PATTON_DEFAULT);
			Date cdate = format.parse(createDate);
			Date sdate = format.parse(startDate);
			
			if(cdate.getTime()>=sdate.getTime())
			{
				flag =  true;
			}
			else
			{
				flag = false;
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
}
