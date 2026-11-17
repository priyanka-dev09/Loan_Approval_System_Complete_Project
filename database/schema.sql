CREATE DATABASE IF NOT EXISTS loan_approval_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE loan_approval_system;

CREATE TABLE users (
 user_id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(120) NOT NULL,
 email VARCHAR(160) NOT NULL UNIQUE, mobile VARCHAR(20), password_hash VARCHAR(300) NOT NULL,
 role ENUM('APPLICANT','LOAN_OFFICER','ADMIN') NOT NULL DEFAULT 'APPLICANT',
 active BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE applicant_profile (
 profile_id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL UNIQUE,
 dob DATE, address VARCHAR(500), employment_type VARCHAR(60), monthly_income DECIMAL(12,2) DEFAULT 0,
 existing_emi DECIMAL(12,2) DEFAULT 0,
 CONSTRAINT fk_profile_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE loan_product (
 product_id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(80) NOT NULL,
 interest_rate DECIMAL(5,2) NOT NULL, min_amount DECIMAL(12,2) NOT NULL,
 max_amount DECIMAL(12,2) NOT NULL, max_tenure_months INT NOT NULL, active BOOLEAN DEFAULT TRUE
);
CREATE TABLE loan_application (
 application_id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, product_id BIGINT NOT NULL,
 amount DECIMAL(12,2) NOT NULL, tenure_months INT NOT NULL, purpose VARCHAR(300),
 emi DECIMAL(12,2), eligibility_score INT, status VARCHAR(40) NOT NULL DEFAULT 'SUBMITTED',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(user_id) REFERENCES users(user_id), FOREIGN KEY(product_id) REFERENCES loan_product(product_id)
);
CREATE TABLE application_document (
 document_id BIGINT PRIMARY KEY AUTO_INCREMENT, application_id BIGINT NOT NULL,
 document_type VARCHAR(80) NOT NULL, original_name VARCHAR(255) NOT NULL, stored_name VARCHAR(255) NOT NULL,
 uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, verified BOOLEAN DEFAULT FALSE,
 FOREIGN KEY(application_id) REFERENCES loan_application(application_id) ON DELETE CASCADE
);
CREATE TABLE decision (
 decision_id BIGINT PRIMARY KEY AUTO_INCREMENT, application_id BIGINT NOT NULL, officer_id BIGINT NOT NULL,
 decision_type VARCHAR(30) NOT NULL, reason_code VARCHAR(80) NOT NULL, remarks VARCHAR(1000),
 decision_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(application_id) REFERENCES loan_application(application_id),
 FOREIGN KEY(officer_id) REFERENCES users(user_id)
);
CREATE TABLE application_timeline (
 entry_id BIGINT PRIMARY KEY AUTO_INCREMENT, application_id BIGINT NOT NULL,
 action VARCHAR(120) NOT NULL, performed_by BIGINT, details VARCHAR(1000),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(application_id) REFERENCES loan_application(application_id) ON DELETE CASCADE,
 FOREIGN KEY(performed_by) REFERENCES users(user_id)
);
INSERT INTO loan_product(name,interest_rate,min_amount,max_amount,max_tenure_months) VALUES
 ('Personal Loan',12.50,10000,500000,60),
 ('Education Loan',8.50,25000,1500000,120),
 ('Vehicle Loan',9.25,50000,2000000,84),
 ('Home Loan',8.75,200000,5000000,240);
