package cn.edu.guet.service;

import cn.edu.guet.bean.News;
import cn.edu.guet.http.HttpResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface NewsService {
    HttpResult saveNews(String newsContent) throws IOException, SQLException;
    //List<News> getNewsByUsername();
    List<News> getAllNews() throws SQLException;
}
