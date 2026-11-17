package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.IOException;import java.sql.*;
@WebServlet("/decision")
public class DecisionServlet extends HttpServlet {
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"LOAN_OFFICER","ADMIN"))return;
  try(Connection c=DB.get()){
   long id=Long.parseLong(r.getParameter("applicationId"));String decision=r.getParameter("decision");String reason=r.getParameter("reasonCode");String remarks=r.getParameter("remarks");
   if(!java.util.Set.of("APPROVED","REJECTED","CLARIFICATION_REQUESTED").contains(decision)||reason==null||reason.isBlank()){s.sendError(400,"Select a valid decision and reason.");return;}
   String status=decision.equals("APPROVED")?"APPROVED":decision.equals("REJECTED")?"REJECTED":"CLARIFICATION_REQUESTED";
   try(PreparedStatement p=c.prepareStatement("UPDATE loan_application SET status=? WHERE application_id=? AND status IN ('SUBMITTED','UNDER_REVIEW','DOCUMENTS_PENDING','CLARIFICATION_REQUESTED')")){p.setString(1,status);p.setLong(2,id);if(p.executeUpdate()==0){s.sendError(409,"Application cannot be decided in its current status.");return;}}
   try(PreparedStatement p=c.prepareStatement("INSERT INTO decision(application_id,officer_id,decision_type,reason_code,remarks) VALUES(?,?,?,?,?)")){p.setLong(1,id);p.setLong(2,Auth.id(r));p.setString(3,decision);p.setString(4,reason);p.setString(5,remarks);p.executeUpdate();}
   try(PreparedStatement p=c.prepareStatement("INSERT INTO application_timeline(application_id,action,performed_by,details) VALUES(?,?,?,?)")){p.setLong(1,id);p.setString(2,status);p.setLong(3,Auth.id(r));p.setString(4,reason+": "+remarks);p.executeUpdate();}
   s.sendRedirect(r.getContextPath()+"/dashboard");
  }catch(SQLException e){throw new ServletException(e);}
 }
}
