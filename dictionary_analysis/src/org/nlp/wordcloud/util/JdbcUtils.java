package org.nlp.wordcloud.util;

//import java.lang.reflect.Field;  
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  
import java.sql.ResultSetMetaData;  
import java.sql.SQLException;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;
 
import org.nlp.wordcloud.fileanalysis.IndustryCode;
import org.nlp.wordcloud.fileanalysis.Word;
import org.nlp.wordcloud.fileanalysis.WordIndex;
import org.nlp.wordcloud.fileanalysis.WordNotes;

public class JdbcUtils {
	//数据库用户名  
    private static  String USERNAME = "root";  
    //数据库密码  
    private static  String PASSWORD = "root";  
    //驱动信息   
    private static  final String DRIVER = "com.mysql.jdbc.Driver";   
    //数据库地址  
    private static  String URL = "jdbc:mysql://10.71.14.232:3306/WORD_CLOUD";  
    //每批次个数
    private static int BATCH_SIZE = 1000;
    
    
     
    private static JdbcUtils instance = null;   
    
    
    private static Connection conn;  
    private PreparedStatement pstmt;  
    private ResultSet rs; 
    
    public static JdbcUtils getInstance(){
    	if (null == instance) {
			instance = new JdbcUtils();
		}
    	if (null == conn) {
    		init(false);
		}
    	return instance;
    }     
    
    private JdbcUtils(){
    	init(false);
    }
    
    private static void init(boolean bAutoCommit) {    	
        try {  
        	Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD); 
            conn.setAutoCommit(bAutoCommit);
        } catch (ClassNotFoundException e) { 
            e.printStackTrace();  
        } catch (SQLException e) { 
            e.printStackTrace();  
        }  
    }
    
    
    public JdbcUtils(String url ,String userName, String passWord,boolean bAutoCommit) {
    	URL = url;
    	USERNAME = userName;
    	PASSWORD = passWord;    	
        try {  
        	Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(bAutoCommit);
        } catch (ClassNotFoundException e) { 
            e.printStackTrace();  
        } catch (SQLException e) { 
            e.printStackTrace();  
        }  
    }
    
    /** 
     * 获得数据库的连接 
     * @return 
     */  
    public Connection getConnection(){ 
    	if (null ==  conn) {
			init(false);
		}
        return conn;  
    }  
    
     /** 
     * commit
     * @return 
     * @throws SQLException 
     */  
    public void commit() throws SQLException{  
    	conn.commit(); 
    }  
    
    /** 
     * 设置数据库的连接 
     * @return 
     */  
    public void SetConnection(Connection c){ 
        conn = c;
    }  
    @Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub    	
    	if(null != rs){
    		rs.close();
    	}
    	
    	if (null != pstmt){
			pstmt.close();
		}
    	
    	if(null != conn){
    		conn.close();
    	} 
    	
		super.finalize();
	}
      
    /** 
     * 增加、删除、改 
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public boolean updateByPreparedStatement(String sql, List<Object>params)throws SQLException{  
        boolean flag = false;  
        int result = -1;  
        pstmt = conn.prepareStatement(sql);  
        int index = 1;  
        if(params != null && !params.isEmpty()){  
            for(int i=0; i<params.size(); i++){  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        result = pstmt.executeUpdate();  
        flag = result > 0 ? true : false;  
        return flag;  
    }  
  
    /** 
     * 查询单条记录 
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException{  
        Map<String, Object> map = new HashMap<String, Object>();  
        int index  = 1;  
        pstmt = conn.prepareStatement(sql);  
        if(params != null && !params.isEmpty()){  
            for(int i=0; i<params.size(); i++){  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        rs = pstmt.executeQuery();//返回查询结果  
        
        ResultSetMetaData metaData = rs.getMetaData();  
        int col_len = metaData.getColumnCount();  
        while(rs.next()){  
            for(int i=0; i<col_len; i++ ){  
                String cols_name = metaData.getColumnName(i+1);  
                Object cols_value = rs.getObject(cols_name);  
                if(cols_value == null){  
                    cols_value = "";  
                }  
                map.put(cols_name, cols_value);  
            }  
        }  
        return map;  
    }  
  
    /**查询多条记录 
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws SQLException{  
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
        int index = 1;  
        pstmt = conn.prepareStatement(sql);  
        if(params != null && !params.isEmpty()){  
            for(int i = 0; i<params.size(); i++){  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        rs = pstmt.executeQuery();  
        ResultSetMetaData metaData = rs.getMetaData();  
        int cols_len = metaData.getColumnCount();  
        while(rs.next()){  
            Map<String, Object> map = new HashMap<String, Object>();  
            for(int i=0; i<cols_len; i++){  
                String cols_name = metaData.getColumnName(i+1);  
                Object cols_value = rs.getObject(cols_name);  
                if(cols_value == null){  
                    cols_value = "";  
                }  
                map.put(cols_name, cols_value);  
            }  
            list.add(map);  
        }  
  
        return list;  
    }
    
    /*
     * 批量插入
     */ 
    public void executeBatch(Word data,List<Word> listData){ 
    	try {			
            conn = this.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(data.GetSql());
            for (int i = 0; i < listData.size(); i += BATCH_SIZE) {
                pstmt.clearBatch(); 
                for (int j = 0; j < BATCH_SIZE && (i+j)<listData.size(); j++) {
                	Word temp = listData.get(i+j); 
                	temp.BindInfo(pstmt);
                    pstmt.addBatch(); 

                }
                pstmt.executeBatch(); 
                conn.commit(); 
            }
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace(); 
		}  
    }

    public void executeBatch(WordIndex data,List<WordIndex> listData){ 
    	try {			
            conn = this.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(data.GetSql());
            for (int i = 0; i < listData.size(); i += BATCH_SIZE) {
                pstmt.clearBatch(); 
                for (int j = 0; j < BATCH_SIZE && (i+j)<listData.size(); j++) {
                	WordIndex temp = listData.get(i+j); 
                	temp.BindInfo(pstmt);
                    pstmt.addBatch(); 

                }
                pstmt.executeBatch(); 
                conn.commit(); 
            }
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace(); 
		}  
    }
    
    
    public void executeBatch(WordNotes data,List<WordNotes> listData){ 
    	try {			
            conn = this.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(data.GetSql());
            for (int i = 0; i < listData.size(); i += BATCH_SIZE) {
                pstmt.clearBatch(); 
                for (int j = 0; j < BATCH_SIZE && (i+j)<listData.size(); j++) {
                	WordNotes temp = listData.get(i+j); 
                	temp.BindInfo(pstmt);
                    pstmt.addBatch(); 
                }
                pstmt.executeBatch(); 
                conn.commit(); 
            }
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace(); 
		}  
    }
     
    //    
    public void executeBatch(IndustryCode data,List<IndustryCode> listData){ 
    	try {			
            conn = this.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(data.GetSql());
            for (int i = 0; i < listData.size(); i += BATCH_SIZE) {
                pstmt.clearBatch(); 
                for (int j = 0; j < BATCH_SIZE && (i+j)<listData.size(); j++) {
                	IndustryCode temp = listData.get(i+j); 
                	temp.BindInfo(pstmt);
                    pstmt.addBatch(); 

                }
                pstmt.executeBatch(); 
                conn.commit(); 
            }
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace(); 
		}  
    }
    /** 
     * 释放数据库连接 
     */  
    public void releaseConn(){  
        if(rs != null){  
            try{  
                rs.close();  
            }catch(SQLException e){  
                e.printStackTrace();  
            }  
        }  
    }  
}
