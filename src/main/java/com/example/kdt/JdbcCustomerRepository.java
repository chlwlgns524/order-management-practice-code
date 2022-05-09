package com.example.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private final static Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    private final String SELECT_ALL_SQL = "SELECT * FROM customers";
    private final String SELECT_ALL_ID_SQL = "SELECT customer_id FROM customers";
    private final String SELECT_BY_NAME_SQL = "SELECT * FROM customers WHERE name = ?";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email) VALUES (UUID_TO_BIN(?), ?, ?)";
    private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String UPDATE_BY_EMAIL_SQL = "UPDATE customers SET email = ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String DELETE_ALL_SQL = "DELETE FROM customers";


    public static void main(String[] args) {

        new JdbcCustomerRepository().transactionalTest();
    }

    public void transactionalTest() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
            connection.setAutoCommit(false);

            PreparedStatement preparedStatementForName =
                    connection.prepareStatement("UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)");
            preparedStatementForName.setString(1, "test-user02");
            preparedStatementForName.setBytes(2, UUID.fromString("59d61b83-751e-4b03-8b64-0f99ed78fec6").toString().getBytes());
            preparedStatementForName.executeUpdate();
            preparedStatementForName.close();

            PreparedStatement preparedStatementForEmail =
                    connection.prepareStatement("UPDATE customers SET email = ? WHERE customer_id = UUID_TO_BIN(?)");
            preparedStatementForEmail.setString(1, "new-test-user@gmail.com");
            preparedStatementForEmail.setBytes(2, UUID.fromString("59d61b83-751e-4b03-8b64-0f99ed78fec6").toString().getBytes());
            preparedStatementForEmail.executeUpdate();
            preparedStatementForEmail.close();

            connection.commit();
        } catch (SQLException e) {
            logger.error("Got an empty connection." , e);
        } finally {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Connection close error." , e);
                }
            }
        }
    }

    public List<String> findALl() {
        List<String> names = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
             PreparedStatement prepareStatement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = prepareStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                names.add(name);
            }
        } catch (SQLException e) {
            logger.error("Got error while getting connection", e);
        }

        return names;
    }

    public List<UUID> findAllIds() {
        List<UUID> uuids = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
             PreparedStatement prepareStatement = connection.prepareStatement(SELECT_ALL_ID_SQL);
             ResultSet resultSet = prepareStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                UUID customer_id = toUUID(resultSet.getBytes("customer_id"));
                uuids.add(customer_id);
            }
        } catch (SQLException e) {
            logger.error("Got error while getting connection", e);
        }

        return uuids;
    }

    public List<String> findNames(String customerName) {

        List<String> names = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
             PreparedStatement prepareStatement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ) {
            prepareStatement.setString(1, customerName);

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    names.add(name);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while getting connection", e);
        }

        return names;
    }

    public int insertCustomer(UUID customerId, String name, String email) {
        try(
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
            PreparedStatement prepareStatement = connection.prepareStatement(INSERT_SQL);
            ) {
            prepareStatement.setBytes(1, customerId.toString().getBytes());
            prepareStatement.setString(2, name);
            prepareStatement.setString(3, email);

            return prepareStatement.executeUpdate();

        } catch(SQLException e) {
            logger.error("Got error while getting connection", e);
        }

        return 0;
    }

    public int deleteAllCustomers() {
        try(
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
                PreparedStatement prepareStatement = connection.prepareStatement(DELETE_ALL_SQL);
        ) {

            return prepareStatement.executeUpdate();

        } catch(SQLException e) {
            logger.error("Got error while getting connection", e);
        }

        return 0;
    }

    public int updateCustomerName(UUID customerId, String name) {
        try(
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC", "test", "test");
                PreparedStatement prepareStatement = connection.prepareStatement(UPDATE_BY_ID_SQL);
        ) {
            prepareStatement.setString(1, name);
            prepareStatement.setBytes(2, customerId.toString().getBytes());

            return prepareStatement.executeUpdate();

        } catch(SQLException e) {
            logger.error("Got error while getting connection", e);
        }

        return 0;
    }

    private static UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
