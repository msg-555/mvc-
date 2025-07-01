package cn.edu.guet.dao.impl;

import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.dao.UserDao;
import cn.edu.guet.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImplOracle implements UserDao {

    @Override
    public boolean login(String username, String password) {
        // 写JDBC代码(固定模式，没有任何难度）
        String URL = "jdbc:oracle:thin:@39.108.123.201:1521:ORCL";
        String USER = "scott";
        String PASSWORD = "tiger";

        String sql = "SELECT * FROM USERS WHERE USER_NAME = ? AND PASSWORD = ?";
        try {
            // 加载驱动
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("数据库操作错误: " + e.getMessage());
        }
        return false;
    }


    @Override
    public List<User> getUserList() {
        // 写JDBC代码来获取数据
        String URL = "jdbc:oracle:thin:@39.108.123.201:1521:ORCL";
        String USER = "scott";
        String PASSWORD = "tiger";

        List<User> userList = new ArrayList<>();

        String sql = "SELECT user_id,user_name,user_phone FROM users";
        try {
            // 加载驱动
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUserName(rs.getString("user_name"));
                    user.setPhone(rs.getString("user_phone"));
                    userList.add(user);
                }
                return userList;
            }
        } catch (SQLException e) {
            System.err.println("数据库操作错误: " + e.getMessage());
        }
        return List.of();
    }

    @Override
    public void updateUser(User user) throws SQLException {
        // 写JDBC代码(固定模式，没有任何难度）
        String URL = "jdbc:oracle:thin:@39.108.123.201:1521:ORCL";
        String USER = "scott";
        String PASSWORD = "tiger";

        String sql = "UPDATE users SET user_name = ?,user_phone = ? WHERE USER_ID = ?";
        try {
            // 加载驱动
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, user.getUserName());
        pstmt.setString(2, user.getPhone());
        pstmt.setInt(3, user.getUserId());

        pstmt.executeUpdate();
    }

    @Override
    public void saveUser(User user) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO users(user_name,user_phone,password) VALUES(?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, user.getUserName());
        pstmt.setString(2, user.getPhone());
        pstmt.setString(3, "123456");

        pstmt.executeUpdate();
        DBUtil.close(null, pstmt, null);
    }

    @Override
    public void deleteUser(int userId) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "DELETE FROM users WHERE USER_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);

        pstmt.executeUpdate();
        DBUtil.close(null, pstmt, null);
    }
    public List<Permission> getPermissionsByUsername(String username) {
        Connection conn = DBUtil.getConnection();

        List<Permission> permissionList = new ArrayList<>();

        String sql = "SELECT p.*\n" +
                "FROM users u,\n" +
                "     user_role ur,\n" +
                "     roles_permission rp,\n" +
                "     permission p\n" +
                "WHERE u.user_id = ur.user_id\n" +
                "  AND ur.role_id = rp.role_id\n" +
                "  AND rp.per_id = p.per_id\n" +
                "  AND u.user_name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPerId(rs.getInt("per_id"));
                    permission.setPerName(rs.getString("per_name"));
                    permission.setUrl(rs.getString("url"));
                    permission.setIcon(rs.getString("icon"));
                    permission.setParent(rs.getBoolean("is_parent"));
                    permission.setParentId(rs.getInt("parent_id"));
                    permissionList.add(permission);
                }
                return permissionList;
            }
        } catch (SQLException e) {
            System.err.println("数据库操作错误: " + e.getMessage());
        }
        return List.of();
    }

}
