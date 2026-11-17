package edu.amu.loan.servlet;
import edu.amu.loan.util.*; import jakarta.servlet.*; import jakarta.servlet.http.*; import jakarta.servlet.annotation.*; import java.io.IOException; import java.sql.*;
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT","LOAN_OFFICER","ADMIN"))return;
  String role=Auth.role(r);
  try(Connection c=DB.get()){
   if("APPLICANT".equals(role)){
    try(PreparedStatement p=c.prepareStatement("SELECT a.*,p.name product_name FROM loan_application a JOIN loan_product p ON p.product_id=a.product_id WHERE a.user_id=? ORDER BY a.created_at DESC")){
     p.setLong(1,Auth.id(r));try(ResultSet rs=p.executeQuery()){r.setAttribute("applications",rsToList(rs));}
    }
    r.getRequestDispatcher("/WEB-INF/views/applicant-dashboard.jsp").forward(r,s);
   } else if("LOAN_OFFICER".equals(role)){
    try(PreparedStatement p=c.prepareStatement("SELECT a.*,u.name applicant_name,p.name product_name FROM loan_application a JOIN users u ON u.user_id=a.user_id JOIN loan_product p ON p.product_id=a.product_id WHERE a.status IN ('SUBMITTED','UNDER_REVIEW','DOCUMENTS_PENDING','CLARIFICATION_REQUESTED') ORDER BY a.created_at")){
     try(ResultSet rs=p.executeQuery()){r.setAttribute("applications",rsToList(rs));}
    }r.getRequestDispatcher("/WEB-INF/views/officer-dashboard.jsp").forward(r,s);
   } else {
    try(Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT status,COUNT(*) total FROM loan_application GROUP BY status")){r.setAttribute("stats",rsToList(rs));}
    try(Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT user_id,name,email,role,active FROM users ORDER BY created_at DESC")){r.setAttribute("users",rsToList(rs));}
    try(Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT * FROM loan_product ORDER BY product_id")){r.setAttribute("products",rsToList(rs));}
    r.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(r,s);
   }
  }catch(SQLException e){throw new ServletException(e);}
 }
 static java.util.List<java.util.Map<String,Object>> rsToList(ResultSet rs)throws SQLException{
  java.util.List<java.util.Map<String,Object>> out=new java.util.ArrayList<>();ResultSetMetaData m=rs.getMetaData();
  while(rs.next()){java.util.Map<String,Object>x=new java.util.LinkedHashMap<>();for(int i=1;i<=m.getColumnCount();i++)x.put(m.getColumnLabel(i),rs.getObject(i));out.add(x);}return out;
 }
}
