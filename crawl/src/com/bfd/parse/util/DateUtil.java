package com.bfd.parse.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 日期处理工具类
 */

public class DateUtil {
    //~ Static fields/initializers =============================================


    private static Log log = LogFactory.getLog(DateUtil.class);
    private static String defaultDatePattern = null;
    private static String timePattern = "HH:mm";
    public static final String TS_FORMAT = DateUtil.getDatePattern() + " HH:mm:ss.S";
	private static Calendar cale = Calendar.getInstance();


    //~ Methods ================================================================

	public DateUtil(){
	}

	/**
	 * 
	 * @param 将几天前、几小时前、几分钟前格式化成标准时间处理格式
	 * @return
	 */
	public static String convertTime(String time)
	{
		long nowTimeL = new Date().getTime();
		Pattern iPattern = Pattern.compile("(\\d+)\\s*(年|个月|天|小时|分钟|秒)前");
		Matcher iM = iPattern.matcher(time.trim());
		//不带年份的时间正则
		Pattern p = Pattern.compile("(?:(\\d{2,4}).)?(\\d+).(\\d+).\\s*(\\d+):(\\d+)(?:\\:(\\d+))?");
		Matcher mch = p.matcher(time);
		long day = 86400000;
		long hour = 3600000;
		long min = 60000;
		long second = 1000;
	    if(time.contains("前天")) {
			nowTimeL = (nowTimeL - 2*day)/1000;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	        String tempDate = sdf.format(new Date(Long.valueOf(nowTimeL + "000")));
	        time = time.replace("前天", "").trim();
	        String finalDate = tempDate + " " + time;
	        return finalDate;
		}
	    else if(time.contains("昨天")) {
			nowTimeL = (nowTimeL - day)/1000;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	        String tempDate = sdf.format(new Date(Long.valueOf(nowTimeL + "000")));
	        time = time.replace("昨天","").trim();
	        String finalDate = tempDate + " " + time;
	        return finalDate;
		}
	    else if(time.contains("今天")) {
			nowTimeL = nowTimeL/1000;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	        String tempDate = sdf.format(new Date(Long.valueOf(nowTimeL + "000")));
	        time = time.replace("今天","").trim();
	        String finalDate = tempDate + " " + time;
	        return finalDate;
		}
		else if(time.contains("半天前"))
		{
			nowTimeL = (nowTimeL - 43200000)/1000;
		}
		else if(time.contains("半小时前"))
		{
			nowTimeL = (nowTimeL - 1800000)/1000;
		}
		else if(iM.find())
		{
			int i = Integer.parseInt(iM.group(1));
			if(time.contains("年前")){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR,-i);
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd"); 
				return matter.format(calendar.getTime());
			} 
			else if(time.contains("个月前")){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH,-i);
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd"); 
				return matter.format(calendar.getTime());
			}
			else if(time.contains("天前"))
			{
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE,-i);
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd"); 
				return matter.format(calendar.getTime());
			}
			else if(time.contains("小时前"))
			{
				nowTimeL = (nowTimeL - i*hour)/1000;
			}
		    else if(time.contains("分钟前"))
			{
				nowTimeL = (nowTimeL - i*min)/1000;
			}
		    else if(time.contains("秒前"))
			{
				nowTimeL = (nowTimeL - i*second)/1000;
			}
		}else if(mch.find()){
			nowTimeL = convertMonthTime(mch);
		}
		else {
			nowTimeL = nowTimeL/1000;
		}
		return normalTime(Long.toString(nowTimeL));
		
	}
	/**
	 * @function:将时间戳转换为标准时间格式，单位是秒
	 * @param time
	 * @return
	 */
	public static String normalTime(String time)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        return sdf.format(new Date(Long.valueOf(time+"000")));
	}

	/**
	 * 对于可能没有年份的时间字符串标准化为标准时间
	 * @param time
	 * @return
	 * 
	 * 2013年11月01号  11:11
	 */
	private static long convertMonthTime(Matcher mch){
		Calendar c = Calendar.getInstance();
		//c.setTimeInMillis(nowTimeL);
		if(mch.group(1) != null){
			String year = mch.group(1);
			if(year.length() == 2){
				year = "20" + year;
			}
			c.set(Calendar.YEAR, Integer.valueOf(year));
		}
		c.set(Calendar.MONTH, Integer.valueOf(mch.group(2)) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mch.group(3)));
		c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mch.group(4)));
		c.set(Calendar.MINUTE, Integer.valueOf(mch.group(5)));
		if(mch.group(6) != null){				
			c.set(Calendar.SECOND, Integer.valueOf(mch.group(6)));
		}
		return c.getTimeInMillis()/1000;
	}
	/**
	 * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm:ss的日期字符串形式返回
	 */
	public static String getDateTime(){
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return datetime.format(Calendar.getInstance().getTime());
		} catch(Exception e){
			log.debug("DateUtil.getDateTime():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm:ss的日期字符串形式返回
	 */
	public static String getDateTime(long date){
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return datetime.format(new Date(date));
		} catch(Exception e){
			log.debug("DateUtil.getDateTime():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 获得服务器当前日期，以格式为：yyyy-MM-dd的日期字符串形式返回
	 */
	public static String getDate(){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			return date.format(Calendar.getInstance().getTime());
		} catch(Exception e){
			log.debug("DateUtil.getDate():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 获得服务器当前时间，以格式为：HH:mm:ss的日期字符串形式返回
	 */
	public static String getTime(){
		String temp = "";
		try{
			 SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			temp += time.format(cale.getTime());
			return temp;
		} catch(Exception e){
			log.debug("DateUtil.getTime():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 获得服务器当前时间，以格式为：HH:mm:ss的日期字符串形式返回
	 */
	public static int getHour(){
		int temp = 0;
		try{
			temp = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			return temp;
		} catch(Exception e){
			log.debug("DateUtil.getTime():" + e.getMessage());
			return 0;
		}
	}
	
	/**
	 * 返回日期加X天后的日期
	 */
	@SuppressWarnings("static-access")
	public static int getHour(String fromdate){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			GregorianCalendar gCal = new GregorianCalendar();
			Date datetime = date.parse(fromdate) ;
			gCal.setTime(datetime) ;
			return gCal.get(gCal.HOUR_OF_DAY);
		} catch(Exception e){
			log.debug("DateUtil.addDay():" + e.toString());
			return 0;
		}
	}


	public static int getMinute(){
		int temp = 0;
		try{
			temp = Calendar.getInstance().get(Calendar.MINUTE);
			return temp;
		} catch(Exception e){
			log.debug("DateUtil.getTime():" + e.getMessage());
			return 0;
		}
	}


	/**
	 * 统计时开始日期的默认值,
	 * 今年的开始时间
	 */
	public static String getStartDate(){
		try{
			return getYear() + "-01-01";
		} catch(Exception e){
			log.debug("DateUtil.getStartDate():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 统计时结束日期的默认值
	 */
	public static String getEndDate(){
		try{
			return getDate();
		} catch(Exception e){
			log.debug("DateUtil.getEndDate():" + e.getMessage());
			return "";
		}
	}


	/**
	 * 获得服务器当前日期的年份
	 */
	public static String getYear(){
		try{
			//返回的int型，需要字符串转换
			return String.valueOf(cale.get(Calendar.YEAR));
		} catch(Exception e){
			log.debug("DateUtil.getYear():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 获得服务器当前日期的月份
	 */
	public static String getMonth(){
		try{
			//一个数字格式，非常好
			java.text.DecimalFormat df = new java.text.DecimalFormat();
			df.applyPattern("00");
			return df.format((cale.get(Calendar.MONTH) + 1));
			//return String.valueOf(cale.get(Calendar.MONTH) + 1);
		} catch(Exception e){
			log.debug("DateUtil.getMonth():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 获得服务器在当前月中天数
	 */
	public static String getDay(){
		try{
			return String.valueOf(cale.get(Calendar.DAY_OF_MONTH));
		} catch(Exception e){
			log.debug("DateUtil.getDay():" + e.getMessage());
			return "";
		}
	}

	/**
	 * 比较两个日期相差的天数,
	 * 第一个日期要比第二个日期要晚
	 */
	public static int getDays(String date1,String date2){
		int margin;
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			ParsePosition pos = new ParsePosition(0);
			ParsePosition pos1 = new ParsePosition(0);
			Date dt1 = date.parse(date1,pos);
			Date dt2 = date.parse(date2,pos1);
			long l = dt1.getTime() - dt2.getTime();
			margin = (int)(l / (24 * 60 * 60 * 1000));
			return margin;
		} catch(Exception e){
			log.debug("DateUtil.getDays():" + e.toString());
			return 0;
		}
	}

	/**
	 * 比较两个日期相差的 小时数,
	 * 第一个日期要比第二个日期要晚
	 */
	public static int getHours(String date1,String date2){
		int margin;
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			ParsePosition pos1 = new ParsePosition(0);
			Date dt1 = datetime.parse(date1,pos);
			Date dt2 = datetime.parse(date2,pos1);
			long l = dt1.getTime() - dt2.getTime();
			margin = (int)(l / ( 60 * 60 * 1000));
			return margin;
		} catch(Exception e){
			log.debug("DateUtil.getHours():" + e.toString());
			return 0;
		}
	}

	/**
	 * 比较两个日期相差的分钟数,
	 * 第一个日期要比第二个日期要晚
	 */
	public static int getMinutes(String date1,String date2){
		int margin;
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			ParsePosition pos1 = new ParsePosition(0);
			Date dt1 = datetime.parse(date1,pos);
			Date dt2 = datetime.parse(date2,pos1);
			long l = dt1.getTime() - dt2.getTime();
			margin = (int)(l / ( 60 * 1000));
			return margin;
		} catch(Exception e){
			log.debug("DateUtil.getMinutes():" + e.toString());
			return 0;
		}
	}

	/**
	 * 返回日期加X天后的日期
	 */
	@SuppressWarnings("static-access")
	public static int getMinutes(String fromdate){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			GregorianCalendar gCal = new GregorianCalendar();
			Date datetime = date.parse(fromdate) ;
			gCal.setTime(datetime) ;
			return gCal.get(gCal.MINUTE);
		} catch(Exception e){
			log.debug("DateUtil.addDay():" + e.toString());
			return 0;
		}
	}
	
	/**
	 * 比较两个日期相差的秒数,
	 * 第一个日期要比第二个日期要晚
	 */
	public static int getSeconds(String date1,String date2){
		int margin;
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = datetime.parse(date1);
			Date dt2 = datetime.parse(date2);
			long dateintvlong = dt1.getTime() - dt2.getTime();
			margin = (int)(dateintvlong /1000);
			return margin;
		} catch(Exception e){
			log.debug("DateUtil.getSeconds():" + e.toString());
			return 0;
		}
	}


	/**
	 * 比较两个日期相差的天数，格式不一样
	 * 第一个日期要比第二个日期要晚
	 */
	public static double getDoubledays(String date1,String date2){
		double margin;
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			ParsePosition pos1 = new ParsePosition(0);
			Date dt1 = datetime.parse(date1,pos);
			Date dt2 = datetime.parse(date2,pos1);
			long l = dt1.getTime() - dt2.getTime();
			margin = (l / (24 * 60 * 60 * 1000.00));
			return margin;
		} catch(Exception e){
			log.debug("DateUtil.getMargin():" + e.toString());
			return 0;
		}
	}


	/**
	 * 比较两个日期相差的月数
	 */
	public static int getMonthMargin(String date1,String date2){
		int margin;
		try{
			margin  = (Integer.parseInt(date2.substring(0,4)) - Integer.parseInt(date1.substring(0,4)))* 12;
			margin += (Integer.parseInt(date2.substring(4,7).replaceAll("-0","-")) - Integer.parseInt(date1.substring(4,7).replaceAll("-0","-")));
			return margin;
		} catch(Exception e){
			log.debug("DateUtil.getMargin():" + e.toString());
			return 0;
		}
	}

	/**
	 * 返回日期加X天后的日期
	 */
	public static String addDay(String fromdate,int i){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar gCal = new GregorianCalendar(Integer.parseInt(fromdate.substring(0,4)),Integer.parseInt(fromdate.substring(5,7))-1,Integer.parseInt(fromdate.substring(8,10)));
			gCal.add(GregorianCalendar.DATE,i);
			return date.format(gCal.getTime());
		} catch(Exception e){
			log.debug("DateUtil.addDay():" + e.toString());
			return getDate();
		}
	}
	
	/**
	 * 返回日期加X天后的日期
	 */
	public static String addDay(int i){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cale = Calendar.getInstance() ;
			cale.add(Calendar.DAY_OF_MONTH, i) ;
			return date.format(cale.getTime());
		} catch(Exception e){
			log.debug("DateUtil.addDay():" + e.toString());
			return getDate();
		}
	}

	/**
	 * 返回日期加X月后的日期
	 */
	public static String addMonth(String fromdate,int i){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar gCal = new GregorianCalendar(Integer.parseInt(fromdate.substring(0,4)),Integer.parseInt(fromdate.substring(5,7))-1,Integer.parseInt(fromdate.substring(8,10)));
			gCal.add(GregorianCalendar.MONTH,i);
			return date.format(gCal.getTime());
		} catch(Exception e){
			log.debug("DateUtil.addMonth():" + e.toString());
			return getDate();
		}
	}

	/**
	 * 返回日期加X年后的日期
	 */
	public static String addYear(String fromdate,int i){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar gCal = new GregorianCalendar(Integer.parseInt(fromdate.substring(0,4)),Integer.parseInt(fromdate.substring(5,7))-1,Integer.parseInt(fromdate.substring(8,10)));
			gCal.add(GregorianCalendar.YEAR,i);
			return date.format(gCal.getTime());
		} catch(Exception e){
			log.debug("DateUtil.addYear():" + e.toString());
			return "";
		}
	}


	/**
	 * 返回某年某月中的最大天
	 */
	public static int getMaxDay(String year,String month){
		int day = 0;
		try{
			int iyear = Integer.parseInt(year);
			int imonth = Integer.parseInt(month);
			if(imonth == 1 || imonth == 3 || imonth == 5 || imonth == 7 || imonth == 8 || imonth == 10 || imonth == 12){
				day = 31;
			} else if(imonth == 4 || imonth == 6 || imonth == 9 || imonth == 11){
				day = 30;
			} else if((0 == (iyear % 4)) && (0 != (iyear % 100)) || (0 == (iyear % 400))){
				day = 29;
			} else{
				day = 28;
			}
			return day;
		} catch(Exception e){
			log.debug("DateUtil.getMonthDay():" + e.toString());
			return 1;
		}
	}



	/**
	 * 格式化日期
	 */
	@SuppressWarnings("static-access")
	public String rollDate(String orgDate,int Type,int Span){
		try{
			String temp = "";
			int iyear,imonth,iday;
			int iPos = 0;
			char seperater = '-';
			if(orgDate == null || orgDate.length() < 6){
				return "";
			}

			iPos = orgDate.indexOf(seperater);
			if(iPos > 0){
				iyear = Integer.parseInt(orgDate.substring(0,iPos));
				temp = orgDate.substring(iPos + 1);
			} else{
				iyear = Integer.parseInt(orgDate.substring(0,4));
				temp = orgDate.substring(4);
			}

			iPos = temp.indexOf(seperater);
			if(iPos > 0){
				imonth = Integer.parseInt(temp.substring(0,iPos));
				temp = temp.substring(iPos + 1);
			} else{
				imonth = Integer.parseInt(temp.substring(0,2));
				temp = temp.substring(2);
			}

			imonth--;
			if(imonth < 0 || imonth > 11){
				imonth = 0;
			}

			iday = Integer.parseInt(temp);
			if(iday < 1 || iday > 31)
				iday = 1;

			Calendar orgcale = Calendar.getInstance();
			orgcale.set(iyear,imonth,iday);
			temp = this.rollDate(orgcale,Type,Span);
			return temp;
		}catch(Exception e){
			return "";
		}
	}

	public static String rollDate(Calendar cal,int Type,int Span){
		try{
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			String temp = "";
			Calendar rolcale;
			rolcale = cal;
			rolcale.add(Type,Span);
			temp = date.format(rolcale.getTime());
			return temp;
		}catch(Exception e){
			return "";
		}
	}

    /**
     *
     * 返回默认的日期格式
     *
     */
    public static synchronized String getDatePattern() {
        defaultDatePattern = "yyyy-MM-dd";
        return defaultDatePattern;
    }

    /**
     * 将指定日期按默认格式进行格式代化成字符串后输出如：yyyy-MM-dd
     */
    public static final String getDate(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }



    /**
     * 取得给定日期的时间字符串，格式为当前默认时间格式
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(timePattern, theTime);
    }

    /**
     * 取得当前时间的Calendar日历对象
     */
    public Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern());
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));
        return cal;
    }

    /**
     * 将日期类转换成指定格式的字符串形式
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }

    /**
     * 将指定的日期转换成默认格式的字符串形式
     */
    public static final String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }


    /**
     * 将日期字符串按指定格式转换成日期类型
     * @param aMask 指定的日期格式，如:yyyy-MM-dd
     * @param strDate 待转换的日期字符串
     */

    public static final Date convertStringToDate(String aMask, String strDate)
      throws ParseException {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '"
                      + aMask + "'");
        }
        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            log.error("ParseException: " + pe);
            throw pe;
        }
        return (date);
    }

    /**
     * 将日期字符串按默认格式转换成日期类型
     */
    public static Date convertStringToDate(String strDate)
      throws ParseException {
        Date aDate = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("converting date with pattern: " + getDatePattern());
            }
            aDate = convertStringToDate(getDatePattern(), strDate);
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate
                      + "' to a date, throwing exception");
            throw new ParseException(pe.getMessage(),
                                     pe.getErrorOffset());

        }

        return aDate;
    }

    /**
     * 返回一个JAVA简单类型的日期字符串
     */
    public static String getSimpleDateFormat(){
    	SimpleDateFormat formatter=new SimpleDateFormat();
		String NDateTime=formatter.format(new Date());
		return NDateTime;
    }

    /**
     * 将两个字符串格式的日期进行比较
     * @param last 要比较的第一个日期字符串
     * @param now	要比较的第二个日期格式字符串
     * @return true(last 在now 日期之前),false(last 在now 日期之后)
     */
    public static boolean compareTo(String last, String now) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date temp1 = formatter.parse(last);
			Date temp2 = formatter.parse(now);
			if (temp1.after(temp2))
				return false;
			else if (temp1.before(temp2))
				return true;
		} catch (ParseException e) {
			log.debug(e.getMessage());
		}
		return false;
	}
    
    
    /**
     * 将两个字符串格式的日期进行比较
     * @param last 要比较的第一个日期字符串
     * @param now	要比较的第二个日期格式字符串
     * @return true(last 在now 日期之前),false(last 在now 日期之后)
     */
    public static boolean compareToForBBS(String last, String now) {
		try {
			if(last.equals(now))
				return true;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd");
			Date temp1 = formatter.parse(last);
			Date temp2 = formatter.parse(now);
			if (temp1.after(temp2))
				return false;
			else if (temp1.before(temp2))
				return true;
		} catch (ParseException e) {
			log.debug(e.getMessage());
		}
		return false;
	}





    /**
     *  为查询日期添加最小时间
     *  @param 目标类型Date
     *  @param 转换参数Date
     *  @return
     */
    @SuppressWarnings("deprecation")
	public static Date addStartTime(Date param) {
    	Date date = param;
        try{
        	date.setHours(0);
        	date.setMinutes(0);
        	date.setSeconds(0);
            return date;
        }catch(Exception ex){
        	return date;
        }
    }



    /**
     * 为查询日期添加最大时间
     *  @param 目标类型Date
     *  @param 转换参数Date
     *  @return
     */
    @SuppressWarnings("deprecation")
	public static Date addEndTime(Date param) {
    	Date date = param;
        try{
        	date.setHours(23);
        	date.setMinutes(59);
        	date.setSeconds(0);
            return date;
        }catch(Exception ex){
            return date;
        }
    }



    /**
     * 返回系统现在年份中指定月份的天数
     * @param 月份month
     * @return 指定月的总天数
     */
    @SuppressWarnings("deprecation")
	public static String getMonthLastDay(int month)
	{
		Date date=new Date();
		int[][] day={{0,30,28,31,30,31,30,31,31,30,31,30,31},
						{0,31,29,31,30,31,30,31,31,30,31,30,31}};
		int year=date.getYear()+1900;
		if(year%4==0 && year%100!=0 || year%400==0)
		{
			return day[1][month]+"";
		}
		else
		{
			return day[0][month]+"";
		}
	}

    /**
     * 返回指定年份中指定月份的天数
     * @param 年份year
     * @param 月份month
     * @return 指定月的总天数
     */
    public static String getMonthLastDay(int year,int month)
	{
		int[][] day={{0,30,28,31,30,31,30,31,31,30,31,30,31},
						{0,31,29,31,30,31,30,31,31,30,31,30,31}};
		if(year%4==0 && year%100!=0 || year%400==0)
		{
			return day[1][month]+"";
		}
		else
		{
			return day[0][month]+"";
		}
	}

    /**
     * 取得当前时间的日戳
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getTimestamp(){
    	Date date=new Date();
    	String timestamp=""+(date.getYear()+1900)+date.getMonth()+date.getDate()+date.getMinutes()+date.getSeconds()+date.getTime();
    	return timestamp;
    }
    /**
     * 取得指定时间的日戳
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getTimestamp(Date date){
    	String timestamp=""+(date.getYear()+1900)+date.getMonth()+date.getDate()+date.getMinutes()+date.getSeconds()+date.getTime();
    	return timestamp;
    }
    
	
	public static Date getDate(String time) {
		Date date = new Date();
		try {
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = datetime.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

    
    public static long getTimeMillis(String datetime){
		long timemillis = 0 ;
		Calendar cal = Calendar.getInstance();
		Date date = getDate(datetime) ;
		cal.setTime(date) ;
		timemillis = cal.getTimeInMillis() ;
		return timemillis ;
	}
	
	
	public static long getsmallSec(String datetime1,String datetime2){
		long time1 = 0 ;
		long time2 = 0 ;
		long time = 0 ;
		if(datetime1!=null){
			time1 = getTimeMillis(datetime1) ;
		}
		if(datetime2!=null){
			time2 = getTimeMillis(datetime2) ;
		}
		if(time1==0){
			time = time2 ;
		}else if(time2==0){
			time = time1 ;
		}else if(time1>time2){
			time = time2 ;
		}else{
			time = time1 ;
		}
		time = time/1000 ;
		return time ;
	}
    
	/** 
	 * @Description: TODO
	 * @param calendarField 修改的字段
	 * @param calc add/sub
	 * @param n
	 * @return 
	 *   
	 * @return String     
	 * @throws 
	*/
	public static String getDatetimeNfieldgap(int calendarField,String calc,int n){
		try{
			SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance() ;
			int value = cal.get(calendarField) ;
			if(calc.equals("add")){
				cal.set(calendarField, value+n) ;
			}else if(calc.equals("sub")){
				cal.set(calendarField, value-n);
			}
			return datetime.format(cal.getTime());
		} catch(Exception e){
			log.debug("DateUtil.getDay():" + e.getMessage());
			return "" ;
		}
	}
	
	@SuppressWarnings("static-access")
	public static void sleep(int seconds){
		if(seconds<1)
			return;
		try {
			Thread.currentThread().sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	public static void sleepLong(long millisecond){
		if(millisecond<1)
			return;
		try {
			Thread.currentThread().sleep(millisecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static long getTimestamp(String dateTime){
		SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date date;
		try {
			date = format.parse(dateTime);
//			System.out.print("Format To times:"+date.getTime());
			return date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public static void main(String[] args) {
	}
}
