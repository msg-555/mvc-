package cn.edu.guet.util;

import java.sql.*;

/**
 * 获取数据库连接、关闭连接、关闭PreparedStatement、关闭Result
 */
public class DBUtil {

    public static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    public static Connection getConnection() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection("jdbc:oracle:thin:@39.108.123.201:1521:ORCL", "scott", "tiger");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            threadLocal.set(conn);
            return conn;
        }
        return conn;
    }

    /**
     * 释放资源
     *
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try{
            if (conn != null) {
                conn.close();
                threadLocal.remove();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
