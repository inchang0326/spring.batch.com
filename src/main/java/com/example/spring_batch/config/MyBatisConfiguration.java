package com.example.spring_batch.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MyBatisConfiguration {
    private final ApplicationContext applicationContext;
    private final String writeClassPath = "classpath:mybatis/write/mappers/*.xml";
    private final String writeBasePackage = "com.example.spring_batch.mybatis.write.mappers";

    private final String readClassPath = "classpath:mybatis/read/mappers/*.xml";
    private final String readBasePackage = "com.example.spring_batch.mybatis.read.mappers";

    private final String primarySqlSessionFactory = "primarySqlSessionFactory";
    private final String readSqlSessionFactory = "readSqlSessionFactory";
    private final String writeSqlSessionFactory = "writeSqlSessionFactory";

    @Primary
    @Bean
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        return getSqlSessionFactoryBean(dataSource, null, null, primarySqlSessionFactory);
    }

    @Bean
    public SqlSessionFactory readSqlSessionFactory(@Qualifier("readDataSource") DataSource dataSource) throws Exception {
        return getSqlSessionFactoryBean(dataSource, readClassPath, readBasePackage, readSqlSessionFactory);
    }

    @Bean
    public SqlSessionFactory writeSqlSessionFactory(@Qualifier("writeDataSource") DataSource dataSource) throws Exception {
        return getSqlSessionFactoryBean(dataSource, writeClassPath, writeBasePackage, writeSqlSessionFactory);
    }

    private SqlSessionFactory getSqlSessionFactoryBean(DataSource dataSource, String classPath, String basePackage, String sqlSessionFactory) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        if( classPath != null ) sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources(classPath));

        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        if( basePackage != null ) scannerConfigurer.setBasePackage(basePackage);
        scannerConfigurer.setSqlSessionFactoryBeanName(sqlSessionFactory);
        scannerConfigurer.setApplicationContext(applicationContext);

        return sqlSessionFactoryBean.getObject();
    }

    /*
        아래와 같이 SqlSessionTemplate의 Bean을 정의하면, 위 SqlSessionFactory의 Bean과 함께 자동으로 의존성이 주입 됨
        MyBatisPagingItemReader와 MyBatisBatchItemWriter 객체를 사용하면, 내부적으로 알아서 SqlSessionTemplate 의존성 주입 후 사용함
        MapperInterface를 따로 쓰지 않는 이상 정의하지 않아도 됨
     */
    @Primary
    @Bean
    public SqlSessionTemplate readSqlSessionTemplate(@Qualifier("readSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    @Bean
    public SqlSessionTemplate writeSqlSessionTemplate(@Qualifier("writeSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}