package com.nhnacademy.bookapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceChecker.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        logger.info("DATASOURCE = {}", dataSource.getClass().getName());
    }
}

