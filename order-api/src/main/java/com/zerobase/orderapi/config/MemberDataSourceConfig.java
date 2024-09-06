//package com.zerobase.orderapi.config;
//
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//
//@Configuration
//public class MemberDataSourceConfig {
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.hikari.member-datasource")
//    public DataSource memberDataSource() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }
//
//    @Bean
//    public PlatformTransactionManager memberTransactionManager(
//            @Qualifier("memberEntityManagerFactory") EntityManagerFactory memberEntityManagerFactory) {
//        JpaTransactionManager memberTransactionManager = new JpaTransactionManager();
//        memberTransactionManager.setEntityManagerFactory(memberEntityManagerFactory);
//        return memberTransactionManager;
//    }
//}
