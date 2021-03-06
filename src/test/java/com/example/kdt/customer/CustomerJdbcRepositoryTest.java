package com.example.kdt.customer;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerJdbcRepositoryTest {

    private final static Logger logger = LoggerFactory.getLogger(CustomerJdbcRepositoryTest.class);

    @Configuration
    @ComponentScan(basePackages = {"com.example.kdt.customer"})
    static class Config {
        
        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC")
                    .username("test")
                    .password("test")
                    .type(HikariDataSource.class)
                    .build();

//            dataSource.setMaximumPoolSize(1000);
//            dataSource.setMinimumIdle(100);

            return dataSource;
        }
        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }

    @Autowired
    CustomerJdbcRepository customerJdbcRepository;

    @Autowired
    DataSource dataSource;

    Customer testCustomer;


    @BeforeAll
    void setup() {
        testCustomer = new Customer(UUID.randomUUID(), "new-test-user", "new-test-user@gmail.com", LocalDateTime.now());
        customerJdbcRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Hikari CP was successfully injected by the inner Config.")
    void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
    }

    @Test
    @Order(2)
    @DisplayName("????????? ????????? ????????????????????? ????????? ??? ??????.")
    void testInsert() {
        customerJdbcRepository.insert(testCustomer);

        Optional<Customer> foundCustomer = customerJdbcRepository.findById(testCustomer.getCustomerId());

        assertThat(foundCustomer.get().getCustomerId()).isEqualTo(testCustomer.getCustomerId());
    }
    
    @Test
    @Order(3)
    @DisplayName("????????????????????? ????????? ?????? ????????? ????????? ??? ??????.")
    void testFindAll() {
        List<Customer> customers = customerJdbcRepository.findAll();
        assertThat(customers.isEmpty()).isFalse();
    }

    @Test
    @Order(4)
    @DisplayName("???????????? ????????????????????? ????????? ????????? ????????? ??? ??????.")
    void testFindByName() {
        Optional<Customer> customer = customerJdbcRepository.findByName(testCustomer.getName());
        assertThat(customer.isPresent()).isTrue();

        Optional<Customer> unknown = customerJdbcRepository.findByName("unknown");
        assertThat(unknown.isPresent()).isFalse();
    }

    @Test
    @Order(5)
    @DisplayName("???????????? ????????????????????? ????????? ????????? ????????? ??? ??????.")
    void testFindByEmail() {
        Optional<Customer> customer = customerJdbcRepository.findByEmail(testCustomer.getEmail());
        assertThat(customer.isPresent()).isTrue();
        Optional<Customer> unknown = customerJdbcRepository.findByEmail("unknown@gmail.com");
        assertThat(unknown.isPresent()).isFalse();
    }

    @Test
    @Order(6)
    @DisplayName("????????? ????????? ????????? ????????? ????????? ??? ??????.")
    public void testUpdate() {
        testCustomer.changeName("updated_test_name");
        customerJdbcRepository.update(testCustomer);

        List<Customer> customers = customerJdbcRepository.findAll();

        assertThat(customers.get(0).getName()).isEqualTo(testCustomer.getName());
    }

}