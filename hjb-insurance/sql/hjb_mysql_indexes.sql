-- ============================================================
-- HJB Insurance — Database Index Optimization
-- ============================================================
-- Background:
--   MySQL InnoDB does NOT automatically create indexes on
--   foreign key columns in child tables. Without explicit
--   indexes, every FK-based lookup performs a full table scan
--   (EXPLAIN type: ALL), which degrades performance as data grows
--   and holds row locks longer — directly harming the concurrent
--   payment transactions protected by REPEATABLE_READ isolation.
--
-- Strategy:
--   Index every FK column that appears in a WHERE clause in the
--   application's high-frequency query paths (CustomerPortalServiceImpl).
--   The critical path executed on every customer portal load is:
--
--     hjb_policy.HJB_CUSTOMER_CUST_ID        (find policies by customer)
--       → hjb_auto_invoice.HJB_AUTOPOLICY_AP_ID    (find invoices by policy)
--         → hjb_auto_payment.HJB_AUTO_INVOICE_I_ID (find payments by invoice)
--       → hjb_home_invoice.HJB_HOMEPOLICY_HP_ID
--         → hjb_home_payment.HJB_HOME_INVOICE_I_ID
-- ============================================================


-- ── Index 1 ─────────────────────────────────────────────────
-- Table:  hjb_policy
-- Column: HJB_CUSTOMER_CUST_ID
-- Reason: findByCustomerId() is called on every portal page load
--         (getAutoPolicies, getHomePolicies, getInvoices, getPayments).
--         Without this index, MySQL scans ALL policies to find ones
--         belonging to a single customer.
CREATE INDEX idx_policy_customer
    ON hjb_policy (HJB_CUSTOMER_CUST_ID);


-- ── Index 2 ─────────────────────────────────────────────────
-- Table:  hjb_auto_invoice
-- Column: HJB_AUTOPOLICY_AP_ID
-- Reason: findByAutoPolicyId() is called for every auto policy
--         owned by a customer to build the invoice list.
--         This is nested inside the policy loop, making it N×M
--         without an index (N customers × M policies each).
CREATE INDEX idx_auto_invoice_policy
    ON hjb_auto_invoice (HJB_AUTOPOLICY_AP_ID);


-- ── Index 3 ─────────────────────────────────────────────────
-- Table:  hjb_auto_payment
-- Column: HJB_AUTO_INVOICE_I_ID
-- Reason: findByInvoiceId() is called twice per invoice:
--         once to calculate paid amount in getInvoices(),
--         once inside makePayment() under REPEATABLE_READ.
--         Without this index, the SUM query holds a full-table
--         lock longer, increasing contention between concurrent
--         payment transactions.
CREATE INDEX idx_auto_payment_invoice
    ON hjb_auto_payment (HJB_AUTO_INVOICE_I_ID);


-- ── Index 4 ─────────────────────────────────────────────────
-- Table:  hjb_home_invoice
-- Column: HJB_HOMEPOLICY_HP_ID
-- Reason: Same as Index 2, for the home insurance query path.
CREATE INDEX idx_home_invoice_policy
    ON hjb_home_invoice (HJB_HOMEPOLICY_HP_ID);


-- ── Index 5 ─────────────────────────────────────────────────
-- Table:  hjb_home_payment
-- Column: HJB_HOME_INVOICE_I_ID
-- Reason: Same as Index 3, for home payment concurrency path.
CREATE INDEX idx_home_payment_invoice
    ON hjb_home_payment (HJB_HOME_INVOICE_I_ID);


-- ── Index 6 ─────────────────────────────────────────────────
-- Table:  hjb_vehicle
-- Column: HJB_AUTOPOLICY_AP_ID
-- Reason: findByAutoPolicyId() in VehicleMapper is called by
--         employees managing vehicle records and by portal
--         policy detail views.
CREATE INDEX idx_vehicle_policy
    ON hjb_vehicle (HJB_AUTOPOLICY_AP_ID);


-- ── Index 7 ─────────────────────────────────────────────────
-- Table:  hjb_driver
-- Column: HJB_VEHICLE_VIN
-- Reason: findByVin() in DriverMapper looks up all drivers
--         for a given vehicle. VIN is VARCHAR(17) — without an
--         index, every lookup scans the entire driver table.
CREATE INDEX idx_driver_vehicle
    ON hjb_driver (HJB_VEHICLE_VIN);


-- ── Index 8 ─────────────────────────────────────────────────
-- Table:  hjb_home
-- Column: HJB_HOMEPOLICY_HP_ID
-- Reason: findByHomePolicyId() in HomeMapper looks up the
--         property details for a given home policy.
CREATE INDEX idx_home_policy
    ON hjb_home (HJB_HOMEPOLICY_HP_ID);


-- ============================================================
-- VERIFY: Run EXPLAIN on the critical query path
-- Execute these BEFORE and AFTER applying indexes to compare.
-- Look for: type changing from ALL → ref, rows count dropping.
-- ============================================================

-- Q1: Find all auto policies for a customer (portal page load)
EXPLAIN SELECT ap.AP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID
        FROM hjb_autopolicy ap
        JOIN hjb_policy p ON ap.AP_ID = p.POLICY_ID
        WHERE p.HJB_CUSTOMER_CUST_ID = 1001;

-- Q2: Find invoices for a policy (nested in above)
EXPLAIN SELECT * FROM hjb_auto_invoice
        WHERE HJB_AUTOPOLICY_AP_ID = 1;

-- Q3: Sum payments for an invoice (called inside makePayment REPEATABLE_READ tx)
EXPLAIN SELECT * FROM hjb_auto_payment
        WHERE HJB_AUTO_INVOICE_I_ID = 10001;

-- Q4: Home invoice lookup
EXPLAIN SELECT * FROM hjb_home_invoice
        WHERE HJB_HOMEPOLICY_HP_ID = 1;

-- Q5: Home payment sum
EXPLAIN SELECT * FROM hjb_home_payment
        WHERE HJB_HOME_INVOICE_I_ID = 20001;
