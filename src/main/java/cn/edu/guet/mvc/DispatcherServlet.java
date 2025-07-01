package cn.edu.guet.mvc;

/**
 * 前端控制器：所有的请求会先到达DispatcherServlet，那DispatcherServlet如何把请求派发出去？
 * 假如当前请求是：http://localhost:8080/user/saveUser.do
 *
 * @Author liwei
 * @Date 2023/9/4 11:49
 * @Version 1.0
 */

import cn.edu.guet.bean.User;
import cn.edu.guet.ioc.BeanFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    private Map<String, ControllerMapping> controllerMapping;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        controllerMapping = (Map<String, ControllerMapping>) config.getServletContext()
                .getAttribute("mapping");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // saveUser.do到达后，要去Map中根据key（saveUser）找到ControlelrMapping（包含：XXController、方法）
        // Map如何拿到？
        String uri = request.getRequestURI();

        int startIndex = uri.lastIndexOf("/");
        int endIndex = uri.lastIndexOf(".");
        uri = uri.substring(startIndex, endIndex);

        ControllerMapping mapping = controllerMapping.get(uri);

        ClassPool pool = ClassPool.getDefault();

        Object obj;
        try {
            CtClass ctClass = pool.get(mapping.getControllerClass().getName());
            Method method = mapping.getHandleMethod();
            int count = method.getParameterCount();
            // 获取方法的参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            // 创建参数数组
            CtClass[] ctParams = new CtClass[parameterTypes.length];
            // 给参数数组赋值
            for (int i = 0; i < ctParams.length; i++) {
                ctParams[i] = pool.getCtClass(parameterTypes[i].getName());
            }
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), ctParams);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            // 获取属性变量相关
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            // 获取方法本地变量信息，包括方法声明和方法体内的变量
            // 需注意，若方法为非静态方法，则第一个变量名为this
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            int pos = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
            List<String> parameterNameList = new ArrayList<>();
            for (int i = 1; i <=count; i++) {
                String parameterName = attr.variableName(i + pos);
                parameterNameList.add(parameterName);
            }
            // 参数值数组
            Object parameterValues[] = new Object[count];
            for (int i = 0; i < count; i++) {
                // 基本类型：8种
                if (parameterTypes[i].isPrimitive()) {
                    if (parameterTypes[i].getTypeName().equals("int")) {
                        parameterValues[i] = Integer.parseInt(request.getParameter(parameterNameList.get(i)));
                    } else if (parameterTypes[i].getTypeName().equals("float")) {
                        parameterValues[i] = Float.parseFloat(request.getParameter(parameterNameList.get(i)));
                    }else if (parameterTypes[i].getTypeName().equals("boolean")) {
                        parameterValues[i] = Boolean.parseBoolean(request.getParameter(parameterNameList.get(i)));
                    }
                } else if (ClassUtils.isAssignable(parameterTypes[i], String.class)) {
                    parameterValues[i] = request.getParameter(parameterNameList.get(i));
                } else if (ClassUtils.isAssignable(parameterTypes[i], HttpServletResponse.class)) {
                    parameterValues[i] = response;
                }
                else if (ClassUtils.isAssignable(parameterTypes[i], HttpServletRequest.class)) {
                    parameterValues[i] = request;
                } else if (ClassUtils.isAssignable(parameterTypes[i], HttpSession.class)) {
                    parameterValues[i] = request.getSession();
                } else {
                    // JSON类型参数映射到Bean
                    InputStreamReader isr = new InputStreamReader(request.getInputStream(), "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .setPrettyPrinting()
                            .create();
                    Object pojo = gson.fromJson(sb.toString(), parameterTypes[i]);
                    parameterValues[i] = pojo;
                }
                /*
                try {
                    Object vo = parameterType[i].getDeclaredConstructor().newInstance();
                    BeanUtils.populate(vo, request.getParameterMap());
                    parameterValues[i] = vo;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                 */
            }
            Arrays.stream(parameterValues).forEach(value -> {
                System.out.println("遍历显示参数的值：" + value);
            });
            // 反射创建Controller对象
//             obj = Class.forName(mapping.getControllerClass().getName()).getDeclaredConstructor().newInstance();

            String controllerClassName = mapping.getControllerClass().getSimpleName();
            String id = StringUtils.replaceChars(controllerClassName, controllerClassName.substring(0, 1), controllerClassName.substring(0, 1).toLowerCase());

            // 根据id，从IoC容器获取Controller对象（该对象被注入了userService）
            obj = BeanFactory.getInstance().getBean(id);
            // 反射调用方法，必须处理返回值
            Object returnValue = method.invoke(obj, parameterValues);

            // 前后端没有分离的时代
            if (returnValue != null && returnValue instanceof String) {
                String retValue = (String) returnValue;
                // 重定向
                if (retValue.startsWith("redirect:")) {
                    int index = retValue.indexOf(":");
                    response.sendRedirect(retValue.substring(index + 1));
                } else {
                    // 请求转发
                    request.getRequestDispatcher(returnValue + ".jsp")
                            .forward(request, response);
                }
            } else {
                // 前后端分离，返回JSON
                response.setContentType("application/json; charset=UTF-8");
                String json = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .setPrettyPrinting()
                        .create()
                        .toJson(returnValue);
                PrintWriter out = response.getWriter();
                out.write(json);
                out.flush();
                out.close();
            }
            /*
            getParameter，参数名称不能固定，那么如何拿到参数名？
            1、JDK8（必须配置JDK8的编译选项，有局限性，放弃这种方案）
            2、javassist
             */

        } catch (NotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
