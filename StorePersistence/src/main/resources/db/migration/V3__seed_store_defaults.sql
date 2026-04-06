INSERT INTO package_category (slug, name, description, hero_title, hero_subtitle, icon_key, active, display_order)
VALUES
    ('ranks', 'Ranks', 'Server-wide ranks and permanent perks.', 'Global Ranks', 'Support the server and unlock long-term rank benefits.', 'crown', TRUE, 1),
    ('keys', 'Crate Keys', 'Premium key bundles for progression and cosmetics.', 'Crate Keys', 'Bundle up key purchases for events, crates, and server rewards.', 'key', TRUE, 2),
    ('cosmetics', 'Cosmetics', 'Particle trails, titles, and aesthetic unlocks.', 'Aesthetics', 'Customize your identity with cosmetic-only purchases.', 'sparkles', TRUE, 3)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO store_package (category_id, slug, name, short_description, description_html, price, currency, visible, featured, giftable, package_type, billing_interval, accent_key, metadata_json)
SELECT id, 'god-rank-lifetime', 'God Rank (Lifetime)', 'Permanent premium network rank.', '<p>Permanent God rank with premium identity perks and priority access.</p>', 149.99, 'USD', TRUE, TRUE, TRUE, 'SINGLE', 'NONE', 'amber', '{}'::jsonb
FROM package_category WHERE slug = 'ranks'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO store_package (category_id, slug, name, short_description, description_html, price, currency, visible, featured, giftable, package_type, billing_interval, accent_key, metadata_json)
SELECT id, 'vip-plus-membership', 'VIP+ Membership', 'Monthly VIP+ subscription.', '<p>VIP+ monthly membership with recurring support perks.</p>', 14.99, 'USD', TRUE, FALSE, TRUE, 'SUBSCRIPTION', 'MONTHLY', 'violet', '{}'::jsonb
FROM package_category WHERE slug = 'ranks'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO store_package (category_id, slug, name, short_description, description_html, price, currency, visible, featured, giftable, package_type, billing_interval, accent_key, metadata_json)
SELECT id, 'mythic-crate-keys-10', '10x Mythic Crate Keys', 'Bundle of ten Mythic crate keys.', '<p>Ten Mythic crate keys delivered to the recipient account.</p>', 19.99, 'USD', TRUE, FALSE, TRUE, 'SINGLE', 'NONE', 'zinc', '{}'::jsonb
FROM package_category WHERE slug = 'keys'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO store_package (category_id, slug, name, short_description, description_html, price, currency, visible, featured, giftable, package_type, billing_interval, accent_key, metadata_json)
SELECT id, 'dragon-wings-particle', 'Dragon Wings Particle', 'Cosmetic wing effect.', '<p>Permanent cosmetic dragon wings particle effect.</p>', 9.99, 'USD', TRUE, FALSE, TRUE, 'SINGLE', 'NONE', 'zinc', '{}'::jsonb
FROM package_category WHERE slug = 'cosmetics'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO package_benefit (store_package_id, benefit_type, target_system, target_key, target_value, duration_days, stacking_policy, metadata_json)
SELECT id, 'RANK', 'VELOCITY', 'rank', 'God', NULL, 'REPLACE', '{}'::jsonb
FROM store_package WHERE slug = 'god-rank-lifetime';

INSERT INTO package_benefit (store_package_id, benefit_type, target_system, target_key, target_value, duration_days, stacking_policy, metadata_json)
SELECT id, 'RANK', 'VELOCITY', 'rank', 'VIPPlus', 30, 'EXTEND', '{}'::jsonb
FROM store_package WHERE slug = 'vip-plus-membership';

INSERT INTO coupon (code, coupon_type, percent_off, amount_off, active, max_uses, use_count)
VALUES
    ('SUMMER26', 'PERCENTAGE', 20.00, NULL, TRUE, 1000, 0),
    ('VIP-ONBOARD', 'FIXED_AMOUNT', NULL, 50.00, TRUE, NULL, 0)
ON CONFLICT (code) DO NOTHING;

INSERT INTO promotion (slug, name, status, target_type, discount_type, discount_value, starts_at, ends_at)
VALUES ('summer-wipe-blowout', 'Summer Wipe Blowout', 'ACTIVE', 'GLOBAL', 'PERCENTAGE', 20.00, NOW(), NOW() + INTERVAL '12 days')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO admin_role (name)
VALUES ('ADMIN'), ('STORE_MANAGER'), ('SUPPORT_AGENT'), ('AUDITOR')
ON CONFLICT (name) DO NOTHING;
