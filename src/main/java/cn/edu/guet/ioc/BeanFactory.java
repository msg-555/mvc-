package cn.edu.guet.ioc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象工厂：可以创建多个产品系列
 *
 * @Author liwei
 * @Date 2023/9/6 09:52
 * @Version 1.0
 */
public class BeanFactory {

    // Map就是IoC容器
    public static Map<String, Object> map = new HashMap<String, Object>();

    public static void parseElement(Element ele) {
        try {
            // 通过反射创建对象
            Object beanObj = null;
            Class clazz = null;
            String id = ele.attributeValue("id");
            if (map.get(id) == null) {
                clazz = Class.forName(ele.attributeValue("class"));
                beanObj = clazz.getDeclaredConstructor().newInstance();
                map.put(id, beanObj);
            }
            // 创建依赖对象
            List<Element> childElements = ele.elements();
            Class finalClazz = clazz;
            Object finalBeanObj = beanObj;
            childElements.forEach(childElement -> {
                /*
                如何知道子元素是否已经创建？
                 */
                Object obj;
                String ref = childElement.attributeValue("ref");
                if (map.get(ref) == null) {
                    for (Element element : list) {
                        String ids = element.attributeValue("id");
                        if (ids.equals(ref)) {
                            // 递归处理
                            parseElement(element);
                        }
                    }
                }
                // 注入依赖（调用set方法）
                obj = map.get(ref);
                if (finalClazz != null) {
                    Method methods[] = finalClazz.getDeclaredMethods();
                    for (Method m : methods) {
                        if (m.getName().startsWith("set") && m.getName().toLowerCase().contains(ref.toLowerCase())) {
                            // 反射调用类的setXXX方法实现bean的自动注入
                            try {
                                m.invoke(finalBeanObj, obj);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<Element> list;

    static {
        try {
            System.out.println("读取applicationContext.xml");
            SAXReader reader = new SAXReader();
            InputStream in = Class.forName("cn.edu.guet.ioc.BeanFactory")
                    .getResourceAsStream("/applicationContext.xml");
            Document doc = reader.read(in);
            // xPathExpression：xPath表达式
            list = doc.selectNodes("/beans/bean");
            list.forEach(element -> {
                parseElement(element);
            });
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    // 工厂很大：人力、物力、财力（说明建造一个工程很耗资源），程序中的工厂也是一样，只有一个工厂实例（单例）

    // 单例模式：饿汉模式

    // 声明一个公共静态的类的实例
    public static BeanFactory instance = new BeanFactory();

    // 私有的类的构造方法
    private BeanFactory() {
    }

    // 公共的获取类的实例的方法
    public static BeanFactory getInstance() {
        return instance;
    }

    public Object getBean(String id) {
        return map.get(id);
    }
}
