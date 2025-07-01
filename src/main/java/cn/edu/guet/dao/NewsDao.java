package cn.edu.guet.dao;

import cn.edu.guet.bean.News;

import java.sql.SQLException;
import java.util.List;

public interface NewsDao {
    void save(News news) throws SQLException;
    //public List<News> getNewsByUsername() ;

    List<News> getAllNews() throws SQLException;

   // List<News> getNewsByUsername();
}
