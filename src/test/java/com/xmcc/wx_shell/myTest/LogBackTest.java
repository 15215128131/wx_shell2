package com.xmcc.wx_shell.myTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class LogBackTest {

    private static Logger logger = LoggerFactory.getLogger(LogBackTest.class);

    @Test
    public void test(){
        logger.debug("logger日志{}","debug");
        logger.info("logger日志{}","info");
        logger.warn("logger日志{}","warn");
        logger.error("logger日志{}","error");
    }

}
