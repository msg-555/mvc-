package cn.edu.guet.bean;

import java.util.List;

public class Permission {
    private int perId;
    private String perName;
    private String url;
    private String icon;
    private Boolean isParent;
    private int parentId;
    private Permission parent;
    private List<Permission> subPermissionList;

    public int getPerId() {
        return perId;
    }

    public void setPerId(int perId) {
        this.perId = perId;
    }

    public String getPerName() {
        return perName;
    }

    public void setPerName(String perName) {
        this.perName = perName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getParent() {
        return isParent;
    }

    public void setParent(Boolean parent) {
        isParent = parent;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<Permission> getSubPermissionList() {
        return subPermissionList;
    }

    public void setSubPermissionList(List<Permission> subPermissionList) {
        this.subPermissionList = subPermissionList;
    }

    public void setParent(Permission parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "perId=" + perId +
                ", perName='" + perName + '\'' +
                ", url='" + url + '\'' +
                ", icon='" + icon + '\'' +
                ", isParent=" + isParent +
                ", parentId=" + parentId +
                '}';
    }
}
