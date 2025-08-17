package com.siteapplet.siteproject;

import com.siteapplet.siteproject.domain.DailyPunch;
import com.siteapplet.siteproject.mapper.DailyPunchMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SiteProjectApplicationTests {

    @Autowired
    private DailyPunchMapper dailyPunchMapper;

    @Test
    void findAll() {
        List<DailyPunch> arr = dailyPunchMapper.selectList(null );
        System.out.println(arr);
    }

    /**
     *  测试添加每日打卡条目
     */
    @Test
    void addDailyPunch() {
        DailyPunch dailyPunch = new DailyPunch();
        dailyPunch.setId(3);
        dailyPunch.setNameId(4);
        dailyPunch.setTime("2022-09-25");
        dailyPunch.setMonthPunch(1023123);
        int insert_res = dailyPunchMapper.insert(dailyPunch);
        System.out.println("Insert: " + insert_res);
    }

}
