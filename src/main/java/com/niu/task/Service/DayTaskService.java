package com.niu.task.Service;

import com.niu.task.Entity.DayTask;
import com.niu.task.util.DateOperate;
import com.niu.task.Mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class DayTaskService {

    @Autowired
    TaskMapper taskMapper;

    public DayTask  findDayTask(Date uDate, String type) {
        java.util.Date oneBefore = uDate;
        java.util.Date oneAfter = DateOperate.dateToOneAfter(uDate);
        java.sql.Date qDateBefore = new java.sql.Date(oneBefore.getTime());
        java.sql.Date qDateAfter = new java.sql.Date(oneAfter.getTime());
        return taskMapper.findDayTask(qDateBefore,qDateAfter,type);
    }

    public List<DayTask> findSlowTaskNumber(Date uDate, String type) {
        java.util.Date oneBefore = DateOperate.getCurrentHourTime(uDate);
        java.util.Date oneAfter = DateOperate.getNextHourTime(uDate,1);
        java.sql.Timestamp qDateBefore = new java.sql.Timestamp(oneBefore.getTime());
        java.sql.Timestamp qDateAfter = new java.sql.Timestamp(oneAfter.getTime());
        return taskMapper.findSlowTaskNumber(qDateBefore,qDateAfter, type);
    }
}
