package cn.edu.guet.controller;

import cn.edu.guet.bean.News;
import cn.edu.guet.http.HttpResult;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.proxy.TransactionHandler;
import cn.edu.guet.service.NewsService;
import cn.edu.guet.vo.Data;
import cn.edu.guet.vo.WangEditorVo;
import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

@Controller // 添加Controller注解
public class NewsController {
    private NewsService newsService;


    public void setNewsService(NewsService newsService) {
        this.newsService = (NewsService )new TransactionHandler(newsService).getProxy();
    }

    @RequestMapping(value = "/getAllNews")
    public HttpResult getAllNews() throws SQLException {
        List<News> newsList = newsService.getAllNews();
        return HttpResult.ok(newsList);
    }
    @RequestMapping(value = "/saveNews")
    public HttpResult saveNews(HttpServletRequest request) {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            reader.lines().forEach(sb::append);
            return newsService.saveNews(sb.toString());
        } catch (Exception e) {
            return HttpResult.error(500, "新闻保存失败: " + e.getMessage());
        }
    }
    @RequestMapping(value = "/upload")
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        String dir = System.getProperty("user.dir");
        System.out.println("我的路径：" + dir);
        dir = dir.substring(0, dir.lastIndexOf("\\"));
        String uri = request.getRequestURI();
        int index = uri.lastIndexOf("\\");
        uri = uri.substring(index + 1);
        String realPath = dir + "\\webapps\\upload";
        System.out.println("实际路径：" + realPath);
        // 检查输入请求是否为multipart表单数据。
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart == true) {
            // 为该请求创建一个DiskFileItemFactory对象，通过它来解析请求。执行解析后，所有的表单项目都保存在一个List中。
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = null;
            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            Iterator<FileItem> itr = items.iterator();
            // String filePath=System.getProperty("user.dir")+ File.separator;
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                // 检查当前项目是普通表单项目还是上传文件。
                // 如果是普通表单项目，显示表单内容。
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String value = item.getString();
                    if (fieldName.equals("id")) {

                    } else if (fieldName.equals("title")) {

                    }
                } else {// 如果是上传文件，显示文件名。
                    File fullFile = new File(item.getName());
                    File savedFile = new File(realPath + "\\", fullFile.getName());
                    try {
                        item.write(savedFile);// 保存文件到指定目录：upload
                        response.setContentType("application/json;utf-8");
                        PrintWriter out = response.getWriter();
                        String url = "http://localhost:8080/upload/" + fullFile.getName();
                        Data data = new Data();
                        data.setUrl(url);
                        WangEditorVo wangEditorVo = new WangEditorVo();
                        wangEditorVo.setErrno(0);
                        wangEditorVo.setData(data);
                        System.out.println(JSON.toJSONString(wangEditorVo));
                        out.print(JSON.toJSONString(wangEditorVo));
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.print("the enctype must be multipart/form-data");
        }
    }

}

