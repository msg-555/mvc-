package cn.edu.guet.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

public class ConfigReader {

    public static String getClassName() {
        SAXReader saxReader = new SAXReader();
        InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.xml");
        try {
            Document document = saxReader.read(inputStream);
            Element element = (Element) document.selectObject("/daoConfig/dao-class");
            return element.getStringValue();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
