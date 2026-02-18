-- Dados de exemplo para popular o banco H2, para fins de avaliação dessa etapa do processo seletivo, os dados podem ser comentados

INSERT INTO coupons (id, code_value, description, discount_value, expiration_date, published, deleted, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'PRIME1', 'Primeira compra', 10.00, '2025-12-31', TRUE, FALSE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO coupons (id, code_value, description, discount_value, expiration_date, published, deleted, created_at, updated_at) VALUES
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'HAPPYD', 'Happy Day Promo', 5.50, '2024-06-30', TRUE, FALSE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO coupons (id, code_value, description, discount_value, expiration_date, published, deleted, created_at, updated_at) VALUES
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'OLDONE', 'Cupom Antigo Inativo', 2.00, '2024-01-01', FALSE, FALSE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO coupons (id, code_value, description, discount_value, expiration_date, published, deleted, created_at, updated_at) VALUES
    ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'DELETD', 'Cupom Deletado', 1.00, '2025-01-01', TRUE, TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()); -- Este não aparecerá nas buscas normais