package cn.edu.guet.dao;

import cn.edu.guet.bean.Log;

import java.sql.SQLException;

public interface LogDao {
    void save(Log log) throws SQLException;
}
