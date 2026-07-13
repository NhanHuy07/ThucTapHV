-- ============================================================
-- Seed data for Product module (IDEMPOTENT — safe to re-run)
-- Database: PostgreSQL
-- Run AFTER schema.sql
--
-- Uses ON CONFLICT ... DO UPDATE so existing rows get updated
-- instead of throwing duplicate key errors.
--
-- Image sources: Unsplash, Pexels, FakeStoreAPI, Clearbit Logo
-- ============================================================

-- =========================== CATEGORIES ===========================

-- Level 0: Root categories
INSERT INTO tbl_categories (id, name, code, img_url, description, status, parent_id, is_leaf, level, path, version)
VALUES
    (1,  'Thời trang nam',    'MEN',    'https://images.unsplash.com/photo-1552642986-ccb41e7059e7?w=200&h=200&fit=crop',           'Quần áo nam',        'ACTIVE', NULL, FALSE, 0, '1',       0),
    (2,  'Thời trang nữ',    'WOMEN',  'https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=200&h=200&fit=crop',           'Quần áo nữ',         'ACTIVE', NULL, FALSE, 0, '2',       0),
    (3,  'Phụ kiện',         'ACC',    'https://images.unsplash.com/photo-1598532163257-ae3c6b2524b6?w=200&h=200&fit=crop',           'Phụ kiện thời trang', 'ACTIVE', NULL, FALSE, 0, '3',       0),
    (4,  'Giày dép',         'SHOES',  'https://images.pexels.com/photos/2048548/pexels-photo-2048548.jpeg?auto=compress&cs=tinysrgb&w=200', 'Giày dép các loại',   'ACTIVE', NULL, FALSE, 0, '4',       0)
ON CONFLICT (id) DO UPDATE SET
    name        = EXCLUDED.name,
    code        = EXCLUDED.code,
    img_url     = EXCLUDED.img_url,
    description = EXCLUDED.description,
    status      = EXCLUDED.status,
    parent_id   = EXCLUDED.parent_id,
    is_leaf     = EXCLUDED.is_leaf,
    level       = EXCLUDED.level,
    path        = EXCLUDED.path;

-- Level 1: Subcategories
INSERT INTO tbl_categories (id, name, code, img_url, description, status, parent_id, is_leaf, level, path, version)
VALUES
    (5,  'Áo thun nam',      'MTS',    'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=200&h=200&fit=crop',           'Áo thun nam các loại','ACTIVE', 1, TRUE,  1, '1/5',     0),
    (6,  'Quần jeans nam',   'MJN',    'https://images.unsplash.com/photo-1563902575-bc2309dc76e4?w=200&h=200&fit=crop',               'Quần jeans nam',      'ACTIVE', 1, TRUE,  1, '1/6',     0),
    (7,  'Áo khoác nam',     'MJK',    'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=200&h=200&fit=crop',               'Áo khoác nam',        'ACTIVE', 1, TRUE,  1, '1/7',     0),
    (8,  'Áo thun nữ',      'WTS',    'https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=200&h=200&fit=crop',             'Áo thun nữ các loại','ACTIVE', 2, TRUE,  1, '2/8',     0),
    (9,  'Váy đầm',         'WDR',    'https://images.unsplash.com/flagged/photo-1585052201332-b8c0ce30972f?w=200&h=200&fit=crop',     'Váy đầm nữ',         'ACTIVE', 2, TRUE,  1, '2/9',     0),
    (10, 'Quần nữ',         'WPN',    'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=200&h=200&fit=crop',             'Quần nữ các loại',   'ACTIVE', 2, TRUE,  1, '2/10',    0),
    (11, 'Mũ & Nón',        'HAT',    'https://images.unsplash.com/photo-1563319078-0b3ad9b6007a?w=200&h=200&fit=crop',               'Mũ nón thời trang',  'ACTIVE', 3, TRUE,  1, '3/11',    0),
    (12, 'Túi xách',        'BAG',    'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=200&h=200&fit=crop',               'Túi xách các loại',  'ACTIVE', 3, TRUE,  1, '3/12',    0),
    (13, 'Giày thể thao',   'SNK',    'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=200', 'Giày sneaker',        'ACTIVE', 4, TRUE,  1, '4/13',    0),
    (14, 'Dép & Sandal',    'SDL',    'https://images.pexels.com/photos/267202/pexels-photo-267202.jpeg?auto=compress&cs=tinysrgb&w=200',   'Dép và sandal',       'ACTIVE', 4, TRUE,  1, '4/14',    0)
ON CONFLICT (id) DO UPDATE SET
    name        = EXCLUDED.name,
    code        = EXCLUDED.code,
    img_url     = EXCLUDED.img_url,
    description = EXCLUDED.description,
    status      = EXCLUDED.status,
    parent_id   = EXCLUDED.parent_id,
    is_leaf     = EXCLUDED.is_leaf,
    level       = EXCLUDED.level,
    path        = EXCLUDED.path;

SELECT setval('tbl_categories_id_seq', (SELECT MAX(id) FROM tbl_categories));

-- =========================== PROVIDERS ===========================

INSERT INTO tbl_providers (id, name, code, description, email, phone, status, logo, version)
VALUES
    (1,  'Nike',        'NIKE',   'Thương hiệu thể thao hàng đầu thế giới',     'contact@nike.com',        '0901000001', 'FAMOUS',   'https://logo.clearbit.com/nike.com',        0),
    (2,  'Adidas',      'ADIDAS', 'Thương hiệu thể thao toàn cầu từ Đức',       'contact@adidas.com',      '0901000002', 'FAMOUS',   'https://logo.clearbit.com/adidas.com',      0),
    (3,  'Uniqlo',      'UNIQLO', 'Thời trang cơ bản chất lượng cao từ Nhật',    'contact@uniqlo.com',      '0901000003', 'FAMOUS',   'https://logo.clearbit.com/uniqlo.com',      0),
    (4,  'Zara',        'ZARA',   'Thời trang nhanh đến từ Tây Ban Nha',         'contact@zara.com',        '0901000004', 'FAMOUS',   'https://logo.clearbit.com/zara.com',        0),
    (5,  'H&M',         'HM',     'Thời trang bình dân từ Thụy Điển',            'contact@hm.com',          '0901000005', 'ACTIVE',   'https://logo.clearbit.com/hm.com',          0),
    (6,  'Coolmate',    'COOL',   'Thương hiệu thời trang nam Việt Nam',         'hello@coolmate.me',       '0901000006', 'ACTIVE',   'https://logo.clearbit.com/coolmate.me',     0),
    (7,  'Routine',     'RTN',    'Thời trang nam thiết kế tối giản',            'contact@routine.vn',      '0901000007', 'ACTIVE',   'https://logo.clearbit.com/routine.vn',      0),
    (8,  'Ivy Moda',    'IVY',    'Thời trang nữ cao cấp Việt Nam',              'contact@ivymoda.com',     '0901000008', 'ACTIVE',   'https://logo.clearbit.com/ivymoda.com',     0),
    (9,  'Vans',        'VANS',   'Giày thời trang đường phố từ Mỹ',            'contact@vans.com',        '0901000009', 'ACTIVE',   'https://logo.clearbit.com/vans.com',        0),
    (10, 'Converse',    'CVS',    'Giày thời trang biểu tượng từ Mỹ',           'contact@converse.com',    '0901000010', 'FAMOUS',   'https://logo.clearbit.com/converse.com',    0)
ON CONFLICT (id) DO UPDATE SET
    name        = EXCLUDED.name,
    code        = EXCLUDED.code,
    description = EXCLUDED.description,
    email       = EXCLUDED.email,
    phone       = EXCLUDED.phone,
    status      = EXCLUDED.status,
    logo        = EXCLUDED.logo;

SELECT setval('tbl_providers_id_seq', (SELECT MAX(id) FROM tbl_providers));

-- =========================== SIZES ===========================

INSERT INTO tbl_sizes (id, size, weight, height)
VALUES
    (1, 'S',    0.2, 65),
    (2, 'M',    0.25, 70),
    (3, 'L',    0.3, 75),
    (4, 'XL',   0.35, 80),
    (5, 'XXL',  0.4, 85),
    (6, 'XXXL', 0.45, 90)
ON CONFLICT (id) DO UPDATE SET
    size   = EXCLUDED.size,
    weight = EXCLUDED.weight,
    height = EXCLUDED.height;

SELECT setval('tbl_sizes_id_seq', (SELECT MAX(id) FROM tbl_sizes));

-- =========================== PRODUCTS ===========================

INSERT INTO tbl_products (id, name, code, description, quantity_sold, price, status, rated, category_id, provider_id, version, img, video)
VALUES
    -- Áo thun nam (category_id = 5)
    (1,  'Áo thun nam cổ tròn basic',         'MTS-001', 'Áo thun cotton 100% thoáng mát, form regular fit',                   1250, 299000,  'BESTSELLER',   4.8, 5, 6, 0,
         'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=400&fit=crop', NULL),
    (2,  'Áo thun nam Dri-FIT',               'MTS-002', 'Áo thun thể thao công nghệ Dri-FIT thoát ẩm nhanh',                  870, 890000,  'ACTIVE',       4.5, 5, 1, 0,
         'https://images.unsplash.com/photo-1552642986-ccb41e7059e7?w=400&h=400&fit=crop', NULL),
    (3,  'Áo thun nam Trefoil',               'MTS-003', 'Áo thun nam logo Trefoil cổ điển phong cách streetwear',              620, 750000,  'ACTIVE',       4.3, 5, 2, 0,
         'https://images.unsplash.com/photo-1626806851009-c98659eb1af0?w=400&h=400&fit=crop', NULL),
    (4,  'Áo thun nam Supima Cotton',          'MTS-004', 'Áo thun Supima Cotton cao cấp mềm mại co giãn tốt',                  450, 390000,  'ACTIVE',       4.6, 5, 3, 0,
         'https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg', NULL),
    (5,  'Áo thun nam oversize graphic',       'MTS-005', 'Áo thun oversize in họa tiết graphic trẻ trung',                     310, 350000,  'ON_SALE',      4.2, 5, 7, 0,
         'https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg', NULL),

    -- Quần jeans nam (category_id = 6)
    (6,  'Quần jeans nam slim fit',            'MJN-001', 'Quần jeans co giãn form slim fit trẻ trung',                         980, 590000,  'BESTSELLER',   4.7, 6, 4, 0,
         'https://images.unsplash.com/photo-1563902575-bc2309dc76e4?w=400&h=400&fit=crop', NULL),
    (7,  'Quần jeans nam straight',            'MJN-002', 'Quần jeans ống đứng phong cách cổ điển',                             560, 490000,  'ACTIVE',       4.4, 6, 5, 0,
         'https://images.unsplash.com/photo-1617114919297-3c8ddb01f599?w=400&h=400&fit=crop', NULL),
    (8,  'Quần jeans nam regular',             'MJN-003', 'Quần jeans regular fit thoải mái cho mọi hoạt động',                 720, 450000,  'ACTIVE',       4.5, 6, 3, 0,
         'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop', NULL),

    -- Áo khoác nam (category_id = 7)
    (9,  'Áo khoác gió nam nhẹ',              'MJK-001', 'Áo khoác gió siêu nhẹ chống nước nhẹ tiện lợi',                     430, 690000,  'ACTIVE',       4.6, 7, 1, 0,
         'https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=400&h=400&fit=crop', NULL),
    (10, 'Áo khoác hoodie nam',               'MJK-002', 'Áo hoodie nỉ bông ấm áp có mũ trùm đầu',                           890, 550000,  'BESTSELLER',   4.8, 7, 6, 0,
         'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop', NULL),
    (11, 'Áo khoác bomber nam',               'MJK-003', 'Áo bomber phong cách thời trang đường phố',                          280, 790000,  'ACTIVE',       4.3, 7, 7, 0,
         'https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg', NULL),

    -- Áo thun nữ (category_id = 8)
    (12, 'Áo thun nữ cổ tròn basic',          'WTS-001', 'Áo thun nữ cotton mềm mại thoáng mát',                              1100, 250000, 'BESTSELLER',   4.7, 8, 5, 0,
         'https://fakestoreapi.com/img/71z3kpMAYsL._AC_UY879_.jpg', NULL),
    (13, 'Áo thun nữ crop top',               'WTS-002', 'Áo crop top trẻ trung năng động phối đồ dễ dàng',                    670, 280000,  'ACTIVE',       4.4, 8, 4, 0,
         'https://fakestoreapi.com/img/51eg55uWmdL._AC_UX679_.jpg', NULL),
    (14, 'Áo thun nữ peplum',                 'WTS-003', 'Áo peplum thanh lịch phù hợp đi làm đi chơi',                       340, 420000,  'ACTIVE',       4.5, 8, 8, 0,
         'https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_.jpg', NULL),

    -- Váy đầm (category_id = 9)
    (15, 'Đầm suông công sở',                 'WDR-001', 'Đầm suông thanh lịch phù hợp môi trường công sở',                   520, 680000,  'ACTIVE',       4.6, 9, 8, 0,
         'https://images.unsplash.com/flagged/photo-1585052201332-b8c0ce30972f?w=400&h=400&fit=crop', NULL),
    (16, 'Đầm maxi hoa nhí',                  'WDR-002', 'Đầm maxi họa tiết hoa nhí phong cách vintage',                      380, 590000,  'ON_SALE',      4.3, 9, 4, 0,
         'https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400&h=400&fit=crop', NULL),
    (17, 'Đầm body ôm sát',                   'WDR-003', 'Đầm bodycon tôn dáng quyến rũ cho buổi tiệc',                       290, 750000,  'ACTIVE',       4.4, 9, 8, 0,
         'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop', NULL),

    -- Quần nữ (category_id = 10)
    (18, 'Quần baggy nữ',                     'WPN-001', 'Quần baggy ống rộng thoải mái thời trang',                           810, 390000,  'ACTIVE',       4.5, 10, 5, 0,
         'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=400&h=400&fit=crop', NULL),
    (19, 'Quần legging nữ',                   'WPN-002', 'Quần legging co giãn 4 chiều tập gym yoga',                          950, 350000,  'BESTSELLER',   4.7, 10, 1, 0,
         'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=400&h=400&fit=crop', NULL),

    -- Mũ & Nón (category_id = 11)
    (20, 'Nón lưỡi trai logo',                'HAT-001', 'Nón lưỡi trai thêu logo thời trang unisex',                         1500, 450000, 'BESTSELLER',   4.6, 11, 1, 0,
         'https://images.unsplash.com/photo-1588850561407-ed78c334e67a?w=400&h=400&fit=crop', NULL),
    (21, 'Mũ bucket unisex',                  'HAT-002', 'Mũ bucket phong cách đường phố che nắng tốt',                       680, 350000,  'ACTIVE',       4.3, 11, 2, 0,
         'https://images.unsplash.com/photo-1563319078-0b3ad9b6007a?w=400&h=400&fit=crop', NULL),

    -- Túi xách (category_id = 12)
    (22, 'Balo thời trang nam',               'BAG-001', 'Balo laptop chống nước ngăn chứa rộng rãi',                          730, 890000,  'ACTIVE',       4.5, 12, 1, 0,
         'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop', NULL),
    (23, 'Túi tote nữ canvas',                'BAG-002', 'Túi tote vải canvas phong cách minimalist',                          420, 290000,  'ACTIVE',       4.4, 12, 3, 0,
         'https://images.unsplash.com/photo-1598532163257-ae3c6b2524b6?w=400&h=400&fit=crop', NULL),

    -- Giày thể thao (category_id = 13)
    (24, 'Giày Air Max 90',                   'SNK-001', 'Giày thể thao huyền thoại đệm Air thoải mái',                       1100, 3290000, 'BESTSELLER',  4.9, 13, 1, 0,
         'https://images.pexels.com/photos/2048548/pexels-photo-2048548.jpeg?auto=compress&cs=tinysrgb&w=400', NULL),
    (25, 'Giày Ultraboost',                   'SNK-002', 'Giày chạy bộ công nghệ Boost êm ái nhẹ nhàng',                       870, 3890000, 'ACTIVE',      4.8, 13, 2, 0,
         'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=400', NULL),
    (26, 'Giày Old Skool',                    'SNK-003', 'Giày classic với sọc trắng biểu tượng bên hông',                     950, 1890000, 'BESTSELLER',  4.7, 13, 9, 0,
         'https://images.pexels.com/photos/1240892/pexels-photo-1240892.jpeg?auto=compress&cs=tinysrgb&w=400', NULL),
    (27, 'Giày Chuck Taylor',                 'SNK-004', 'Giày Converse cổ cao biểu tượng thời trang',                         1200, 1590000, 'BESTSELLER', 4.8, 13, 10, 0,
         'https://images.unsplash.com/photo-1601131831144-5d096d7a832c?w=400&h=400&fit=crop', NULL),

    -- Dép & Sandal (category_id = 14)
    (28, 'Dép quai ngang Nike',               'SDL-001', 'Dép quai ngang êm ái cho ngày nghỉ thoải mái',                      1800, 790000,  'ACTIVE',      4.5, 14, 1, 0,
         'https://images.pexels.com/photos/267202/pexels-photo-267202.jpeg?auto=compress&cs=tinysrgb&w=400', NULL),
    (29, 'Sandal Adilette',                   'SDL-002', 'Sandal quai ngang phong cách thể thao cổ điển',                      1300, 690000,  'ACTIVE',      4.4, 14, 2, 0,
         'https://images.unsplash.com/photo-1603487742131-4160ec999306?w=400&h=400&fit=crop', NULL),
    (30, 'Dép xỏ ngón nam',                   'SDL-003', 'Dép xỏ ngón nhẹ nhàng tiện lợi cho mùa hè',                         600, 190000,  'ON_SALE',     4.1, 14, 6, 0,
         'https://images.unsplash.com/photo-1562183241-b937e95585b6?w=400&h=400&fit=crop', NULL)
ON CONFLICT (id) DO UPDATE SET
    name          = EXCLUDED.name,
    code          = EXCLUDED.code,
    description   = EXCLUDED.description,
    quantity_sold = EXCLUDED.quantity_sold,
    price         = EXCLUDED.price,
    status        = EXCLUDED.status,
    rated         = EXCLUDED.rated,
    category_id   = EXCLUDED.category_id,
    provider_id   = EXCLUDED.provider_id,
    img           = EXCLUDED.img,
    video         = EXCLUDED.video;

SELECT setval('tbl_products_id_seq', (SELECT MAX(id) FROM tbl_products));

-- =========================== ITEMS (product-size inventory) ===========================
-- composite PK = (product_id, size_id)

INSERT INTO tbl_items (product_id, size_id, quantity, status) VALUES
    -- Áo thun nam basic (product 1)
    (1, 1, 120, 'AVAILABLE'), (1, 2, 200, 'AVAILABLE'), (1, 3, 180, 'AVAILABLE'),
    (1, 4, 90,  'AVAILABLE'), (1, 5, 40,  'AVAILABLE'), (1, 6, 15,  'AVAILABLE'),
    -- Áo thun Dri-FIT (product 2)
    (2, 1, 60,  'AVAILABLE'), (2, 2, 100, 'AVAILABLE'), (2, 3, 80,  'AVAILABLE'),
    (2, 4, 50,  'AVAILABLE'), (2, 5, 20,  'AVAILABLE'),
    -- Áo thun Trefoil (product 3)
    (3, 1, 45,  'AVAILABLE'), (3, 2, 70,  'AVAILABLE'), (3, 3, 55,  'AVAILABLE'),
    (3, 4, 30,  'AVAILABLE'),
    -- Áo thun Supima (product 4)
    (4, 1, 80,  'AVAILABLE'), (4, 2, 120, 'AVAILABLE'), (4, 3, 100, 'AVAILABLE'),
    (4, 4, 60,  'AVAILABLE'), (4, 5, 25,  'AVAILABLE'),
    -- Áo thun oversize graphic (product 5)
    (5, 2, 50,  'AVAILABLE'), (5, 3, 70,  'AVAILABLE'), (5, 4, 80,  'AVAILABLE'),
    (5, 5, 40,  'AVAILABLE'), (5, 6, 20,  'AVAILABLE'),
    -- Quần jeans slim fit (product 6)
    (6, 1, 40,  'AVAILABLE'), (6, 2, 90,  'AVAILABLE'), (6, 3, 110, 'AVAILABLE'),
    (6, 4, 60,  'AVAILABLE'), (6, 5, 20,  'AVAILABLE'),
    -- Quần jeans straight (product 7)
    (7, 2, 55, 'AVAILABLE'), (7, 3, 70, 'AVAILABLE'), (7, 4, 45, 'AVAILABLE'),
    -- Quần jeans regular (product 8)
    (8, 1, 30, 'AVAILABLE'), (8, 2, 80, 'AVAILABLE'), (8, 3, 100, 'AVAILABLE'),
    (8, 4, 70, 'AVAILABLE'), (8, 5, 35, 'AVAILABLE'),
    -- Áo khoác gió (product 9)
    (9, 2, 40, 'AVAILABLE'), (9, 3, 60, 'AVAILABLE'), (9, 4, 50, 'AVAILABLE'),
    -- Áo hoodie (product 10)
    (10, 1, 30, 'AVAILABLE'), (10, 2, 80, 'AVAILABLE'), (10, 3, 110, 'AVAILABLE'),
    (10, 4, 70, 'AVAILABLE'), (10, 5, 25, 'AVAILABLE'),
    -- Áo bomber (product 11)
    (11, 2, 35, 'AVAILABLE'), (11, 3, 50, 'AVAILABLE'), (11, 4, 40, 'AVAILABLE'),
    -- Áo thun nữ basic (product 12)
    (12, 1, 150, 'AVAILABLE'), (12, 2, 180, 'AVAILABLE'), (12, 3, 120, 'AVAILABLE'),
    (12, 4, 50,  'AVAILABLE'),
    -- Áo crop top (product 13)
    (13, 1, 90, 'AVAILABLE'), (13, 2, 100, 'AVAILABLE'), (13, 3, 60, 'AVAILABLE'),
    -- Áo peplum (product 14)
    (14, 1, 40, 'AVAILABLE'), (14, 2, 65, 'AVAILABLE'), (14, 3, 50, 'AVAILABLE'),
    (14, 4, 20, 'AVAILABLE'),
    -- Đầm suông (product 15)
    (15, 1, 35, 'AVAILABLE'), (15, 2, 55, 'AVAILABLE'), (15, 3, 45, 'AVAILABLE'),
    -- Đầm maxi (product 16)
    (16, 1, 25, 'AVAILABLE'), (16, 2, 40, 'AVAILABLE'), (16, 3, 35, 'AVAILABLE'),
    -- Đầm body (product 17)
    (17, 1, 30, 'AVAILABLE'), (17, 2, 45, 'AVAILABLE'), (17, 3, 20, 'AVAILABLE'),
    -- Quần baggy nữ (product 18)
    (18, 1, 60, 'AVAILABLE'), (18, 2, 90, 'AVAILABLE'), (18, 3, 70, 'AVAILABLE'),
    (18, 4, 30, 'AVAILABLE'),
    -- Quần legging (product 19)
    (19, 1, 100, 'AVAILABLE'), (19, 2, 150, 'AVAILABLE'), (19, 3, 120, 'AVAILABLE'),
    (19, 4, 60,  'AVAILABLE'),
    -- Nón lưỡi trai (product 20)
    (20, 2, 300, 'AVAILABLE'),
    -- Mũ bucket (product 21)
    (21, 2, 200, 'AVAILABLE'),
    -- Balo (product 22)
    (22, 3, 150, 'AVAILABLE'),
    -- Túi tote (product 23)
    (23, 3, 180, 'AVAILABLE'),
    -- Giày Air Max 90 (product 24)
    (24, 1, 40, 'AVAILABLE'), (24, 2, 80, 'AVAILABLE'), (24, 3, 90, 'AVAILABLE'),
    (24, 4, 50, 'AVAILABLE'),
    -- Giày Ultraboost (product 25)
    (25, 1, 35, 'AVAILABLE'), (25, 2, 60, 'AVAILABLE'), (25, 3, 70, 'AVAILABLE'),
    (25, 4, 40, 'AVAILABLE'),
    -- Giày Old Skool (product 26)
    (26, 1, 50, 'AVAILABLE'), (26, 2, 90, 'AVAILABLE'), (26, 3, 80, 'AVAILABLE'),
    (26, 4, 45, 'AVAILABLE'),
    -- Giày Chuck Taylor (product 27)
    (27, 1, 60, 'AVAILABLE'), (27, 2, 100, 'AVAILABLE'), (27, 3, 110, 'AVAILABLE'),
    (27, 4, 55, 'AVAILABLE'), (27, 5, 20, 'AVAILABLE'),
    -- Dép quai ngang Nike (product 28)
    (28, 1, 80, 'AVAILABLE'), (28, 2, 120, 'AVAILABLE'), (28, 3, 100, 'AVAILABLE'),
    (28, 4, 60, 'AVAILABLE'),
    -- Sandal Adilette (product 29)
    (29, 1, 70, 'AVAILABLE'), (29, 2, 100, 'AVAILABLE'), (29, 3, 90, 'AVAILABLE'),
    (29, 4, 50, 'AVAILABLE'),
    -- Dép xỏ ngón (product 30)
    (30, 2, 0, 'OUT_OF_STOCK'), (30, 3, 80, 'AVAILABLE'), (30, 4, 60, 'AVAILABLE')
ON CONFLICT (product_id, size_id) DO UPDATE SET
    quantity = EXCLUDED.quantity,
    status   = EXCLUDED.status;
