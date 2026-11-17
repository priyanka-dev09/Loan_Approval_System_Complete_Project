package edu.amu.loan.servlet;
import edu.amu.loan.util.*;import jakarta.servlet.*;import jakarta.servlet.http.*;import jakarta.servlet.annotation.*;import java.io.*;import java.nio.file.*;import java.sql.*;import java.util.UUID;
@WebServlet("/documents/upload") @MultipartConfig(maxFileSize=5*1024*1024,maxRequestSize=10*1024*1024)
public class DocumentServlet extends HttpServlet {
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
  if(!Auth.require(r,s,"APPLICANT"))return;
  try{
   long app=Long.parseLong(r.getParameter("applicationId"));String type=r.getParameter("documentType");Part part=r.getPart("file");
   String original=Paths.get(part.getSubmittedFileName()).getFileName().toString();String ext=original.toLowerCase();
   if(!(ext.endsWith(".pdf")||ext.endsWith(".jpg")||ext.endsWith(".jpeg")||ext.endsWith(".png"))||part.getSize()==0){s.sendError(400,"Upload a PDF, JPG or PNG file.");return;}
   try(Connection c=DB.get();PreparedStatement own=c.prepareStatement("SELECT application_id FROM loan_application WHERE application_id=? AND user_id=?")){
    own.setLong(1,app);own.setLong(2,Auth.id(r));try(ResultSet rs=own.executeQuery()){if(!rs.next()){s.sendError(403);return;}}
    Path dir=Paths.get(DB.uploadDir()).toAbsolutePath();Files.createDirectories(dir);String stored=UUID.randomUUID()+ext.substring(ext.lastIndexOf('.'));Files.copy(part.getInputStream(),dir.resolve(stored),StandardCopyOption.REPLACE_EXISTING);
    try(PreparedStatement p=c.prepareStatement("INSERT INTO application_document(application_id,document_type,original_name,stored_name) VALUES(?,?,?,?)")){p.setLong(1,app);p.setString(2,type);p.setString(3,original);p.setString(4,stored);p.executeUpdate();}
    try(PreparedStatement t=c.prepareStatement("INSERT INTO application_timeline(application_id,action,performed_by,details) VALUES(?,'DOCUMENT_UPLOADED',?,?)")){t.setLong(1,app);t.setLong(2,Auth.id(r));t.setString(3,type);t.executeUpdate();}
   }
   s.sendRedirect(r.getContextPath()+"/dashboard");
  }catch(SQLException e){throw new ServletException(e);}
 }
}
