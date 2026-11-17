# Loan Approval System (Web-based MCA Project)

A Java Servlet/JSP + JDBC + MySQL application following the supplied SRS. It uses MVC-style servlets (controllers), Java model/DAO classes, JSP views, and Bootstrap styling. It does **not** use Spring Boot or Swing.

## Features
- Applicant registration/login and role-based sessions
- Applicant profile management and loan application submission
- Loan products with interest rate, amount and tenure limits
- EMI and affordability calculation
- PDF/JPG/PNG document upload with required-document checklist
- Application status tracking, timeline and supporting document upload
- Loan Officer review, document verification, clarification, approve/reject with reason
- Admin dashboard, user/loan-product management
- Password hashing (PBKDF2), prepared SQL statements, session logout

## Requirements
- JDK 17+
- Apache Maven 3.9+
- MySQL 8+
- Apache Tomcat 10.1+
- IntelliJ IDEA or Eclipse

## Setup
1. Create database and tables: run `database/schema.sql` in MySQL Workbench.
2. Edit `src/main/resources/db.properties` with your MySQL username/password.
3. From project root run: `mvn clean package`.
4. Copy `target/loan-approval-system.war` to Tomcat `webapps/` and start Tomcat.
5. Open `http://localhost:8080/loan-approval-system/`.
6. Create the first ADMIN account by running `edu.amu.loan.util.AdminSeeder` once from IntelliJ (Run configuration). Change the seeded password immediately after first login. See the class comments.

## First admin
Run `AdminSeeder` as a Java application after configuring `db.properties`. It creates:
- Email: `admin@loan.local`
- Password: `Admin@123`

The seeder refuses to create a duplicate email. Change the password after logging in; never use this demo credential on a public server.

## Notes / academic scope
- Eligibility is a transparent **preliminary rule-based indicator**, not a credit bureau score or legally binding lending decision.
- Actual approval/rejection remains with an authorised Loan Officer.
- No real bank API, credit bureau integration, loan disbursement or repayment processing is included.
- Uploaded files are stored outside the public web root in the configured upload directory. Configure a production-grade private directory and HTTPS before deployment.
- This is an academic starter implementation. Review security, validation, backups and deployment settings before real-world use.
