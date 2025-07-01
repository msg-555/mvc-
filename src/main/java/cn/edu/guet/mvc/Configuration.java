package cn.edu.guet.mvc;

import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 扫描Controller，获取到哪个Controller能处理哪个请求，并预先存储起来，如何保证Tomcat启动的时候，就自动去扫描呢？
 *
 * @Author liwei
 * @Date 2023/9/4 10:51
 * @Version 1.0
 */
public class Configuration {
    /**
     * 告诉系统扫描控制器，但是去哪里扫描？
     */
    public static Map<String, ControllerMapping> scanController() throws URISyntaxException, ClassNotFoundException {
        Map<String, ControllerMapping> controllerMapping = new HashMap<String, ControllerMapping>();

        // 拿到要扫描的包
        ResourceBundle bundle = ResourceBundle.getBundle("config");
        String controllerPackageName = bundle.getString("controller.package");
        // 把要扫描的包转成实际的路径
        URL url = Configuration.class.getResource("/" + controllerPackageName.replace(".", "/"));
        if (url != null) {
            URI uri = url.toURI();
            File file = new File(uri);
            String controllerClassNames[] = file.list();
            for (String className : controllerClassNames) {
                // 拿到全类名：因为要使用反射
                String fullClassName = controllerPackageName + "." + StringUtils.substringBefore(className, ".class");
                // 找出哪些类上面使用了@Controller注解，用什么技术？用反射

                Class controllerClass = Class.forName(fullClassName);
                if (controllerClass.isAnnotationPresent(Controller.class)) {
                    // 但是Controller类能处理哪些请求？还要进一步通过反射获取类的方法，然后拿到方法上的注解

                    Method methods[] = MethodUtils.getMethodsWithAnnotation(controllerClass, RequestMapping.class);
                    Arrays.stream(methods).forEach(method -> {
                        // 拿到RequestMapping注解，目的：拿到注解的属性值
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        /*
                        迄今为止，通过扫描获取到了什么？
                        1、Controller类
                        2、Controller类能处理的请求
                         */
                        ControllerMapping mapping = new ControllerMapping(controllerClass, method);

                        // 把请求和类、方法做映射
                        controllerMapping.put(annotation.value(), mapping);
                    });
                }
            }
        }
        return controllerMapping;
    }
}
