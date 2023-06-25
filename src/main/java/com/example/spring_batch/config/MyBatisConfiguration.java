package com.example.spring_batch.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration // @Configuration가 있어야, @Bean에 대한 의존성이 주입 됨
@MapperScan(
        basePackages = "com.example.spring_batch.mybatis.mappers"
)
@RequiredArgsConstructor
public class MyBatisConfiguration extends DefaultBatchConfigurer {
    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // spring classpath convention : src/main/resources
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mappers/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    // 아래와 같이 SqlSessionTemplate의 Bean을 정의하면, 위 SqlSessionFactory의 Bean과 함께 자동으로 의존성이 주입 됨
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}