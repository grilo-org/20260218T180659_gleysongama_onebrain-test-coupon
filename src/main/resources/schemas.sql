CREATE UNIQUE INDEX IF NOT EXISTS ux_coupons_code_value_active
    ON coupons (code_value)
    WHERE deleted = FALSE;