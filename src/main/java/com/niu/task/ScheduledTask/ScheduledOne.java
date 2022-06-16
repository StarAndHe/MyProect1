package com.niu.task.ScheduledTask;

import com.niu.task.util.Calculator;
import com.niu.task.Entity.DayTask;
import com.niu.task.Service.DayTaskService;
import com.niu.task.util.DateOperate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@EnableAsync
@Component
public class ScheduledOne {
    public static List<String> list;
    public static List<List<DayTask>> listTotal = new LinkedList<>();
    Map<String,Integer> map = new HashMap<>();  //按类型保存最终结果
    List<DayTask> weekList = new ArrayList<>(); //保存上周数据
    Integer count = 0;                          //标记map中的元素数量

    @Value("${spring.mail.username}")
    private String from;

    @Value("#{'${test.list}'.split(',')}")
    public  void setList(List<String> list) {
        ScheduledOne.list = list;
    }

    @Autowired DayTaskService dayTaskService;
    @Autowired Calculator calculator;
    @Resource
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 * * ? * MON-SUN")
    public void configureTasks() throws Exception {
        //System.out.println("当前主线程的线程名为 ："+Thread.currentThread().getName());
        Date currentDate = new Date();          //当前时间，从第八日0点开始
        List<DayTask> tList;                    //保存查询到的该类型一个小时的所有记录
        List<Date> lastWeek = new ArrayList<>();//保存要查询哪七天
        DayTask tTask;                          //第一个查询的结果，保存每天查询到的该类型的总任务数
        Date qDate = DateOperate.dateToWeek(currentDate);//得到一周前的那一天，应该是上周一
        qDate = DateOperate.dateToTodayZero(qDate);      //得到零点零分
        lastWeek.add(qDate);
        listTotal.add(new ArrayList<>());
        for(int i=0; i<6; i++){                 //保存要查询哪七天以及初始化listTotal
            qDate = DateOperate.dateToOneAfter(qDate);
            lastWeek.add(qDate);
            listTotal.add(new ArrayList<>());
        }

        for(int i=0; i<lastWeek.size(); i++){                       //遍历要计算的天
            List<Date> dayHours = new ArrayList<>();
            Date firstHour = DateOperate.getCurrentHourTime(lastWeek.get(i));
            dayHours.add(firstHour);
            List<DayTask> writeList = listTotal.get(i);
            for(int k=0; k<list.size(); k++){
                //System.out.println("求当天总任务数时的日期是 ："+lastWeek.get(i));
                tTask = dayTaskService.findDayTask(lastWeek.get(i),list.get(k));//求得这一天一个类型的总任务数等数据
                tTask.setTaskType(list.get(k));                                 //写入类型
                writeList.add(tTask);                                           //存入这一天对应的List中，随后汇总即可
            }

            // 注意！为了测试时不出现无谓错误，现在17点，所以我只统计5个小时内的数据，确保都在今天，不会和findDaytask()不一致
            for(int j=0; j<23; j++){
                firstHour = DateOperate.getNextHourTime(firstHour,1);
                dayHours.add(firstHour);
            }


            for (int type=0; type<list.size(); type++){
                List<Future<Integer>> daySlowAsc = new ArrayList<>();
                for(int j=0; j<dayHours.size(); j++){
                    tList = dayTaskService.findSlowTaskNumber(dayHours.get(j), list.get(type));//求出该类型一个小时的记录
                    daySlowAsc.add(calculator.calculateSlow(tList));                           //利用自定义线程池异步计算这一个小时的记录
                }

                //取出该类型这一天24小时的排队任务数
                int typeSlowDay = 0;
                for (Future<Integer> f :daySlowAsc){
                    try{
                        while (true){
                            if(f.isDone() && !f.isCancelled()){
                                typeSlowDay += f.get(5, TimeUnit.DAYS.SECONDS);
                                break;
                            }else{
                                Thread.sleep(100);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                //将该类型当天24小时的slow任务数加到储存当天数据的 writeList的对应类型记录中
                for(int j=0; j<writeList.size(); j++){
                    if(list.get(type).equals(writeList.get(j).getTaskType())){
                        writeList.get(j).setSlowTaskNumber(writeList.get(j).getSlowTaskNumber()+typeSlowDay);
                    }
                }
            }

            //System.out.println("一天计算完成！！！");
        }//lastWeek遍历完成


        //汇总上周数据
        for(int k=0; k<listTotal.size(); k++){

            List<DayTask> dayList = listTotal.get(k);       //取出每天的数据
            for(int i=0; i<dayList.size();i++){             //处理这一天的数据
                if( !map.containsKey(dayList.get(i).getTaskType())){
                    map.put(dayList.get(i).getTaskType(),count++);
                    DayTask temDayTask = new DayTask();
                    temDayTask.setTaskType(dayList.get(i).getTaskType());
                    temDayTask.setDayTaskTotal(dayList.get(i).getDayTaskTotal());
                    temDayTask.setDayTaskAvgTime(dayList.get(i).getDayTaskAvgTime());
                    temDayTask.setSlowTaskNumber(dayList.get(i).getSlowTaskNumber());
                    weekList.add(temDayTask);
                }else{
                    DayTask temDayTask = weekList.get(map.get(dayList.get(i).getTaskType()));
                    Integer originTime = temDayTask.getDayTaskTotal() * temDayTask.getDayTaskAvgTime();
                    Integer nowTotalTime = originTime + dayList.get(i).getDayTaskTotal() * dayList.get(i).getDayTaskAvgTime();
                    temDayTask.setDayTaskTotal(temDayTask.getDayTaskTotal() + dayList.get(i).getDayTaskTotal());
                    temDayTask.setSlowTaskNumber(temDayTask.getSlowTaskNumber() + dayList.get(i).getSlowTaskNumber());
                    //如果该类型本周没有该类型的训练记录
                    if(nowTotalTime != 0){
                        temDayTask.setDayTaskAvgTime(nowTotalTime/temDayTask.getDayTaskTotal());
                    }
                }
            }
        }


        String content = new String();
        for(int i=0; i<weekList.size();i++){            //求得所有数据后，计算每种类型任务的排队率
            DayTask perTask = weekList.get(i);
            if(perTask.getSlowTaskNumber() != 0){
                perTask.setSlowRatio((double)((float)perTask.getSlowTaskNumber()/(float)perTask.getDayTaskTotal()));
            }

            //整合邮件发送
            content+="类型 :";
            content+=perTask.getTaskType();
            content+="\n";
            content+="任务总数 ：";
            content+=perTask.getDayTaskTotal();
            content+="\n";
            content+="任务平均耗时 ：";
            content+=perTask.getDayTaskAvgTime();
            content+="\n";
            content+="排队率 ：";
            content+=perTask.getSlowRatio();
            content+="\n";
            content+="*******************\n\n";

        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo("332234337@qq.com");
        message.setSubject("test");
        message.setText(content);

        try{
            mailSender.send(message);
            System.out.println("\n发送邮件成功！！!********************\n");
        }catch (MailException e){
            e.printStackTrace();
            System.out.println("发送邮件失败！！！");
        }
    }

}


