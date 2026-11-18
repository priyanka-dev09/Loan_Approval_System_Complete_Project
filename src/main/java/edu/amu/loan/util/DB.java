
package edu.amu.loan.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public final class DB {

 private static final Properties P = new Properties();

 static {
  try (InputStream in = DB.class.getClassLoader()
          .getResourceAsStream("db.properties")) {

   if (in != null) {
    P.load(in);
   }

   Class.forName("com.mysql.cj.jdbc.Driver");

  } catch (Exception e) {
   throw new ExceptionInInitializerError(e);
  }
 }

 private DB() {}

 private static String config(String env, String property) {
  String value = System.getenv(env);

  if (value == null || value.isBlank()) {
   value = P.getProperty(property);
  }

  return value;
 }

 public static Connection get() throws SQLException {

  String host = config("DB_HOST", "db.host");
  String port = config("DB_PORT", "db.port");
  String name = config("DB_NAME", "db.name");
  String user = config("DB_USER", "db.username");
  String password = config("DB_PASSWORD", "db.password");

  if (host == null || port == null || name == null
          || user == null || password == null) {
   throw new SQLException("Database configuration is incomplete");
  }

  String url = "jdbc:mysql://" + host + ":" + port + "/" + name
          + "?sslMode=REQUIRED";

  return DriverManager.getConnection(url, user, password);
 }

 public static String uploadDir() {
  return P.getProperty("upload.directory", "./private-uploads");
 }
}

