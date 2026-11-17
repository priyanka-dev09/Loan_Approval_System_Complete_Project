package edu.amu.loan.util;
import java.sql.*;
/** Run once from the IDE after configuring db.properties. Demo credentials are for local academic use only. */
public class AdminSeeder {
 public static void main(String[] args) throws Exception {
  try(Connection c=DB.get(); PreparedStatement q=c.prepareStatement("SELECT user_id FROM users WHERE email=?")){
   q.setString(1,"admin@loan.local"); try(ResultSet rs=q.executeQuery()){if(rs.next()){System.out.println("Admin already exists.");return;}}
  }
  try(Connection c=DB.get(); PreparedStatement p=c.prepareStatement("INSERT INTO users(name,email,mobile,password_hash,role) VALUES(?,?,?,?, 'ADMIN')")){
   p.setString(1,"System Administrator");p.setString(2,"admin@loan.local");p.setString(3,"9999999999");p.setString(4,PasswordUtil.hash("Admin@123"));p.executeUpdate();
  }
  System.out.println("Created admin@loan.local / Admin@123. Change password immediately.");
 }
}
