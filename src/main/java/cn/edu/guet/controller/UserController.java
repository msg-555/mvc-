package cn.edu.guet.controller;


import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.http.HttpResult;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.proxy.TransactionHandler;
import cn.edu.guet.service.UserService;

import java.sql.SQLException;
import java.util.List;

@Controller
public class UserController {
    private UserService userService ;

    public void setUserService(UserService userService) {
        this.userService = (UserService) new TransactionHandler(userService).getProxy();
    }

    // 获取用户列表
    @RequestMapping(value = "/getUserList")
    public HttpResult getUserList() {
        List<User> userList = userService.getUserList();
        return HttpResult.ok(userList);

    }
    @RequestMapping(value = "/saveUser")
    public HttpResult saveUser(User user) {
        try {
            return userService.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/deleteUser")
    public HttpResult deleteUser(int userId) {
        try {
            return userService.deleteUser(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @RequestMapping(value = "/login")
    public HttpResult login(String username) {
        List<Permission> permissions = userService.getPermissionsByUsername(username);
        return HttpResult.ok(permissions);
    }


}
