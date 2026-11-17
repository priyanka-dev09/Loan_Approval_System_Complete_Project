package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.IOException;import java.sql.*;
@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"LOAN_OFFICER","ADMIN"))return;
  try(Connection c=DB.get();PreparedStatement p=c.prepareStatement("SELECT a.*,u.name applicant_name,u.email,p.name product_name FROM loan_application a JOIN users u ON u.user_id=a.user_id JOIN loan_product p ON p.product_id=a.product_id WHERE a.application_id=?")){
   p.setLong(1,Long.parseLong(r.getParameter("id")));try(ResultSet rs=p.executeQuery()){if(!rs.next()){s.sendError(404);return;}java.util.Map<String,Object> m=new java.util.LinkedHashMap<>();ResultSetMetaData md=rs.getMetaData();for(int i=1;i<=md.getColumnCount();i++)m.put(md.getColumnLabel(i),rs.getObject(i));r.setAttribute("app",m);}
   try(PreparedStatement d=c.prepareStatement("SELECT * FROM application_document WHERE application_id=?")){d.setLong(1,Long.parseLong(r.getParameter("id")));try(ResultSet rs=d.executeQuery()){r.setAttribute("docs",DashboardServlet.rsToList(rs));}}
   r.getRequestDispatcher("/WEB-INF/views/review.jsp").forward(r,s);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
