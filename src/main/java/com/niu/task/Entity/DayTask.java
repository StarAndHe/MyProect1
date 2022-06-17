package com.niu.task.Entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class DayTask implements Serializable {

    private String taskType;

    private Integer dayTaskTotal = 0;

    private Integer dayTaskAvgTime = 0;

    private Integer SlowTaskNumber = 0;

    private Double  SlowRatio = 0.0;

    private java.sql.Timestamp createTime;

    private java.sql.Timestamp updateTime;

    private BigInteger engineCostTime;

}
