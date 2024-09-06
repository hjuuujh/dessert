//package com.zerobase.orderapi.config;
//
//import org.hibernate.cfg.AvailableSettings;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = {
//                "com.zerobase.orderapi.repository.member"
//        },
//        entityManagerFactoryRef = "memberEntityManagerFactory",
//        transactionManagerRef = "memberTransactionManager"
//)
//public class MemberDataManagerConfig {
//
//    private final DataSource memberDataSource;
//
//    public MemberDataManagerConfig(
//            @Qualifier("memberDataSource") DataSource memberDataSource) {
//        this.memberDataSource = memberDataSource;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean memberEntityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//
//        em.setDataSource(this.memberDataSource);
//        em.setPersistenceUnitName("memberEntityManager");
//        em.setPackagesToScan(
//                "com.zerobase.orderapi.domain.member"
//        );
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.setJpaProperties(memberJpaProperties());
//        em.afterPropertiesSet();
//        return em;
//    }
//
//    @Bean
//    public JdbcTemplate memberJdbcTemplate(@Qualifier("memberDataSource") DataSource memberDataSource) {
//        return new JdbcTemplate(memberDataSource);
//    }
//
//    private Properties memberJpaProperties() {
//        Properties properties = new Properties();
////        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
//        properties.setProperty(AvailableSettings.SHOW_SQL, "true");
//        properties.setProperty(AvailableSettings.ALLOW_UPDATE_OUTSIDE_TRANSACTION, "true");
//        return properties;
//    }
//}
