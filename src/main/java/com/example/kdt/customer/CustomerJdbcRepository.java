package com.example.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class CustomerJdbcRepository implements CustomerRepository {

    private final static String SELECT_ALL_SQL = "SELECT * FROM customers";
    private final static String SELECT_BY_ID_SQL = "SELECT * FROM customers WHERE customer_id = UUID_TO_BIN(?)";
    private final String SELECT_BY_NAME_SQL = "SELECT * FROM customers WHERE name = ?";
    private final String SELECT_BY_EMAIL_SQL = "SELECT * FROM customers WHERE email = ?";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email, created_at) VALUES(UUID_TO_BIN(?), ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String DELETE_ALL_SQL = "DELETE FROM customers";

    private final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
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
    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Customer insert(Customer customer) {
        int insertResult = jdbcTemplate.update(INSERT_SQL,
                customer.getCustomerId().toString().getBytes(),
                customer.getName(),
                customer.getEmail(),
                Timestamp.valueOf(customer.getCreatedAt()));

        if (insertResult != 1) {
            throw new RuntimeException("Nothing was inserted.");
        }
        /*
        int result = 0;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
        ) {
            preparedStatement.setBytes(1, customer.getCustomerId().toString().getBytes());
            preparedStatement.setString(2, customer.getName());
            preparedStatement.setString(3, customer.getEmail());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
            result = preparedStatement.executeUpdate();

            if (result != 1)
                throw new RuntimeException("Nothing was inserted.");
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }
        */
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        int updateResult = jdbcTemplate.update(UPDATE_SQL,
                customer.getName(),
                customer.getEmail(),
                (customer.getLastLoginAt() == null ? null : Timestamp.valueOf(customer.getLastLoginAt())),
                customer.getCustomerId().toString().getBytes());

        if (updateResult != 1) {
            throw new RuntimeException("Nothing was updated.");
        }
        /*
        try(
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
        ) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setTimestamp(3, (customer.getLastLoginAt() == null ? null : Timestamp.valueOf(customer.getLastLoginAt())));
            preparedStatement.setBytes(4, customer.getCustomerId().toString().getBytes());
            int result = preparedStatement.executeUpdate();

            if (result != 1)
                throw new RuntimeException("Nothing was updated.");
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }
        */
        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, customerRowMapper);
        /*
        List<Customer> customers = new ArrayList<>();

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                addFoundCustomer(customers, resultSet);
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }

        return customers;
        */
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_ID_SQL,
                    customerRowMapper,
                    customerId.toString().getBytes()));
        } catch (DataAccessException e) {
            logger.error("Got empty result.");
            return Optional.empty();
        }
        /*
        List<Customer> customer = new ArrayList<>();

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL);
        ) {
            preparedStatement.setBytes(1, customerId.toString().getBytes());
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    addFoundCustomer(customer, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }

        return customer.stream().findFirst();
        */
    }

    @Override
    public Optional<Customer> findByName(String name) {
        /*
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_NAME_SQL,
                    customerRowMapper,
                    name));
        } catch (DataAccessException e) {
            logger.error("Got empty result.");
            return Optional.empty();
        }
        */

        List<Customer> customer = new ArrayList<>();

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    addFoundCustomer(customer, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }

        return customer.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        /*
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_EMAIL_SQL,
                    customerRowMapper,
                    email));
        } catch (DataAccessException e) {
            logger.error("Got empty result.");
            return Optional.empty();
        }
        */

        List<Customer> customer = new ArrayList<>();

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_EMAIL_SQL);
        ) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    addFoundCustomer(customer, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }

        return customer.stream().findFirst();
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM customers", Integer.class);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL_SQL);
        /*
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_SQL);
        ) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Got error while closing connection");
            throw new RuntimeException(e);
        }
        */
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
