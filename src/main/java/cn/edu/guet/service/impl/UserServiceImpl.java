package cn.edu.guet.service.impl;

import cn.edu.guet.bean.Log;
import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.dao.LogDao;
import cn.edu.guet.dao.UserDao;
import cn.edu.guet.http.HttpResult;
import cn.edu.guet.service.UserService;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private LogDao logDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setLogDao(LogDao logDao) {
        this.logDao = logDao;
    }

    @Override
    public List<User> getUserList() {
        return userDao.getUserList();
    }

    @Override
    public void updateUser(User user) throws SQLException {
        userDao.updateUser(user);
    }

    @Override
    public HttpResult saveUser(User user) throws SQLException {
        userDao.saveUser(user);
        Log log = new Log();
        Random random = new Random();
        int logId = random.nextInt(20000);
        log.setLogId(logId);
        log.setLogTime(new Date(System.currentTimeMillis()));
        log.setOperatorId("guet");
        log.setOperationType("saveUser--liwei");
        log.setOperationDesc("新增用户");
        log.setCreateTime(new Date(System.currentTimeMillis()));
        log.setUpdateTime(new Date(System.currentTimeMillis()));
        logDao.save(log);
        return HttpResult.ok("用户保存成功");
    }

    @Override
    public HttpResult deleteUser(int userId) throws SQLException {
        userDao.deleteUser(userId);
        Log log = new Log();
        Random random = new Random();
        int logId = random.nextInt(20000);
        log.setLogId(logId);
        log.setLogTime(new Date(System.currentTimeMillis()));
        log.setOperatorId("guet");
        log.setOperationType("deleteUser");
        log.setOperationDesc("删除用户");
        log.setCreateTime(new Date(System.currentTimeMillis()));
        log.setUpdateTime(new Date(System.currentTimeMillis()));
        logDao.save(log);
        return HttpResult.ok("用户删除成功");
    }
    @Override
    public List<Permission> getPermissionsByUsername(String username) {
    return userDao.getPermissionsByUsername(username);
    }
}
