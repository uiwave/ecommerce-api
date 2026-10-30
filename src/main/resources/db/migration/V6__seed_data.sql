INSERT INTO categories (name, slug, description) VALUES
    ('Cuidado facial', 'cuidado-facial', 'Limpieza, hidratación y protección para el rostro.'),
    ('Cuidado corporal', 'cuidado-corporal', 'Cremas, aceites y tratamientos para el cuerpo.'),
    ('Maquillaje', 'maquillaje', 'Productos de color y acabado profesional.'),
    ('Accesorios', 'accesorios', 'Herramientas y complementos de belleza.')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Sérum hidratante de ácido hialurónico', 'serum-hidratante-acido-hialuronico',
       'Sérum ligero con ácido hialurónico que hidrata en profundidad y mejora la elasticidad de la piel.',
       49.90, 59.90, 25, 'SKU-SERUM-01', c.id,
       'https://picsum.photos/seed/serum/600/600', TRUE
FROM categories c WHERE c.slug = 'cuidado-facial'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Crema facial de noche', 'crema-facial-de-noche',
       'Crema nutritiva de textura rica que repara y regenera la piel durante el descanso.',
       39.90, NULL, 30, 'SKU-CREMA-01', c.id,
       'https://picsum.photos/seed/crema/600/600', TRUE
FROM categories c WHERE c.slug = 'cuidado-facial'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Limpiador facial suave', 'limpiador-facial-suave',
       'Gel limpiador de uso diario que elimina impurezas sin resecar la piel.',
       24.90, 29.90, 40, 'SKU-LIMP-01', c.id,
       'https://picsum.photos/seed/limpiador/600/600', TRUE
FROM categories c WHERE c.slug = 'cuidado-facial'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Aceite corporal de almendras', 'aceite-corporal-almendras',
       'Aceite natural de almendras que nutre y suaviza la piel del cuerpo.',
       32.50, NULL, 18, 'SKU-ACEITE-01', c.id,
       'https://picsum.photos/seed/aceite/600/600', TRUE
FROM categories c WHERE c.slug = 'cuidado-corporal'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Exfoliante corporal de café', 'exfoliante-corporal-cafe',
       'Exfoliante con granos de café que renueva la piel y mejora su textura.',
       27.90, 34.90, 22, 'SKU-EXFO-01', c.id,
       'https://picsum.photos/seed/exfoliante/600/600', TRUE
FROM categories c WHERE c.slug = 'cuidado-corporal'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Labial mate de larga duración', 'labial-mate-larga-duracion',
       'Labial de acabado mate con hasta 12 horas de duración y fórmula hidratante.',
       19.90, NULL, 50, 'SKU-LABIAL-01', c.id,
       'https://picsum.photos/seed/labial/600/600', TRUE
FROM categories c WHERE c.slug = 'maquillaje'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Máscara de pestañas volumen', 'mascara-pestanas-volumen',
       'Máscara que aporta volumen y definición sin grumos.',
       22.90, 26.90, 35, 'SKU-MASCARA-01', c.id,
       'https://picsum.photos/seed/mascara/600/600', TRUE
FROM categories c WHERE c.slug = 'maquillaje'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Brocha de base profesional', 'brocha-base-profesional',
       'Brocha de cerdas sintéticas para una aplicación uniforme de base o corrector.',
       15.90, NULL, 60, 'SKU-BROCHA-01', c.id,
       'https://picsum.photos/seed/brocha/600/600', TRUE
FROM categories c WHERE c.slug = 'accesorios'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO products (name, slug, description, price, compare_price, stock, sku, category_id, image_url, active)
SELECT 'Esponja de maquillaje', 'esponja-de-maquillaje',
       'Esponja de látex libre para difuminar base y corrector con acabado natural.',
       9.90, 12.90, 80, 'SKU-ESPONJA-01', c.id,
       'https://picsum.photos/seed/esponja/600/600', TRUE
FROM categories c WHERE c.slug = 'accesorios'
ON CONFLICT (slug) DO NOTHING;
