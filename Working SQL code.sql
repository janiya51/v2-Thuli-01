-- Create Database
CREATE DATABASE IF NOT EXISTS v3_01_thuli_life_insurance;
USE v3_01_thuli_life_insurance;

-- -----------------------------------------------------
-- 1. Roles Table
-- -----------------------------------------------------
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- -----------------------------------------------------
-- 2. Users Table (NOTE: Passwords are in plain text for testing)
-- -----------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE, -- New field for login
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    is_active TINYINT DEFAULT 1,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- -----------------------------------------------------
-- 3. Customer Details
-- -----------------------------------------------------
CREATE TABLE customer_details (
    customer_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    date_of_birth DATE,
    phone_number VARCHAR(20),
    address TEXT,
    is_pending_review TINYINT DEFAULT 0, -- Flag for CSE review of customer-initiated updates
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 4. Applications Table (Workflow Tracking)
-- -----------------------------------------------------
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    product_type VARCHAR(50),
    desired_coverage DECIMAL(15,2),
    current_status ENUM('Submitted', 'Incomplete', 'PendingSIA', 'Rejected', 'PendingFO', 'PendingCustomer', 'Accepted') NOT NULL DEFAULT 'Submitted',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 5. Risk Assessment Table (SIA's CRUD)
-- -----------------------------------------------------
CREATE TABLE risk_assessments (
    assessment_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    advisor_id INT NOT NULL, -- Senior Insurance Advisor
    risk_score INT,
    recommendation TEXT,
    assessment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(application_id),
    FOREIGN KEY (advisor_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 6. Policies Table (Active Contracts)
-- -----------------------------------------------------
CREATE TABLE policies (
    policy_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    policy_number VARCHAR(50) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    annual_premium DECIMAL(10,2) NOT NULL,
    policy_status ENUM('Active', 'Lapsed', 'Cancelled', 'Matured') DEFAULT 'Active',
    FOREIGN KEY (application_id) REFERENCES applications(application_id)
);

-- -----------------------------------------------------
-- 7. Beneficiaries Table (Customer's Update CRUD)
-- -----------------------------------------------------
CREATE TABLE beneficiaries (
    beneficiary_id INT AUTO_INCREMENT PRIMARY KEY,
    policy_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    relationship VARCHAR(50),
    share_percentage INT,
    FOREIGN KEY (policy_id) REFERENCES policies(policy_id)
);

-- -----------------------------------------------------
-- 8. Payments Table (FO's CRUD)
-- -----------------------------------------------------
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    policy_id INT NOT NULL,
    finance_officer_id INT NOT NULL,
    amount DECIMAL(10,2),
    payment_date DATE,
    type ENUM('Schedule', 'Received') NOT NULL,
    status ENUM('Due', 'Paid', 'Overdue', 'Unused') DEFAULT 'Due',
    FOREIGN KEY (policy_id) REFERENCES policies(policy_id),
    FOREIGN KEY (finance_officer_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 9. Claims Table (CSE/SIA/FO workflow)
-- -----------------------------------------------------
CREATE TABLE claims (
    claim_id INT AUTO_INCREMENT PRIMARY KEY,
    policy_id INT NOT NULL,
    filing_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    claim_type VARCHAR(50),
    claim_status ENUM('Filed', 'DocumentsRequired', 'PendingSIA', 'Approved', 'Rejected', 'Paid', 'Archived') DEFAULT 'Filed',
    handler_id INT, -- SIA currently handling
    payout_amount DECIMAL(15,2),
    FOREIGN KEY (policy_id) REFERENCES policies(policy_id),
    FOREIGN KEY (handler_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 10. Policy Disputes Table (Admin's CRUD)
-- -----------------------------------------------------
CREATE TABLE policy_disputes (
    dispute_id INT AUTO_INCREMENT PRIMARY KEY,
    policy_id INT NOT NULL,
    admin_id INT NOT NULL,
    reason TEXT,
    resolution_status ENUM('Open', 'Resolved', 'Archived') DEFAULT 'Open',
    filed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES policies(policy_id),
    FOREIGN KEY (admin_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 11. System Announcements Table (ISA's CRUD)
-- -----------------------------------------------------
CREATE TABLE system_announcements (
    announcement_id INT AUTO_INCREMENT PRIMARY KEY,
    isa_id INT NOT NULL,
    title VARCHAR(255),
    content TEXT,
    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATE,
    FOREIGN KEY (isa_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 12. Audit Logs Table (Admin/ISA Read function source)
-- -----------------------------------------------------
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    action_type VARCHAR(50),
    table_affected VARCHAR(50),
    record_id INT,
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -----------------------------------------------------
-- 13. Internal Messages Table (Staff-to-Staff Communication)
-- -----------------------------------------------------
CREATE TABLE internal_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    policy_id INT NULL,
    claim_id INT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    sent_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read TINYINT DEFAULT 0,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id),
    FOREIGN KEY (policy_id) REFERENCES policies(policy_id),
    FOREIGN KEY (claim_id) REFERENCES claims(claim_id)
);


-- -----------------------------------------------------
-- II. SAMPLE DATA (DML)
-- -----------------------------------------------------

-- Insert Roles
INSERT INTO roles (role_name) VALUES
('Customer'),                   -- role_id 1
('Customer Service Executive'), -- role_id 2
('Senior Insurance Advisor'),   -- role_id 3
('Finance Officer'),            -- role_id 4
('HR/Admin Manager'),           -- role_id 5
('IT System Analyst');          -- role_id 6

-- 1. Populate Users (Staff and Customers)
-- Password for all is 'password123'
INSERT INTO users (first_name, last_name, username, email, password, role_id, is_active) VALUES
('John', 'Doe', 'johndoe', 'john.customer@insure.com', 'password123', 1, 1),        -- user_id 1 (Customer A)
('Jane', 'Smith', 'janesmith', 'jane.customer@insure.com', 'password123', 1, 1),      -- user_id 2 (Customer B)
('Bob', 'Johnson', 'bobj', 'bob.cse@insure.com', 'password123', 2, 1),           -- user_id 3 (CSE)
('Charlie', 'Brown', 'charlieb', 'charlie.sia@insure.com', 'password123', 3, 1),     -- user_id 4 (SIA)
('Diana', 'Prince', 'dianap', 'diana.fo@insure.com', 'password123', 4, 1),          -- user_id 5 (FO)
('Alice', 'Admin', 'alicea', 'alice.admin@insure.com', 'password123', 5, 1),         -- user_id 6 (Admin)
('Ethan', 'Hunt', 'ethanh', 'ethan.isa@insure.com', 'password123', 6, 1),          -- user_id 7 (ISA)
('Deactivated', 'Staff', 'formercse', 'former.staff@insure.com', 'password123', 2, 0); -- user_id 8 (Deactivated Staff)

-- 2. Populate Customer Details
INSERT INTO customer_details (user_id, date_of_birth, phone_number, address, is_pending_review) VALUES
(1, '1985-06-15', '555-1001', '123 Main St, Anytown', 0),
(2, '1992-02-28', '555-1002', '456 Oak Ave, Othercity', 1); -- Customer B has a pending review on details

-- 3. Populate Applications (Testing Workflow Stages)
INSERT INTO applications (user_id, product_type, desired_coverage, current_status) VALUES
(1, 'Term Life 20Y', 500000.00, 'Accepted'),           -- App ID 1: Fully approved, ready for policy creation
(1, 'Whole Life', 100000.00, 'PendingSIA'),           -- App ID 2: Awaiting SIA review
(2, 'Term Life 10Y', 250000.00, 'Submitted'),          -- App ID 3: New, pending CSE review
(2, 'Universal Life', 750000.00, 'Incomplete');        -- App ID 4: Needs additional info

-- 4. Populate Risk Assessments (SIA ID: 4)
INSERT INTO risk_assessments (application_id, advisor_id, risk_score, recommendation) VALUES
(1, 4, 90, 'Excellent health history. Approve standard rate.'),
(2, 4, 55, 'Borderline risk factors. Recommend premium loading or revision.');

-- 5. Populate Policies (Only for App ID 1)
INSERT INTO policies (application_id, policy_number, start_date, annual_premium, policy_status) VALUES
(1, 'LI-2024-0001', '2024-10-01', 5000.00, 'Active'); -- Policy ID 1 (John Doe)

-- 6. Populate Beneficiaries (Customer Update CRUD Test)
INSERT INTO beneficiaries (policy_id, name, relationship, share_percentage) VALUES
(1, 'Jane Doe', 'Spouse', 75),
(1, 'Junior Doe', 'Child', 25);

-- 7. Populate Payments (FO ID: 5)
INSERT INTO payments (policy_id, finance_officer_id, amount, payment_date, type, status) VALUES
(1, 5, 5000.00, '2024-10-01', 'Received', 'Paid'),      -- Actual payment
(1, 5, 5000.00, '2025-10-01', 'Schedule', 'Due'),       -- Schedule for next year
(1, 5, 1250.00, '2024-11-01', 'Schedule', 'Unused');    -- Unused schedule

-- 8. Populate Claims
INSERT INTO claims (policy_id, filing_date, claim_type, claim_status, handler_id, payout_amount) VALUES
(1, '2024-11-10 10:00:00', 'Disability', 'PendingSIA', 4, NULL), -- Claim ID 1 (Active, awaiting SIA)
(1, '2024-05-01 15:00:00', 'Hospitalization', 'Paid', 4, 5000.00), -- Claim ID 2 (Closed/Paid)
(1, '2023-01-01 15:00:00', 'Accidental', 'Archived', 4, 1000.00); -- Claim ID 3 (Soft-deleted/Archived)

-- 9. Populate Policy Disputes (Admin ID: 6)
INSERT INTO policy_disputes (policy_id, admin_id, reason, resolution_status) VALUES
(1, 6, 'Customer disputes the premium increase after the first year.', 'Open'); -- Dispute ID 1

-- 10. Populate System Announcements (ISA ID: 7)
INSERT INTO system_announcements (isa_id, title, content, post_date, expiry_date) VALUES
(7, 'System Update Required', 'All staff must log out for a mandatory patch update.', '2024-11-15 08:00:00', '2024-11-15'),
(7, 'New Policy Terms', 'New term life product launching next week. Training is required.', '2024-11-15 09:00:00', '2025-01-01');

-- 11. Audit Logs (Simulating key actions)
INSERT INTO audit_logs (user_id, action_type, table_affected, record_id) VALUES
(1, 'CREATE', 'applications', 1),
(3, 'UPDATE', 'applications', 3),
(4, 'CREATE', 'risk_assessments', 1),
(6, 'LOGIN', 'users', 6),
(5, 'UPDATE', 'payments', 1);

-- 12. Internal Messages (Staff Communication Test)
INSERT INTO internal_messages (sender_id, receiver_id, policy_id, claim_id, subject, content, is_read) VALUES
(3, 4, 1, 1, 'Claim 1 Documents Ready', 'Please review the documents uploaded for John Doe’s disability claim.', 0),
(4, 5, 1, NULL, 'Policy 1 Premium Calc', 'The risk assessment is finalized, please calculate the final premium.', 1);