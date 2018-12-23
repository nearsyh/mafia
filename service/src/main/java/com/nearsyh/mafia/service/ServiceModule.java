package com.nearsyh.mafia.service;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceModule {

    @Bean
    public DataSource providesPGDataSource() {
        var url = "jdbc:postgresql://recommendation.cjyejqv2btnw.rds.cn-north-1.amazonaws.com.cn:5432/mafia";
        var properties = new Properties();
        properties.setProperty("user", "jikeapp");
        properties.setProperty("password", "jikeapppassword");
        var connectionFactory = new PoolableConnectionFactory(
            new DriverManagerConnectionFactory(url, properties), null);
        var connectionPool = new GenericObjectPool<>(connectionFactory);
        connectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }
}
