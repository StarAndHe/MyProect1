package com.niu.task.util;

import com.niu.task.Entity.DayTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class Calculator{
    @Value("${test.val}")
    private String val;

    @Async("myTaskExecutor")
    public Future<Integer> calculateSlow(List<DayTask> tList)throws Exception{
        int returnVal = 0;
        for(int c=0;c<tList.size();c++){
            DayTask t = tList.get(c);
            BigInteger perTime = DateOperate.diff(t.getCreateTime(),t.getUpdateTime());
            if(perTime.compareTo(t.getEngineCostTime().add(new BigInteger(val))) > 0){
                returnVal+=1;
            }
        }
        //System.out.println("当前线程为 ：" + Thread.currentThread().getName());
        return new AsyncResult<>(returnVal);
    }

}
