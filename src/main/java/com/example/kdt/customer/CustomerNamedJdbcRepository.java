package com.example.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

//@Repository
public class CustomerNamedJdbcRepository implements CustomerRepository {

    private final static String SELECT_ALL_SQL = "SELECT * FROM customers";
    private final static String SELECT_BY_ID_SQL = "SELECT * FROM customers WHERE customer_id = UUID_TO_BIN(?)";
    private final String SELECT_BY_NAME_SQL = "SELECT * FROM customers WHERE name = ?";
    private final String SELECT_BY_EMAIL_SQL = "SELECT * FROM customers WHERE email = ?";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email, created_at) VALUES(UUID_TO_BIN(?), ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String DELETE_ALL_SQL = "DELETE FROM customers";

    private final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepository.class);

    private final DataSource dataSource;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        String name = rs.getString("name");
        String email = rs.getString("email");
        UUID customer_id = binToUuid(rs.getBytes("customer_id"));
        LocalDateTime lastLoginAt = rs.getTimestamp("last_login_at") == null ? null :
                rs.getTimestamp("last_login_at").toLocalDateTime();
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customer_id, name, email, lastLoginAt, createdAt);
    };

    @Autowired
    public CustomerNamedJdbcRepository(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Customer insert(Customer customer) {
        Map<String, Object> paramMap = new HashMap<>() {
            {
                put("customerId", customer.getCustomerId().toString().getBytes());
                put("name", customer.getName());
                put("email", customer.getEmail());
                put("createdAt", Timestamp.valueOf(customer.getCreatedAt()));
            }
        };
        int insertResult = namedParameterJdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) VALUES(UUID_TO_BIN(:customerId), :name, :email, :createdAt)", paramMap);

        if (insertResult != 1)
            throw new RuntimeException("Nothing was inserted.");

        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        Map<String, Object> paramMap = new HashMap<>() {
            {
                put("name", customer.getName());
                put("email", customer.getEmail());
                put("lastLoginAt", (customer.getLastLoginAt() == null ? null : Timestamp.valueOf(customer.getLastLoginAt())));
                put("customerId", customer.getCustomerId().toString().getBytes());
            }
        };
        int updateResult = namedParameterJdbcTemplate.update("UPDATE customers SET name = :name, email = :email, last_login_at = :lastLoginAt WHERE customer_id = UUID_TO_BIN(:customerId)", paramMap);

        if (updateResult != 1)
            throw new RuntimeException("Nothing was updated.");

        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL_SQL, customerRowMapper);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject("SELECT * FROM customers WHERE customer_id = UUID_TO_BIN(:customerId)",
                    Collections.singletonMap("customerId",customerId.toString().getBytes()),
                    customerRowMapper));
        } catch (DataAccessException e) {
            logger.error("Got empty result.", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject("SELECT * FROM customers WHERE name = :name",
                    Collections.singletonMap("name", name),
                    customerRowMapper));
        } catch (DataAccessException e) {
            logger.error("Got empty result.", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject("SELECT * FROM customers WHERE email = :email",
                    Collections.singletonMap("email", email),
                    customerRowMapper));
        } catch (DataAccessException e) {
            logger.error("Got empty result.", e);
            return Optional.empty();
        }
    }

    @Override
    public int count() {
        return namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM customers", Collections.emptyMap(), Integer.class);
    }

    @Override
    public void deleteAll() {
        namedParameterJdbcTemplate.update("DELETE FROM customers", Collections.emptyMap());
    }

    private void addFoundCustomer(List<Customer> customers, ResultSet resultSet) throws SQLException {
        UUID customerId = binToUuid(resultSet.getBytes("customer_id"));
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");

        Timestamp timestamp = resultSet.getTimestamp("last_login_at");
        LocalDateTime lastLoginAt = timestamp == null ? null : timestamp.toLocalDateTime();

        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        customers.add(new Customer(customerId, name, email, lastLoginAt, createdAt));
    }

    private static UUID binToUuid(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
