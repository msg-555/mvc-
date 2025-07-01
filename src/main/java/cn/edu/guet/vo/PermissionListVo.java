package cn.edu.guet.vo;

import cn.edu.guet.bean.Permission;

import java.util.List;

/**
 * ViewObject
 */
public class PermissionListVo {
    private String status;
    private List<Permission> permissionList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}
