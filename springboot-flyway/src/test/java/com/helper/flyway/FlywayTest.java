package com.helper.flyway;

import com.alibaba.fastjson.JSONObject;
import com.helper.flyway.callback.MyNotifierCallback;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@Data
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class FlywayTest {
    @Autowired
    private Flyway flyway;

    @Test
    public void dbMigrate() {
        System.out.println(flyway.migrate());
    }

    @Test
    public void dbValidate() {
        flyway.validate();
    }

    @Test
    public void dbClean() {
        flyway.clean();
    }

    @Test
    public void dbInfo() {
        System.out.println(JSONObject.toJSONString(flyway.info()));
    }

    @Test
    public void dbBaseline() {
        flyway.baseline();
    }

    @Test
    public void dbRepair() {
        flyway.repair();
    }


    @Test
    public void migrateWithCallbacks() {
        String url = "jdbc:mysql://127.0.0.1:3306/flyway?useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "eastcompeace";

        Flyway flyway =
                 Flyway.configure()
                         .encoding("ISO_8859_1")
                .dataSource(url,user,password)
                .locations("db/migration", "db/callbacks")
                .callbacks(new MyNotifierCallback())
                .load();
        flyway.migrate();
    }
}
