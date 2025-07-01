package cn.edu.guet.dao.impl;

import cn.edu.guet.bean.News;
import cn.edu.guet.dao.NewsDao;
import cn.edu.guet.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {

    @Override
    public void save(News news) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO news_yzh VALUES(?,?,?,?,?,?,?,?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, news.getId());
        pstmt.setString(2, news.getTitle());
        pstmt.setString(3, news.getAuthor());
        pstmt.setDate(4, news.getPubTime());
        pstmt.setInt(5, news.getClickCount());
        pstmt.setString(6, news.getContent());
        pstmt.setString(7, news.getProvider());
        pstmt.setString(8, news.getReviewer());
        pstmt.setString(9,news.getType());
        pstmt.executeUpdate();
    }

    @Override
    public List<News> getAllNews() throws SQLException {

        Connection conn = DBUtil.getConnection();
        List<News> newsList = new ArrayList<>();

        String sql = "SELECT  * FROM news_yzh";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        try {
            rs = pstmt.executeQuery();
            while (rs.next()) {
                News news = new News();
                news.setId(rs.getString("id"));
                news.setTitle(rs.getString("title"));
                news.setAuthor(rs.getString("author"));
                news.setPubTime(rs.getDate("pubtime"));
                news.setClickCount(rs.getInt("clickcount"));
                news.setContent(rs.getString("content"));
                news.setProvider(rs.getString("provider"));
                news.setReviewer(rs.getString("reviewer"));
                news.setType(rs.getString("type"));
                newsList.add(news);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("新闻列表大小: " + newsList.size());
        for (News news : newsList) {
            System.out.println(news.getTitle());
        }
        return newsList;
        //return List.of();
    }

}


