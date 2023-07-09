package com.example.spring_batch.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MyBatisConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Primary
    @Bean("primarySqlSessionFactory")
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // spring classpath convention : src/main/resources
        // sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mybatis/read/mappers/*.xml"));

        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        // scannerConfigurer.setBasePackage("com.example.spring_batch.mybatis.read.mappers");
        scannerConfigurer.setSqlSessionFactoryBeanName("primarySqlSessionFactory");
        scannerConfigurer.setApplicationContext(applicationContext);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("readSqlSessionFactory")
    public SqlSessionFactory readSqlSessionFactory(@Qualifier("readDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mybatis/read/mappers/*.xml"));

        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.example.spring_batch.mybatis.read.mappers");
        scannerConfigurer.setSqlSessionFactoryBeanName("readSqlSessionFactory");
        scannerConfigurer.setApplicationContext(applicationContext);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean("writeSqlSessionFactory")
    public SqlSessionFactory writeSqlSessionFactory(@Qualifier("writeDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mybatis/write/mappers/*.xml"));

        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.example.spring_batch.mybatis.write.mappers");
        scannerConfigurer.setSqlSessionFactoryBeanName("writeSqlSessionFactory");
        scannerConfigurer.setApplicationContext(applicationContext);

        return sqlSessionFactoryBean.getObject();
    }

    /*
        아래와 같이 SqlSessionTemplate의 Bean을 정의하면, 위 SqlSessionFactory의 Bean과 함께 자동으로 의존성이 주입 됨
        MyBatisPagingItemReader와 MyBatisBatchItemWriter 객체를 사용하면, 내부적으로 알아서 SqlSessionTemplate 의존성 주입 후 사용함
     */
    /*
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
     */
}