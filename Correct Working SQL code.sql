-- Create Database
CREATE DATABASE IF NOT EXISTS v1_16_thuli_life_insurance;
USE v1_16_thuli_life_insurance;

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
-- Note: These role IDs are referenced in the users table.
-- 'Customer' (1), 'Customer Service Executive' (2), 'Senior Insurance Advisor' (3),
-- 'Finance Officer' (4), 'HR/Admin Manager' (5), 'IT System Analyst' (6)
INSERT INTO roles (role_name) VALUES
('Customer'),
('Customer Service Executive'),
('Senior Insurance Advisor'),
('Finance Officer'),
('HR/Admin Manager'),
('IT System Analyst');

-- 1. Populate Users (Staff and Customers)
-- New user data as requested.
INSERT INTO users (first_name, last_name, username, email, password, role_id, is_active) VALUES
('Janith', 'Deshan', 'customer1', 'customer1@gmail.com', 'pass123', 1, 1),      -- user_id: 1
('John', 'Smith', 'customer2', 'customer2@gmail.com', 'pass123', 1, 1),         -- user_id: 2
('Peter', 'Jones', 'customer3', 'customer3@gmail.com', 'pass123', 1, 1),      -- user_id: 3
('Alice', 'Williams', 'cse1', 'cse1@gmail.com', 'cse123', 2, 1),                -- user_id: 4 (CSE)
('Bob', 'Brown', 'sia1', 'sia1@gmail.com', 'sia123', 3, 1),                      -- user_id: 5 (SIA)
('Charlie', 'Davis', 'fo1', 'fo1@gmail.com', 'fo123', 4, 1),                    -- user_id: 6 (FO)
('Thulindi', 'Rathnayake', 'admin1', 'admin@gmail.com', 'admin123', 5, 1),      -- user_id: 7 (Admin)
('Edward', 'Wilson', 'isa1', 'isa@gmail.com', 'isa123', 6, 1);                  -- user_id: 8 (ISA)

-- 2. Populate Customer Details
-- user_id 1 and 2 now refer to Janith Deshan and John Smith.
INSERT INTO customer_details (user_id, date_of_birth, phone_number, address, is_pending_review) VALUES
(1, '1990-01-20', '555-0101', '10 Galle Rd, Colombo', 0),
(2, '1988-05-15', '555-0102', '25 Kandy Rd, Kandy', 1), -- This customer has a pending detail review
(3, '1995-11-30', '555-0103', '30 Temple St, Jaffna', 0);

-- 3. Populate Applications (Testing Workflow Stages)
-- Applications are linked to the new customers.
INSERT INTO applications (user_id, product_type, desired_coverage, current_status) VALUES
(1, 'Term Life 20Y', 500000.00, 'Accepted'),     -- App ID 1: Janith's app, fully approved
(1, 'Whole Life', 100000.00, 'PendingSIA'),     -- App ID 2: Janith's second app, awaiting SIA review
(2, 'Term Life 10Y', 250000.00, 'Submitted'),    -- App ID 3: John's app, new, pending CSE review
(3, 'Universal Life', 750000.00, 'Incomplete'); -- App ID 4: Peter's app, needs more info

-- 4. Populate Risk Assessments
-- advisor_id updated from 4 to 5 (Bob Brown is the new SIA).
INSERT INTO risk_assessments (application_id, advisor_id, risk_score, recommendation) VALUES
(1, 5, 90, 'Excellent health history. Approve standard rate.'),
(2, 5, 55, 'Borderline risk factors. Recommend premium loading or revision.');

-- 5. Populate Policies
-- Only for App ID 1, which belongs to Janith Deshan.
INSERT INTO policies (application_id, policy_number, start_date, annual_premium, policy_status) VALUES
(1, 'LI-2025-1001', '2025-10-01', 5000.00, 'Active'); -- Policy ID 1

-- 6. Populate Beneficiaries
-- Beneficiary names updated to align with the new policy holder (Janith Deshan).
INSERT INTO beneficiaries (policy_id, name, relationship, share_percentage) VALUES
(1, 'Maria Deshan', 'Spouse', 75),
(1, 'Sam Deshan', 'Child', 25);

-- 7. Populate Payments
-- finance_officer_id updated from 5 to 6 (Charlie Davis is the new FO).
INSERT INTO payments (policy_id, finance_officer_id, amount, payment_date, type, status) VALUES
(1, 6, 5000.00, '2025-10-01', 'Received', 'Paid'),     -- Actual payment
(1, 6, 5000.00, '2026-10-01', 'Schedule', 'Due'),      -- Schedule for next year
(1, 6, 1250.00, '2025-11-01', 'Schedule', 'Unused');   -- Unused schedule

-- 8. Populate Claims
-- handler_id (SIA) updated from 4 to 5.
INSERT INTO claims (policy_id, filing_date, claim_type, claim_status, handler_id, payout_amount) VALUES
(1, '2025-11-10 10:00:00', 'Disability', 'PendingSIA', 5, NULL),      -- Claim ID 1 (Active, awaiting SIA Bob Brown)
(1, '2025-05-01 15:00:00', 'Hospitalization', 'Paid', 5, 5000.00),  -- Claim ID 2 (Closed/Paid)
(1, '2024-01-01 15:00:00', 'Accidental', 'Archived', 5, 1000.00); -- Claim ID 3 (Soft-deleted/Archived)

-- 9. Populate Policy Disputes
-- admin_id updated from 6 to 7 (Thulindi Rathnayake is the new Admin).
INSERT INTO policy_disputes (policy_id, admin_id, reason, resolution_status) VALUES
(1, 7, 'Customer disputes the premium increase after the first year.', 'Open'); -- Dispute ID 1

-- 10. Populate System Announcements
-- isa_id updated from 7 to 8 (Edward Wilson is the new ISA).
INSERT INTO system_announcements (isa_id, title, content, post_date, expiry_date) VALUES
(8, 'System Update Required', 'All staff must log out for a mandatory patch update tonight at 10 PM.', '2025-11-15 08:00:00', '2025-11-15'),
(8, 'New Policy Terms', 'New term life product launching next week. Training is mandatory for all CSE and SIA staff.', '2025-11-15 09:00:00', '2026-01-01');

-- 11. Audit Logs
-- user_ids updated to reflect the new staff IDs.
-- Old IDs: CSE(3), SIA(4), FO(5), Admin(6). New IDs: CSE(4), SIA(5), FO(6), Admin(7).
INSERT INTO audit_logs (user_id, action_type, table_affected, record_id) VALUES
(1, 'CREATE', 'applications', 1),    -- Janith (Customer) created an application
(4, 'UPDATE', 'applications', 3),    -- Alice (CSE) updated an application
(5, 'CREATE', 'risk_assessments', 1), -- Bob (SIA) created an assessment
(7, 'LOGIN', 'users', 7),            -- Thulindi (Admin) logged in
(6, 'UPDATE', 'payments', 1);        -- Charlie (FO) updated a payment

-- 12. Internal Messages
-- sender_id and receiver_id updated to new staff IDs.
-- Old: CSE(3)->SIA(4), SIA(4)->FO(5). New: CSE(4)->SIA(5), SIA(5)->FO(6).
-- Content updated to reflect new customer name.
INSERT INTO internal_messages (sender_id, receiver_id, policy_id, claim_id, subject, content, is_read) VALUES
(4, 5, 1, 1, 'Claim 1 Docs Ready', 'Please review the documents uploaded for Janith Deshan’s disability claim.', 0),
(5, 6, 1, NULL, 'Policy 1 Premium Calc', 'The risk assessment for policy LI-2025-1001 is finalized, please calculate the final premium.', 1);