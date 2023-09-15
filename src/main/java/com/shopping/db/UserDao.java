package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {

    private static final Logger logger = Logger.getLogger(UserDao.class.getName());

    public User getDetails(String username) {
        User user = new User();

        try {
            Connection conn = H2DatabaseConnection.getConnectionToDatabase();
            logger.info("Executing prepared statement");

            PreparedStatement ps = conn.prepareStatement("select * from user_details where username=?");
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            logger.info("Prepared statement is executed");


            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setAge(rs.getInt("age"));
                user.setName(rs.getString("name"));
                user.setGender(rs.getString("gender"));
            }
            return user;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
