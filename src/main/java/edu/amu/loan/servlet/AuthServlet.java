package edu.amu.loan.servlet;
import edu.amu.loan.util.*; import jakarta.servlet.*; import jakarta.servlet.http.*; import jakarta.servlet.annotation.*;
import java.io.IOException; import java.sql.*;
@WebServlet(urlPatterns={"/login","/register","/logout"})
public class AuthServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  String p=r.getServletPath(); if(p.equals("/logout")){if(r.getSession(false)!=null)r.getSession().invalidate();s.sendRedirect(r.getContextPath()+"/login");return;}
  r.getRequestDispatcher(p.equals("/register")?"/WEB-INF/views/register.jsp":"/WEB-INF/views/login.jsp").forward(r,s);
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  r.setCharacterEncoding("UTF-8"); String path=r.getServletPath();
  try(Connection c=DB.get()){
   if(path.equals("/register")){
    String name=r.getParameter("name"),email=r.getParameter("email"),mobile=r.getParameter("mobile"),pw=r.getParameter("password");
    if(name==null||email==null||pw==null||pw.length()<8){r.setAttribute("error","Enter all required details. Password must be at least 8 characters.");r.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(r,s);return;}
    try(PreparedStatement p=c.prepareStatement("INSERT INTO users(name,email,mobile,password_hash,role) VALUES(?,?,?,?, 'APPLICANT')",Statement.RETURN_GENERATED_KEYS)){
     p.setString(1,name.trim());p.setString(2,email.trim().toLowerCase());p.setString(3,mobile);p.setString(4,PasswordUtil.hash(pw));p.executeUpdate();
     try(ResultSet k=p.getGeneratedKeys()){k.next();try(PreparedStatement prof=c.prepareStatement("INSERT INTO applicant_profile(user_id) VALUES(?)")){prof.setLong(1,k.getLong(1));prof.executeUpdate();}}
    }
    s.sendRedirect(r.getContextPath()+"/login?registered=1"); return;
   }
   String email=r.getParameter("email"),pw=r.getParameter("password");
   try(PreparedStatement p=c.prepareStatement("SELECT user_id,name,password_hash,role,active FROM users WHERE email=?")){
    p.setString(1,email==null?"":email.trim().toLowerCase());try(ResultSet rs=p.executeQuery()){
     if(rs.next()&&rs.getBoolean("active")&&PasswordUtil.verify(pw==null?"":pw,rs.getString("password_hash"))){
      HttpSession old=r.getSession(false);if(old!=null)old.invalidate();HttpSession ses=r.getSession(true);
      ses.setAttribute("userId",rs.getLong("user_id"));ses.setAttribute("name",rs.getString("name"));ses.setAttribute("role",rs.getString("role"));
      s.sendRedirect(r.getContextPath()+"/dashboard");return;
     }
    }
   }
   r.setAttribute("error","Invalid email or password.");r.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(r,s);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
