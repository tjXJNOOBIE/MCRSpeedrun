CREATE TABLE store_order (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(40) NOT NULL UNIQUE,
    purchaser_player_id BIGINT NULL REFERENCES player_account(id),
    recipient_player_id BIGINT NULL REFERENCES player_account(id),
    purchaser_username_snapshot VARCHAR(32) NOT NULL,
    recipient_username_snapshot VARCHAR(32) NOT NULL,
    state VARCHAR(32) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    discount_total NUMERIC(12, 2) NOT NULL,
    total NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payment_provider VARCHAR(32) NOT NULL,
    stripe_checkout_session_id VARCHAR(128) NULL,
    stripe_customer_id VARCHAR(128) NULL,
    paid_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE store_order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES store_order(id) ON DELETE CASCADE,
    store_package_id BIGINT NULL REFERENCES store_package(id),
    package_slug_snapshot VARCHAR(64) NOT NULL,
    package_name_snapshot VARCHAR(160) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    total_price NUMERIC(12, 2) NOT NULL,
    benefit_snapshot_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE payment_transaction (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NULL REFERENCES store_order(id),
    provider VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider_reference VARCHAR(128) NULL,
    provider_event_id VARCHAR(128) NULL UNIQUE,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    processed_at TIMESTAMPTZ NULL,
    error_code VARCHAR(80) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE entitlement (
    id BIGSERIAL PRIMARY KEY,
    player_account_id BIGINT NOT NULL REFERENCES player_account(id),
    order_item_id BIGINT NOT NULL REFERENCES store_order_item(id),
    benefit_type VARCHAR(32) NOT NULL,
    target_system VARCHAR(64) NOT NULL,
    target_key VARCHAR(64) NOT NULL,
    target_value VARCHAR(160) NOT NULL,
    state VARCHAR(32) NOT NULL,
    effective_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NULL,
    revoked_at TIMESTAMPTZ NULL,
    revocation_reason VARCHAR(255) NULL,
    source_package_name VARCHAR(160) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE fulfillment_job (
    id BIGSERIAL PRIMARY KEY,
    entitlement_id BIGINT NOT NULL REFERENCES entitlement(id),
    target_system VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT NULL,
    idempotency_key VARCHAR(80) NOT NULL UNIQUE,
    correlation_id VARCHAR(80) NOT NULL,
    next_attempt_at TIMESTAMPTZ NOT NULL,
    last_attempt_at TIMESTAMPTZ NULL,
    processed_by_node VARCHAR(64) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    actor_type VARCHAR(32) NOT NULL,
    actor_id VARCHAR(128) NOT NULL,
    action VARCHAR(128) NOT NULL,
    target_type VARCHAR(64) NOT NULL,
    target_id VARCHAR(128) NOT NULL,
    correlation_id VARCHAR(80) NULL,
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE admin_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    break_glass_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE admin_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE admin_user_role (
    admin_user_id BIGINT NOT NULL REFERENCES admin_user(id) ON DELETE CASCADE,
    admin_role_id BIGINT NOT NULL REFERENCES admin_role(id) ON DELETE CASCADE,
    PRIMARY KEY (admin_user_id, admin_role_id)
);

CREATE TABLE admin_identity (
    id BIGSERIAL PRIMARY KEY,
    admin_user_id BIGINT NOT NULL REFERENCES admin_user(id) ON DELETE CASCADE,
    provider VARCHAR(32) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (provider, provider_user_id)
);

CREATE TABLE ownership_challenge (
    id BIGSERIAL PRIMARY KEY,
    player_account_id BIGINT NULL REFERENCES player_account(id),
    username_snapshot VARCHAR(32) NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    code VARCHAR(32) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    verified_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_store_order_recipient ON store_order (recipient_player_id, created_at DESC);
CREATE INDEX idx_payment_transaction_reference ON payment_transaction (provider_reference);
CREATE INDEX idx_entitlement_player_state ON entitlement (player_account_id, state);
CREATE INDEX idx_fulfillment_job_status_attempt ON fulfillment_job (status, next_attempt_at);
CREATE INDEX idx_audit_log_actor_time ON audit_log (actor_id, created_at DESC);
CREATE INDEX idx_admin_identity_email ON admin_identity (email);
