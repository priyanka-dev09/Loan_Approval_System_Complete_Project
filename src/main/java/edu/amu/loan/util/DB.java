package edu.amu.loan.util;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
public final class DB {
 private static final Properties P = new Properties();
 static {
  try (InputStream in=DB.class.getClassLoader().getResourceAsStream("db.properties")) {
   if(in==null) throw new IllegalStateException("db.properties not found");
   P.load(in); Class.forName("com.mysql.cj.jdbc.Driver");
  } catch(Exception e){ throw new ExceptionInInitializerError(e); }
 }
 private DB(){}
 public static Connection get() throws SQLException { return DriverManager.getConnection(P.getProperty("db.url"),P.getProperty("db.username"),P.getProperty("db.password")); }
 public static String uploadDir(){ return P.getProperty("upload.directory","./private-uploads"); }
}
