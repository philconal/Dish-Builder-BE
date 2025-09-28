-- Insert default tenant
INSERT INTO dish_builder_schema.tenant (id, created_at, updated_at, name, phone, email, address, url_slug, logo_url, sub_domain, status)
VALUES (
    '7cccdad4-c562-402b-8dce-d64559a91500',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'Default Tenant',
    '0123456789',
    'default@dishbuilder.com',
    'Default Address',
    'default',
    'https://example.com/logo.png',
    'default',
    1
) ON CONFLICT (id) DO NOTHING;

-- Insert default roles
INSERT INTO dish_builder_schema.role (id, created_at, updated_at, name, description)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SUPER_ADMIN', 'Super Administrator with full access'),
    ('550e8400-e29b-41d4-a716-446655440002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ADMIN', 'Administrator with tenant access'),
    ('550e8400-e29b-41d4-a716-446655440003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CUSTOMER', 'Regular customer user')
ON CONFLICT (id) DO NOTHING;

-- Insert default categories
INSERT INTO dish_builder_schema.category (id, created_at, updated_at, name, description, tenant_id)
VALUES 
    ('660e8400-e29b-41d4-a716-446655440001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Vegetables', 'Fresh vegetables and greens', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Meat', 'Fresh meat and poultry', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Seafood', 'Fresh fish and seafood', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dairy', 'Milk, cheese, and dairy products', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440005', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Grains', 'Rice, pasta, and grain products', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440006', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Spices', 'Herbs, spices, and seasonings', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440007', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Fruits', 'Fresh fruits and berries', '7cccdad4-c562-402b-8dce-d64559a91500'),
    ('660e8400-e29b-41d4-a716-446655440008', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Beverages', 'Drinks and beverages', '7cccdad4-c562-402b-8dce-d64559a91500')
ON CONFLICT (id) DO NOTHING;

-- Insert default super admin user
INSERT INTO dish_builder_schema."user" (id, created_at, updated_at, username, password, first_name, last_name, phone, email, register_with, status, user_type, tenant_id)
VALUES (
    '770e8400-e29b-41d4-a716-446655440001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'superadmin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', -- password: admin123
    'Super',
    'Admin',
    '0123456789',
    'superadmin@dishbuilder.com',
    0,
    2, -- ACTIVE
    1, -- ADMIN
    '7cccdad4-c562-402b-8dce-d64559a91500'
) ON CONFLICT (id) DO NOTHING;

-- Assign SUPER_ADMIN role to super admin user
INSERT INTO dish_builder_schema.user_role (role_id, user_id)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001')
ON CONFLICT (role_id, user_id) DO NOTHING;
