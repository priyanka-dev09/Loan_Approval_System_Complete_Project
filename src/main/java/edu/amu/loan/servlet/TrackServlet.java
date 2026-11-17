package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.IOException;import java.sql.*;
@WebServlet("/track")
public class TrackServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT"))return;
  long id=Long.parseLong(r.getParameter("id"));
  try(Connection c=DB.get();PreparedStatement own=c.prepareStatement("SELECT application_id FROM loan_application WHERE application_id=? AND user_id=?")){
   own.setLong(1,id);own.setLong(2,Auth.id(r));try(ResultSet rs=own.executeQuery()){if(!rs.next()){s.sendError(404);return;}}
   try(PreparedStatement p=c.prepareStatement("SELECT action,details,created_at FROM application_timeline WHERE application_id=? ORDER BY created_at DESC")){p.setLong(1,id);try(ResultSet rs=p.executeQuery()){r.setAttribute("timeline",DashboardServlet.rsToList(rs));}}
   r.setAttribute("applicationId",id);r.getRequestDispatcher("/WEB-INF/views/track.jsp").forward(r,s);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
