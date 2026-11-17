package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.IOException;import java.sql.*;
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT"))return;
  try(Connection c=DB.get();PreparedStatement p=c.prepareStatement("SELECT u.name,u.email,u.mobile,ap.dob,ap.address,ap.employment_type,ap.monthly_income,ap.existing_emi FROM users u LEFT JOIN applicant_profile ap ON ap.user_id=u.user_id WHERE u.user_id=?")){
   p.setLong(1,Auth.id(r));try(ResultSet rs=p.executeQuery()){if(rs.next()){java.util.Map<String,Object> m=new java.util.LinkedHashMap<>();ResultSetMetaData md=rs.getMetaData();for(int i=1;i<=md.getColumnCount();i++)m.put(md.getColumnLabel(i),rs.getObject(i));r.setAttribute("profile",m);}}
   r.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(r,s);
  }catch(SQLException e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT"))return;
  try(Connection c=DB.get()){
   try(PreparedStatement p=c.prepareStatement("UPDATE users SET name=?,mobile=? WHERE user_id=?")){p.setString(1,r.getParameter("name"));p.setString(2,r.getParameter("mobile"));p.setLong(3,Auth.id(r));p.executeUpdate();}
   try(PreparedStatement p=c.prepareStatement("UPDATE applicant_profile SET dob=?,address=?,employment_type=?,monthly_income=?,existing_emi=? WHERE user_id=?")){
    String dob=r.getParameter("dob");if(dob==null||dob.isBlank())p.setNull(1,Types.DATE);else p.setDate(1,Date.valueOf(dob));
    p.setString(2,r.getParameter("address"));p.setString(3,r.getParameter("employmentType"));
    p.setBigDecimal(4,new java.math.BigDecimal(r.getParameter("monthlyIncome")));
    p.setBigDecimal(5,new java.math.BigDecimal(r.getParameter("existingEmi")));p.setLong(6,Auth.id(r));p.executeUpdate();
   }
   s.sendRedirect(r.getContextPath()+"/profile?saved=1");
  }catch(Exception e){throw new ServletException(e);}
 }
}
