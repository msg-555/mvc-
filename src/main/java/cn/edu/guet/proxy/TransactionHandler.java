package cn.edu.guet.proxy;

import cn.edu.guet.http.HttpResult;
import cn.edu.guet.util.DBUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionHandler implements InvocationHandler {
    private Object target;

    public TransactionHandler(Object target) {
        this.target = target;
    }

    public Object getProxy() {
//        Proxy proxy = (Proxy) Proxy.newProxyInstance(
//                target.getClass().getClassLoader(),
//                target.getClass().getInterfaces(),
//                this);
//        return proxy;
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Connection conn = DBUtil.getConnection();
        Object retVal = null;
        // 调用数据层
        //应该是可以了，没有测试错误的情况下是怎么样？
        //测试下
        try {

            // 调目标方法前开启事务
            conn.setAutoCommit(false);
            retVal = method.invoke(target, args);
            // 调目标之后提交事务
            conn.commit();

            return retVal;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
                retVal = HttpResult.error(method.getName() + "失败了，请联系管理员！");
                return retVal;
                //return false;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            DBUtil.close(conn,null,null);
        }


    }
}
