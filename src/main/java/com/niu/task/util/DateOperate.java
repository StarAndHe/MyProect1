package com.niu.task.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateOperate {


    public static void main(String[] args) {
        Date currentDate=new Date();
        String week  =  getWeek(currentDate);
        System.out.println("今天是"+week);

    }

    public static Date dateToWeek( Date mdate){

        Date fdate = new Date();
        Long fTime = mdate.getTime() - 7 * 24 * 3600000; //获取时间戳
        fdate.setTime(fTime);

        return fdate;

    }

    public static Date dateToOneBefore( Date mdate){

        Date fdate = new Date();
        Long fTime = mdate.getTime() - 1 * 24 * 3600000; //获取时间戳
        fdate.setTime(fTime);

        return fdate;

    }

    public static Date dateToTodayZero( Date mdate){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mdate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;

    }

    public static Date dateToOneAfter( Date mdate){

        Date fdate = new Date();
        Long fTime = mdate.getTime() + 1 * 24 * 3600000; //获取时间戳
        fdate.setTime(fTime);

        return fdate;

    }


    //根据日期取得星期几,星期日
    public static String getWeek(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(date);
        return week;
    }






    /**
     * 获取当前时间小时整点时间
     *
     * @param
     * @return
     */
    public static Date getCurrentHourTime(Date currentDate) {
        return getHourTime(currentDate, 0, "=");
    }

    /**
     * 获取指定时间上n个小时整点时间
     *
     * @param date
     * @return
     */
    public static Date getLastHourTime(Date date, int n) {
        return getHourTime(date, n, "-");
    }

    /**
     * 获取指定时间下n个小时整点时间
     *
     * @param date
     * @return
     */
    public static Date getNextHourTime(Date date, int n) {
        return getHourTime(date, n, "+");
    }

    /**
     * 获取指定时间n个小时整点时间
     *
     * @param date
     * @return
     */
    public static Date getHourTime(Date date, int n, String direction) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        switch (direction) {
            case "+":
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) + n);
                break;
            case "-":
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - n);
                break;
            case "=":
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY));
                break;
            default:
                ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY));
        }

        date = ca.getTime();
        return date;
    }




    public static BigInteger diff(Date start, Date end){

        //单位是毫秒
        long diff = end.getTime() - start.getTime();
        //将结果转化成秒
        long second = diff/1000;
        //System.out.println("时间差 ：" +second);
        return BigInteger.valueOf(second);
    }




}
