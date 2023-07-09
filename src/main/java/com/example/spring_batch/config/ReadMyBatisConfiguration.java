package com.example.spring_batch.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration // @Configuration가 있어야, @Bean에 대한 의존성이 주입 됨
@MapperScan(
        basePackages = "com.example.spring_batch.mybatis.read.mappers"
)
public class ReadMyBatisConfiguration {
    @Autowired
    @Qualifier("readDataSource")
    private DataSource readDataSource;
    @Autowired
    private ApplicationContext applicationContext;

    @Bean("readSqlSessionFactory")
    public SqlSessionFactory readSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(readDataSource);
        // spring classpath convention : src/main/resources
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mybatis/read/mappers/*.xml"));

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