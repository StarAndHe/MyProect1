package com.niu.task.Mapper;

import com.niu.task.Entity.DayTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface TaskMapper {

    @Select("SELECT  COUNT(0) AS dayTaskTotal, AVG(TIME_TO_SEC(TIMEDIFF(update_time,create_time))) AS dayTaskAvgTime FROM tb_task WHERE create_time>=#{qDateBefore} and create_time<=#{qDateAfter} and task_type=#{type}  ")
    DayTask findDayTask(@Param("qDateBefore") Date qDateBefore, @Param("qDateAfter") Date qDateAfter, @Param("type") String type);

    @Select("SELECT create_time AS createTime,  update_time AS updateTime, engine_cost_time AS engineCostTime  from tb_task where create_time>=#{qDateBefore} and create_time<=#{qDateAfter} and task_type=#{type}  ")
    List<DayTask> findSlowTaskNumber(@Param("qDateBefore") Timestamp qDateBefore, @Param("qDateAfter") Timestamp qDateAfter, @Param("type") String type);

}
