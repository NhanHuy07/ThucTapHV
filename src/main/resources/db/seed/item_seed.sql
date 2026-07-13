-- ============================================================
-- Seed data for Items (product-size inventory) — IDEMPOTENT
-- Database: PostgreSQL
-- Run AFTER product_seed.sql
--
-- Composite PK = (product_id, size_id)
-- Sizes: 1=S, 2=M, 3=L, 4=XL, 5=XXL, 6=XXXL
-- ============================================================

INSERT INTO tbl_items (product_id, size_id, quantity, status) VALUES

    -- ===================== THỜI TRANG NAM =====================

    -- Áo thun nam cổ tròn basic (product 1, MTS-001)
    (1, 1, 120, 'AVAILABLE'), (1, 2, 200, 'AVAILABLE'), (1, 3, 180, 'AVAILABLE'),
    (1, 4, 90,  'AVAILABLE'), (1, 5, 40,  'AVAILABLE'), (1, 6, 15,  'AVAILABLE'),

    -- Áo thun nam Dri-FIT (product 2, MTS-002)
    (2, 1, 60,  'AVAILABLE'), (2, 2, 100, 'AVAILABLE'), (2, 3, 80,  'AVAILABLE'),
    (2, 4, 50,  'AVAILABLE'), (2, 5, 20,  'AVAILABLE'),

    -- Áo thun nam Trefoil (product 3, MTS-003)
    (3, 1, 45,  'AVAILABLE'), (3, 2, 70,  'AVAILABLE'), (3, 3, 55,  'AVAILABLE'),
    (3, 4, 30,  'AVAILABLE'),

    -- Áo thun nam Supima Cotton (product 4, MTS-004)
    (4, 1, 80,  'AVAILABLE'), (4, 2, 120, 'AVAILABLE'), (4, 3, 100, 'AVAILABLE'),
    (4, 4, 60,  'AVAILABLE'), (4, 5, 25,  'AVAILABLE'),

    -- Áo thun nam oversize graphic (product 5, MTS-005)
    (5, 2, 50,  'AVAILABLE'), (5, 3, 70,  'AVAILABLE'), (5, 4, 80,  'AVAILABLE'),
    (5, 5, 40,  'AVAILABLE'), (5, 6, 20,  'AVAILABLE'),

    -- Quần jeans nam slim fit (product 6, MJN-001)
    (6, 1, 40,  'AVAILABLE'), (6, 2, 90,  'AVAILABLE'), (6, 3, 110, 'AVAILABLE'),
    (6, 4, 60,  'AVAILABLE'), (6, 5, 20,  'AVAILABLE'),

    -- Quần jeans nam straight (product 7, MJN-002)
    (7, 1, 25,  'AVAILABLE'), (7, 2, 55,  'AVAILABLE'), (7, 3, 70,  'AVAILABLE'),
    (7, 4, 45,  'AVAILABLE'), (7, 5, 15,  'AVAILABLE'),

    -- Quần jeans nam regular (product 8, MJN-003)
    (8, 1, 30,  'AVAILABLE'), (8, 2, 80,  'AVAILABLE'), (8, 3, 100, 'AVAILABLE'),
    (8, 4, 70,  'AVAILABLE'), (8, 5, 35,  'AVAILABLE'),

    -- Áo khoác gió nam nhẹ (product 9, MJK-001)
    (9, 1, 20,  'AVAILABLE'), (9, 2, 40,  'AVAILABLE'), (9, 3, 60,  'AVAILABLE'),
    (9, 4, 50,  'AVAILABLE'), (9, 5, 15,  'AVAILABLE'),

    -- Áo khoác hoodie nam (product 10, MJK-002)
    (10, 1, 30, 'AVAILABLE'), (10, 2, 80,  'AVAILABLE'), (10, 3, 110, 'AVAILABLE'),
    (10, 4, 70, 'AVAILABLE'), (10, 5, 25,  'AVAILABLE'),

    -- Áo khoác bomber nam (product 11, MJK-003)
    (11, 1, 20, 'AVAILABLE'), (11, 2, 35,  'AVAILABLE'), (11, 3, 50,  'AVAILABLE'),
    (11, 4, 40, 'AVAILABLE'),

    -- ===================== THỜI TRANG NỮ =====================

    -- Áo thun nữ cổ tròn basic (product 12, WTS-001)
    (12, 1, 150, 'AVAILABLE'), (12, 2, 180, 'AVAILABLE'), (12, 3, 120, 'AVAILABLE'),
    (12, 4, 50,  'AVAILABLE'),

    -- Áo thun nữ crop top (product 13, WTS-002)
    (13, 1, 90,  'AVAILABLE'), (13, 2, 100, 'AVAILABLE'), (13, 3, 60,  'AVAILABLE'),

    -- Áo thun nữ peplum (product 14, WTS-003)
    (14, 1, 40,  'AVAILABLE'), (14, 2, 65,  'AVAILABLE'), (14, 3, 50,  'AVAILABLE'),
    (14, 4, 20,  'AVAILABLE'),

    -- Đầm suông công sở (product 15, WDR-001)
    (15, 1, 35,  'AVAILABLE'), (15, 2, 55,  'AVAILABLE'), (15, 3, 45,  'AVAILABLE'),
    (15, 4, 20,  'AVAILABLE'),

    -- Đầm maxi hoa nhí (product 16, WDR-002)
    (16, 1, 25,  'AVAILABLE'), (16, 2, 40,  'AVAILABLE'), (16, 3, 35,  'AVAILABLE'),
    (16, 4, 15,  'AVAILABLE'),

    -- Đầm body ôm sát (product 17, WDR-003)
    (17, 1, 30,  'AVAILABLE'), (17, 2, 45,  'AVAILABLE'), (17, 3, 20,  'AVAILABLE'),

    -- Quần baggy nữ (product 18, WPN-001)
    (18, 1, 60,  'AVAILABLE'), (18, 2, 90,  'AVAILABLE'), (18, 3, 70,  'AVAILABLE'),
    (18, 4, 30,  'AVAILABLE'),

    -- Quần legging nữ (product 19, WPN-002)
    (19, 1, 100, 'AVAILABLE'), (19, 2, 150, 'AVAILABLE'), (19, 3, 120, 'AVAILABLE'),
    (19, 4, 60,  'AVAILABLE'),

    -- ===================== PHỤ KIỆN =====================

    -- Nón lưỡi trai logo (product 20, HAT-001) — one size (M)
    (20, 1, 100, 'AVAILABLE'), (20, 2, 300, 'AVAILABLE'), (20, 3, 150, 'AVAILABLE'),

    -- Mũ bucket unisex (product 21, HAT-002) — one size (M)
    (21, 1, 80,  'AVAILABLE'), (21, 2, 200, 'AVAILABLE'), (21, 3, 120, 'AVAILABLE'),

    -- Balo thời trang nam (product 22, BAG-001) — one size (L)
    (22, 3, 150, 'AVAILABLE'),

    -- Túi tote nữ canvas (product 23, BAG-002) — one size (L)
    (23, 3, 180, 'AVAILABLE'),

    -- ===================== GIÀY DÉP =====================

    -- Giày Air Max 90 (product 24, SNK-001)
    (24, 1, 40,  'AVAILABLE'), (24, 2, 80,  'AVAILABLE'), (24, 3, 90,  'AVAILABLE'),
    (24, 4, 50,  'AVAILABLE'), (24, 5, 20,  'AVAILABLE'),

    -- Giày Ultraboost (product 25, SNK-002)
    (25, 1, 35,  'AVAILABLE'), (25, 2, 60,  'AVAILABLE'), (25, 3, 70,  'AVAILABLE'),
    (25, 4, 40,  'AVAILABLE'), (25, 5, 15,  'AVAILABLE'),

    -- Giày Old Skool (product 26, SNK-003)
    (26, 1, 50,  'AVAILABLE'), (26, 2, 90,  'AVAILABLE'), (26, 3, 80,  'AVAILABLE'),
    (26, 4, 45,  'AVAILABLE'),

    -- Giày Chuck Taylor (product 27, SNK-004)
    (27, 1, 60,  'AVAILABLE'), (27, 2, 100, 'AVAILABLE'), (27, 3, 110, 'AVAILABLE'),
    (27, 4, 55,  'AVAILABLE'), (27, 5, 20,  'AVAILABLE'),

    -- Dép quai ngang Nike (product 28, SDL-001)
    (28, 1, 80,  'AVAILABLE'), (28, 2, 120, 'AVAILABLE'), (28, 3, 100, 'AVAILABLE'),
    (28, 4, 60,  'AVAILABLE'),

    -- Sandal Adilette (product 29, SDL-002)
    (29, 1, 70,  'AVAILABLE'), (29, 2, 100, 'AVAILABLE'), (29, 3, 90,  'AVAILABLE'),
    (29, 4, 50,  'AVAILABLE'),

    -- Dép xỏ ngón nam (product 30, SDL-003)
    (30, 1, 40,  'AVAILABLE'), (30, 2, 70,  'AVAILABLE'), (30, 3, 80,  'AVAILABLE'),
    (30, 4, 60,  'AVAILABLE')

ON CONFLICT (product_id, size_id) DO UPDATE SET
    quantity = EXCLUDED.quantity,
    status   = EXCLUDED.status;
