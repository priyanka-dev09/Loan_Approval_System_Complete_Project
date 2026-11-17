package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.IOException;import java.sql.*;
@WebServlet("/loans")
public class LoanServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT"))return;
  try(Connection c=DB.get();Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT * FROM loan_product WHERE active=1 ORDER BY name")){
   r.setAttribute("products",DashboardServlet.rsToList(rs));r.getRequestDispatcher("/WEB-INF/views/apply.jsp").forward(r,s);
  }catch(SQLException e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT"))return;
  try(Connection c=DB.get()){
   long product=Long.parseLong(r.getParameter("productId"));double amount=Double.parseDouble(r.getParameter("amount"));int months=Integer.parseInt(r.getParameter("tenure"));
   String purpose=r.getParameter("purpose");
   try(PreparedStatement p=c.prepareStatement("SELECT * FROM loan_product WHERE product_id=? AND active=1")){p.setLong(1,product);try(ResultSet rs=p.executeQuery()){
    if(!rs.next()||amount<rs.getDouble("min_amount")||amount>rs.getDouble("max_amount")||months<1||months>rs.getInt("max_tenure_months")){r.setAttribute("error","Loan amount or tenure is outside the selected product limits.");doGet(r,s);return;}
    double rate=rs.getDouble("interest_rate")/1200.0;double emi=amount*rate*Math.pow(1+rate,months)/(Math.pow(1+rate,months)-1);
    int score=amount>0?Math.min(100,(int)(50+(months<=60?15:5)+(amount<500000?15:5))):0;
    try(PreparedStatement ins=c.prepareStatement("INSERT INTO loan_application(user_id,product_id,amount,tenure_months,purpose,emi,eligibility_score,status) VALUES(?,?,?,?,?,?,?,'SUBMITTED')",Statement.RETURN_GENERATED_KEYS)){
     ins.setLong(1,Auth.id(r));ins.setLong(2,product);ins.setDouble(3,amount);ins.setInt(4,months);ins.setString(5,purpose);ins.setDouble(6,emi);ins.setInt(7,score);ins.executeUpdate();
     try(ResultSet k=ins.getGeneratedKeys()){k.next();long id=k.getLong(1);try(PreparedStatement t=c.prepareStatement("INSERT INTO application_timeline(application_id,action,performed_by,details) VALUES(?,'SUBMITTED',?,'Application submitted by applicant')")){t.setLong(1,id);t.setLong(2,Auth.id(r));t.executeUpdate();}}
    }
   }}
   s.sendRedirect(r.getContextPath()+"/dashboard?submitted=1");
  }catch(Exception e){throw new ServletException(e);}
 }
}
