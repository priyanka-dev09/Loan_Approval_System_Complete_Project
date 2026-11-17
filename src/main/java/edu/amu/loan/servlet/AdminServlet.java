package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.IOException;import java.sql.*;
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"ADMIN"))return;
  try(Connection c=DB.get()){
   String action=r.getParameter("action");
   if("createOfficer".equals(action)){
    try(PreparedStatement p=c.prepareStatement("INSERT INTO users(name,email,mobile,password_hash,role) VALUES(?,?,?,?, 'LOAN_OFFICER')")){
     p.setString(1,r.getParameter("name"));p.setString(2,r.getParameter("email").toLowerCase());p.setString(3,r.getParameter("mobile"));p.setString(4,PasswordUtil.hash(r.getParameter("password")));p.executeUpdate();
    }
   } else if("addProduct".equals(action)){
    try(PreparedStatement p=c.prepareStatement("INSERT INTO loan_product(name,interest_rate,min_amount,max_amount,max_tenure_months) VALUES(?,?,?,?,?)")){
     p.setString(1,r.getParameter("name"));p.setBigDecimal(2,new java.math.BigDecimal(r.getParameter("rate")));p.setBigDecimal(3,new java.math.BigDecimal(r.getParameter("minAmount")));p.setBigDecimal(4,new java.math.BigDecimal(r.getParameter("maxAmount")));p.setInt(5,Integer.parseInt(r.getParameter("maxTenure")));p.executeUpdate();
    }
   } else if("toggleUser".equals(action)){
    try(PreparedStatement p=c.prepareStatement("UPDATE users SET active=NOT active WHERE user_id=? AND role<>'ADMIN'")){p.setLong(1,Long.parseLong(r.getParameter("userId")));p.executeUpdate();}
   }
   s.sendRedirect(r.getContextPath()+"/dashboard");
  }catch(SQLException e){throw new ServletException(e);}
 }
}
