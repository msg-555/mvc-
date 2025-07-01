package cn.edu.guet.mvc; /**
 * @Author liwei
 * @Date 2023/9/4 11:00
 * @Version 1.0
 */

import cn.edu.guet.ioc.BeanFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URISyntaxException;
import java.util.Map;

public class ConfigurationListener implements ServletContextListener {

    public ConfigurationListener() {
    }

    /**
     * ServletContext：Servlet上下文（全局Servlet配置对象）只要被创建，该方法就会自动执行
     * Tomcat启动的时候会自动创建ServletContext
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /* This method is called when the servlet context is initialized(when the Web application is deployed). */
        try {
            Map<String, ControllerMapping> controllerMapping = Configuration.scanController();
            // 把映射放入全局上下文（application作用域），以便DispatcherServlet调用
            // 拿到ServletContext（其实就是我们说的JSP的几个作用域之：application作用域，也就是全局作用域）
            sce.getServletContext().setAttribute("mapping",controllerMapping);

            // forName：把BeanFactory加载Java的虚拟机中，此时可能还没有类的实例，没有类的实例，就无法调用非静态方法（成员方法|实例方法）
            Class.forName("cn.edu.guet.ioc.BeanFactory");

        } catch (URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
    }
}
