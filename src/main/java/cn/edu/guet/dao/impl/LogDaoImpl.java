package cn.edu.guet.dao.impl;

import cn.edu.guet.bean.Log;
import cn.edu.guet.dao.LogDao;
import cn.edu.guet.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogDaoImpl implements LogDao {
    @Override
    public void save(Log log) throws SQLException {
        Connection conn = DBUtil.getConnection();

        String sql = "INSERT INTO system_log VALUES(?,?,?,?,?,?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, log.getLogId());
        pstmt.setDate(2, new java.sql.Date(log.getLogTime().getTime()));
        pstmt.setString(3, log.getOperatorId());
        pstmt.setString(4, log.getOperationType());
        pstmt.setString(5, log.getOperationDesc());
        pstmt.setDate(6, new java.sql.Date(log.getCreateTime().getTime()));
        pstmt.setDate(7, new java.sql.Date(log.getUpdateTime().getTime()));

        pstmt.executeUpdate();
        DBUtil.close(null, pstmt, null);
    }
}
