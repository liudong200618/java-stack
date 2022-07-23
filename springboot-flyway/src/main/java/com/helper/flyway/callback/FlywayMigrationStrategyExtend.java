package com.helper.flyway.callback;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注意的就是boot集成的Flyway默认只可以进行migrate
 * 的操作，但是我们可以使用FlywayMigrationStrategy对其进行自定义的各种扩展，
 * 我们可以试着扩展 下
 * @author jaydon
 */
@Configuration
public class FlywayMigrationStrategyExtend {

    @Bean
    public FlywayMigrationStrategy constructFlywayMigrationStrategyExtendLocal(){

        return new FlywayMigrationStrategyExtendLocal();
    }

    private  static class FlywayMigrationStrategyExtendLocal implements FlywayMigrationStrategy{


        @Override
        public void migrate(Flyway flyway) {

            System.out.println("before migrate flyway");

            flyway.migrate();

            System.out.println("after migrate flyway");



        }
    }
    
}