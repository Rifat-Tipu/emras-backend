-- Seed initial categories for Emras clothing shop
INSERT INTO schema_product.categories (name_en, name_bn, slug, created_by, updated_by)
VALUES
    ('Men',    'পুরুষ',     'men',    'system', 'system'),
    ('Women',  'মহিলা',     'women',  'system', 'system'),
    ('Kids',   'শিশু',      'kids',   'system', 'system')
    ON CONFLICT (slug) DO NOTHING;

-- Sub-categories for Men
INSERT INTO schema_product.categories (name_en, name_bn, slug, parent_id, created_by, updated_by)
SELECT 'Shirts',   'শার্ট',    'men-shirts',   id, 'system', 'system' FROM schema_product.categories WHERE slug = 'men'
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO schema_product.categories (name_en, name_bn, slug, parent_id, created_by, updated_by)
SELECT 'T-Shirts', 'টি-শার্ট', 'men-tshirts',  id, 'system', 'system' FROM schema_product.categories WHERE slug = 'men'
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO schema_product.categories (name_en, name_bn, slug, parent_id, created_by, updated_by)
SELECT 'Polo',     'পোলো',    'men-polo',     id, 'system', 'system' FROM schema_product.categories WHERE slug = 'men'
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO schema_product.categories (name_en, name_bn, slug, parent_id, created_by, updated_by)
SELECT 'Pants',    'প্যান্ট', 'men-pants',    id, 'system', 'system' FROM schema_product.categories WHERE slug = 'men'
    ON CONFLICT (slug) DO NOTHING;