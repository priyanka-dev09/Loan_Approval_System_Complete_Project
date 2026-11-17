package edu.amu.loan.util;
import jakarta.servlet.http.*;
public final class Auth {
 private Auth(){}
 public static Long id(HttpServletRequest r){ Object v=r.getSession(false)==null?null:r.getSession(false).getAttribute("userId"); return v instanceof Long?(Long)v:null; }
 public static String role(HttpServletRequest r){ Object v=r.getSession(false)==null?null:r.getSession(false).getAttribute("role"); return v==null?null:v.toString(); }
 public static boolean require(HttpServletRequest r,HttpServletResponse s,String... roles) throws java.io.IOException {
  String role=role(r); if(role==null){s.sendRedirect(r.getContextPath()+"/login");return false;}
  for(String x:roles) if(x.equals(role)) return true;
  s.sendError(403,"You do not have permission to access this page."); return false;
 }
}
