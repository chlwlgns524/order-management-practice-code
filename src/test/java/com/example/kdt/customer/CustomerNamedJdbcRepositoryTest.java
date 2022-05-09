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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
class CustomerNamedJdbcRepositoryTest {

    private final static Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepositoryTest.class);

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

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

    }

    @Autowired
    CustomerNamedJdbcRepository customerNamedJdbcRepository;

    @Autowired
    DataSource dataSource;

    Customer testCustomer;


    @BeforeAll
    void setup() {
        testCustomer = new Customer(UUID.randomUUID(), "new-test-user", "new-test-user@gmail.com", LocalDateTime.now());
        customerNamedJdbcRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Hikari CP was successfully injected by the inner Config.")
    void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
    }

    @Test
    @Order(2)
    @DisplayName("새로운 고객을 데이터베이스에 저장할 수 있다.")
    void testInsert() {
        customerNamedJdbcRepository.insert(testCustomer);

        Optional<Customer> foundCustomer = customerNamedJdbcRepository.findById(testCustomer.getCustomerId());

        assertThat(foundCustomer.get().getCustomerId()).isEqualTo(testCustomer.getCustomerId());
    }
    
    @Test
    @Order(3)
    @DisplayName("데이터베이스에 저장된 모든 고객을 조회할 수 있다.")
    void testFindAll() {
        List<Customer> customers = customerNamedJdbcRepository.findAll();
        assertThat(customers.isEmpty()).isFalse();
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 데이터베이스에 저장된 고객을 조회할 수 있다.")
    void testFindByName() {
        Optional<Customer> customer = customerNamedJdbcRepository.findByName(testCustomer.getName());
        assertThat(customer.isPresent()).isTrue();

        Optional<Customer> unknown = customerNamedJdbcRepository.findByName("unknown");
        assertThat(unknown.isPresent()).isFalse();
    }

    @Test
    @Order(5)
    @DisplayName("이메일로 데이터베이스에 저장된 고객을 조회할 수 있다.")
    void testFindByEmail() {
        Optional<Customer> customer = customerNamedJdbcRepository.findByEmail(testCustomer.getEmail());
        assertThat(customer.isPresent()).isTrue();
        Optional<Customer> unknown = customerNamedJdbcRepository.findByEmail("unknown@gmail.com");
        assertThat(unknown.isPresent()).isFalse();
    }

    @Test
    @Order(6)
    @DisplayName("기존에 저장된 고객의 정보를 수정할 수 있다.")
    public void testUpdate() {
        testCustomer.changeName("updated_test_name");
        customerNamedJdbcRepository.update(testCustomer);

        List<Customer> customers = customerNamedJdbcRepository.findAll();

        assertThat(customers.get(0).getName()).isEqualTo(testCustomer.getName());
    }

}