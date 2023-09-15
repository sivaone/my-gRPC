package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDao {

    private static final Logger logger = Logger.getLogger(OrderDao.class.getName());

    public List<Order> getOrders(int userId) {

        List<Order> orders = new ArrayList<>();
        Connection conn = H2DatabaseConnection.getConnectionToDatabase();

        try(PreparedStatement stmt = conn.prepareStatement("select * from orders where user_id=?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setUserId(rs.getInt("user_id"));
                order.setOrderId(rs.getInt("order_id"));
                order.setNumberOfItems(rs.getInt("no_of_items"));
                order.setAmount(rs.getDouble("total_amount"));
                order.setOrderDate(rs.getDate("order_date"));
                orders.add(order);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing order sql query", e);
            throw new RuntimeException(e);
        }

        logger.info("Returning orders for user " + userId);
        return orders;
    }
}
