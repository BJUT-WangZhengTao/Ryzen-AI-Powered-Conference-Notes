package com.siteapplet.siteproject.domain;

import lombok.Data;

/**
 * @author: danghongbo
 * ===============================
 * Created with IDEA
 * Date: 2022/9/5
 * Time: 13:09
 * ===============================
 * 每日打卡实体类
 */

@Data
public class DailyPunch {
    private int id;
    private int nameId;
    private String time;
    private int monthPunch;
}

