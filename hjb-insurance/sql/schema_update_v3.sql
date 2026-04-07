-- v3: Insurance Plan management table
CREATE TABLE IF NOT EXISTS hjb_plan (
    plan_id   INT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50)     NOT NULL,
    plan_type VARCHAR(10)     NOT NULL,  -- AUTO | HOME
    amount    DECIMAL(10, 2)  NOT NULL,
    features  VARCHAR(500),              -- comma-separated feature list
    is_active TINYINT(1)      DEFAULT 1
);

INSERT INTO hjb_plan (plan_name, plan_type, amount, features, is_active) VALUES
('Basic',    'AUTO', 800.00,  'Liability coverage,Collision coverage,24/7 roadside assistance', 1),
('Standard', 'AUTO', 1200.00, 'All Basic benefits,Comprehensive coverage,Rental reimbursement', 1),
('Premium',  'AUTO', 1800.00, 'All Standard benefits,New car replacement,Accident forgiveness', 1),
('Basic',    'HOME', 600.00,  'Dwelling protection,Personal property,Liability coverage', 1),
('Standard', 'HOME', 900.00,  'All Basic benefits,Additional living expenses,Medical payments', 1),
('Premium',  'HOME', 1400.00, 'All Standard benefits,Equipment breakdown,Identity theft protection', 1);
