package com.baidu;

import com.github.wujun234.uid.UidGenerator;
import com.github.wujun234.uid.impl.DefaultUidGenerator;
import com.helper.spring.boot.uid.generator.UidGeneratorApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UidGeneratorApplication.class)
@ActiveProfiles("dev")
@Slf4j
public class UidGen {

    /**
     * 根据百度uid-generator封装
     * https://github.com/baidu/uid-generator.git
     */
    @Resource
    private UidGenerator cachedUidGenerator;

    @Resource
        private DefaultUidGenerator defaultUidGenerator;
    @Test
    public void testSerialGenerate() {
        // Generate UID
        long uid = cachedUidGenerator.getUID();
        log.info("uid = {}",uid);
        // Parse UID into [Timestamp, WorkerId, Sequence]
        // {"UID":"450795408770","timestamp":"2019-02-20 14:55:39","workerId":"27","sequence":"2"}
        System.out.println(cachedUidGenerator.parseUID(uid));

    }
    @Test
    public void testDefaultUidGenerator() {
        // Generate UID
        long uid = defaultUidGenerator.getUID();
        log.info("uid = {}",uid);
        log.info("uidBinary = {}",Long.toBinaryString(uid));
        // Parse UID into [Timestamp, WorkerId, Sequence]
        // {"UID":"450795408770","timestamp":"2019-02-20 14:55:39","workerId":"27","sequence":"2"}
        System.out.println(defaultUidGenerator.parseUID(uid));
    }

    @Test
    public void test() throws InterruptedException {
        while(true) {
            long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            System.out.println("currentSecond = " + currentSecond);
            Thread.sleep(1000);
        }


    }
}
