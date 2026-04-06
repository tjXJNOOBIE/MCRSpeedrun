CREATE TABLE player_account (
    id BIGSERIAL PRIMARY KEY,
    minecraft_uuid VARCHAR(36) NOT NULL UNIQUE,
    current_username VARCHAR(32) NOT NULL UNIQUE,
    account_state VARCHAR(32) NOT NULL,
    linked_identities_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE package_category (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    hero_title VARCHAR(120) NOT NULL,
    hero_subtitle TEXT NOT NULL,
    icon_key VARCHAR(32) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE store_package (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES package_category(id),
    slug VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    short_description TEXT NOT NULL,
    description_html TEXT NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    giftable BOOLEAN NOT NULL DEFAULT TRUE,
    package_type VARCHAR(32) NOT NULL,
    billing_interval VARCHAR(32) NOT NULL,
    accent_key VARCHAR(32) NOT NULL DEFAULT 'amber',
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE package_benefit (
    id BIGSERIAL PRIMARY KEY,
    store_package_id BIGINT NOT NULL REFERENCES store_package(id) ON DELETE CASCADE,
    benefit_type VARCHAR(32) NOT NULL,
    target_system VARCHAR(64) NOT NULL,
    target_key VARCHAR(64) NOT NULL,
    target_value VARCHAR(160) NOT NULL,
    duration_days INTEGER NULL,
    stacking_policy VARCHAR(32) NOT NULL,
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE coupon (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    coupon_type VARCHAR(32) NOT NULL,
    percent_off NUMERIC(12, 2) NULL,
    amount_off NUMERIC(12, 2) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    max_uses INTEGER NULL,
    use_count INTEGER NOT NULL DEFAULT 0,
    starts_at TIMESTAMPTZ NULL,
    ends_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE promotion (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    status VARCHAR(32) NOT NULL,
    target_type VARCHAR(32) NOT NULL,
    discount_type VARCHAR(32) NOT NULL,
    discount_value NUMERIC(12, 2) NOT NULL,
    category_slug VARCHAR(64) NULL,
    package_slug VARCHAR(64) NULL,
    starts_at TIMESTAMPTZ NULL,
    ends_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE checkout_session (
    id BIGSERIAL PRIMARY KEY,
    session_token VARCHAR(80) NOT NULL UNIQUE,
    purchaser_username VARCHAR(32) NOT NULL,
    recipient_username VARCHAR(32) NOT NULL,
    purchaser_uuid VARCHAR(36) NOT NULL,
    recipient_uuid VARCHAR(36) NOT NULL,
    coupon_code VARCHAR(64) NULL,
    status VARCHAR(32) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    discount_total NUMERIC(12, 2) NOT NULL,
    total NUMERIC(12, 2) NOT NULL,
    line_items_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    stripe_checkout_session_id VARCHAR(128) NULL,
    stripe_customer_id VARCHAR(128) NULL,
    idempotency_key VARCHAR(80) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_package_category_display_order ON package_category (display_order);
CREATE INDEX idx_store_package_category_visible ON store_package (category_id, visible);
CREATE INDEX idx_checkout_session_recipient ON checkout_session (recipient_uuid, status);
