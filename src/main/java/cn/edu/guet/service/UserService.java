package cn.edu.guet.service;


import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.http.HttpResult;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
//    List<Permission> getPermissionsByUsername(String username);
    List<User> getUserList();

    void updateUser(User user) throws SQLException;

    HttpResult saveUser(User user) throws Exception;

    HttpResult deleteUser(int userId) throws SQLException;
    List<Permission> getPermissionsByUsername(String username);


}
