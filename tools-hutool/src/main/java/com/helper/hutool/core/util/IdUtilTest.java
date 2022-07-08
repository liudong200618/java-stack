package com.helper.hutool.core.util;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author jaydon
 */
@Slf4j
public class IdUtilTest {

    @Test
    public void testFastSimpleUUID(){
        String simpleUUID = IdUtil.fastSimpleUUID();
        log.info("simpleUUID = {}",simpleUUID);
    }
    @Test
    public void testRandomUUID(){
        String randomUUID = IdUtil.randomUUID();
        log.info("randomUUID = {}",randomUUID);
    }
    @Test
    public void testObjectId(){
        String objectId = IdUtil.objectId();
        log.info("objectId = {}",objectId);
    }
    @Test
    public void testGetSnowflakeNextId(){
        long snowflakeNextId = IdUtil. getSnowflakeNextId();
        log.info("snowflakeNextId = {}",snowflakeNextId);
    }

    @Test
    public void testGetSnowflakeNextIdStr(){
        String snowflakeNextIdStr = IdUtil. getSnowflakeNextIdStr();
        log.info("snowflakeNextIdStr = {}",snowflakeNextIdStr);
    }
}
