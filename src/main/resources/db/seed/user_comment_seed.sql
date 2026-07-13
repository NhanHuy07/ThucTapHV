-- ============================================================
-- Seed data for Users & Comments (IDEMPOTENT — safe to re-run)
-- Database: PostgreSQL
-- Run AFTER product_seed.sql
--
-- Password cho tất cả tài khoản: Abc@12345
-- BCrypt hash: $2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.
-- ============================================================

-- =========================== ACCOUNTS ===========================

INSERT INTO tbl_accounts (id, username, password, role, email, verify, status)
VALUES
    (101, 'nguyenvanan',    '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'an.nguyen@gmail.com',      TRUE,  'ACTIVE'),
    (102, 'tranbinhchi',    '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'binh.tran@gmail.com',      TRUE,  'ACTIVE'),
    (103, 'lehoangcuong',   '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'cuong.le@gmail.com',       TRUE,  'ACTIVE'),
    (104, 'phamminhdung',   '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'dung.pham@gmail.com',      TRUE,  'ACTIVE'),
    (105, 'hoangthuha',     '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'ha.hoang@gmail.com',       TRUE,  'ACTIVE'),
    (106, 'vodinhkhoa',     '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'khoa.vo@gmail.com',        TRUE,  'ACTIVE'),
    (107, 'dangngoclinh',   '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'linh.dang@gmail.com',      TRUE,  'ACTIVE'),
    (108, 'buithanhmai',    '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'mai.bui@gmail.com',        TRUE,  'ACTIVE'),
    (109, 'ngoquocnam',     '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'nam.ngo@gmail.com',        TRUE,  'ACTIVE'),
    (110, 'duongthuyphuong', '$2a$10$dXJ3SW6G7P50lGmMQgel6uVktDQd6B6TlQ8VjYDd3h3gI0rDVxMT.', 'CUSTOMER', 'phuong.duong@gmail.com',  TRUE,  'ACTIVE')
ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
    password = EXCLUDED.password,
    role     = EXCLUDED.role,
    email    = EXCLUDED.email,
    verify   = EXCLUDED.verify,
    status   = EXCLUDED.status;

-- Reset sequence để account tiếp theo sẽ là MAX(id)+1 (111 nếu không có id nào > 110)
SELECT setval('tbl_accounts_id_seq', (SELECT MAX(id) FROM tbl_accounts), true);

-- =========================== USERS ===========================

INSERT INTO tbl_users (
    id,
    f_name,
    l_name,
    avatar,
    acc_id,
    rank_id
)
VALUES
    ('f47ac10b-58cc-4372-a567-0e02b2c3d401', 'An',     'Nguyễn Văn',   'https://i.pravatar.cc/150?u=an',      101, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Bình',   'Trần Thị',     'https://i.pravatar.cc/150?u=binh',    102, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Cường',  'Lê Hoàng',     'https://i.pravatar.cc/150?u=cuong',   103, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Dũng',   'Phạm Minh',    'https://i.pravatar.cc/150?u=dung',    104, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Hà',     'Hoàng Thu',    'https://i.pravatar.cc/150?u=ha',      105, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Khoa',   'Võ Đình',      'https://i.pravatar.cc/150?u=khoa',    106, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Linh',   'Đặng Ngọc',    'https://i.pravatar.cc/150?u=linh',    107, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Mai',    'Bùi Thanh',    'https://i.pravatar.cc/150?u=mai',     108, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Nam',    'Ngô Quốc',     'https://i.pravatar.cc/150?u=nam',     109, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Phương', 'Dương Thùy',   'https://i.pravatar.cc/150?u=phuong',  110, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1))
ON CONFLICT (id) DO UPDATE SET
    f_name = EXCLUDED.f_name,
    l_name = EXCLUDED.l_name,
    avatar = EXCLUDED.avatar,
    acc_id = EXCLUDED.acc_id,
    rank_id = EXCLUDED.rank_id;

-- =========================== COMMENTS ===========================
-- Mỗi user chỉ review 1 lần / product (PK = product_id, user_id)
-- Rating phân bố sao cho AVG ≈ rated hiện tại của product

INSERT INTO tbl_comments (product_id, user_id, content, rating) VALUES

    -- ===== Product 1: Áo thun nam basic (rated 4.8) → avg(5,5,5,5,4)=4.8 =====
    (1, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Vải cotton mềm mại, mặc rất thoáng. Mua lần 3 rồi, chất lượng ổn định.', 5),
    (1, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Form áo vừa vặn, không bị rộng hay chật. Giao hàng nhanh nữa.', 5),
    (1, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Áo đẹp lắm, mặc đi làm hay đi chơi đều được. Rất hài lòng!', 5),
    (1, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Chất vải tốt, giặt nhiều không bị giãn. Giá hợp lý.', 5),
    (1, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Áo đẹp nhưng màu hơi khác so với hình. Vẫn ổn cho giá này.', 4),

    -- ===== Product 2: Áo thun Dri-FIT (rated 4.5) → avg(5,5,4,4)=4.5 =====
    (2, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Công nghệ Dri-FIT thật sự thoát mồ hôi tốt, chạy bộ mát lắm.', 5),
    (2, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Mặc tập gym rất thoải mái, khô nhanh. Đáng tiền Nike.', 5),
    (2, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Chất lượng tốt nhưng giá hơi cao so với áo thun bình thường.', 4),
    (2, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Áo nhẹ, mau khô. Nhưng size hơi nhỏ so với bảng size thường.', 4),

    -- ===== Product 3: Áo thun Trefoil (rated 4.3) → avg(5,4,4)=4.33 =====
    (3, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Logo Trefoil cổ điển, phối streetwear rất đẹp!', 5),
    (3, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Áo ổn, nhưng vải hơi mỏng hơn mình nghĩ. Vẫn đáng mua.', 4),
    (3, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Mặc đi chơi thì ok, nhưng giặt vài lần thấy phai màu nhẹ.', 4),

    -- ===== Product 4: Áo thun Supima Cotton (rated 4.6) → avg(5,5,4,5,4)=4.6 =====
    (4, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Supima Cotton đúng là khác biệt, mềm mượt hơn cotton thường nhiều.', 5),
    (4, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Vải cao cấp, form đẹp. Uniqlo không bao giờ làm thất vọng.', 5),
    (4, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Chất vải rất mịn nhưng hơi dễ nhăn, cần là hơi phiền.', 4),
    (4, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Mua lần 2, chất lượng vẫn đều. Rất thích sự đơn giản của Uniqlo.', 5),
    (4, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Áo mềm thật sự, nhưng size chart hơi lệch, mua lên 1 size nhé.', 4),

    -- ===== Product 5: Áo oversize graphic (rated 4.2) → avg(5,4,4,4,4)=4.2 =====
    (5, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Graphic đẹp, form oversize đúng trend. Giá sale rất hời!', 5),
    (5, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Áo ok nhưng hình in có dấu hiệu bong sau vài lần giặt.', 4),
    (5, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Form oversize thoải mái, mặc mùa hè mát. Graphic bắt mắt.', 4),
    (5, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Thiết kế đẹp, nhưng vải hơi nóng hơn mong đợi.', 4),
    (5, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Chất áo tạm ổn so với giá tiền. Graphic không bị nhòe.', 4),

    -- ===== Product 6: Quần jeans slim fit (rated 4.7) → avg(5,5,5,4)=4.75 =====
    (6, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Co giãn cực tốt, mặc cả ngày không bí. Form slim rất đẹp.', 5),
    (6, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Quần Zara đúng chuẩn, chất jeans dày dặn mà vẫn thoáng.', 5),
    (6, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Form slim fit chuẩn, phối với áo thun hay sơ mi đều đẹp.', 5),
    (6, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Quần đẹp, nhưng ống hơi bó ở bắp chân với người chân to.', 4),

    -- ===== Product 7: Quần jeans straight (rated 4.4) → avg(5,4,4,5,4)=4.4 =====
    (7, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Ống đứng cổ điển, phối giày gì cũng hợp. Chất jeans bền.', 5),
    (7, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Quần ok, nhưng màu thực tế nhạt hơn hình chút.', 4),
    (7, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Form straight thoải mái, mặc đi làm hay đi chơi đều ổn.', 4),
    (7, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Chất vải dày, đường may chắc chắn. Rất ưng ý.', 5),
    (7, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Quần đẹp, giá tầm trung hợp lý. Hơi cứng khi mới mua.', 4),

    -- ===== Product 8: Quần jeans regular (rated 4.5) → avg(5,5,4,4)=4.5 =====
    (8, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Regular fit thoải mái nhất, mặc cả ngày không mỏi. Uniqlo chất lượng!', 5),
    (8, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Quần jeans Uniqlo giá rẻ mà chất lượng không thua hàng hiệu.', 5),
    (8, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Vải ổn nhưng hơi mỏng so với jeans truyền thống.', 4),
    (8, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Form regular vừa, không rộng quá. Phù hợp đi làm hàng ngày.', 4),

    -- ===== Product 9: Áo khoác gió (rated 4.6) → avg(5,5,4,5,4)=4.6 =====
    (9, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Siêu nhẹ, gấp gọn bỏ túi luôn. Chống gió tốt khi đi xe máy.', 5),
    (9, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Áo khoác Nike chất lượng đỉnh, mặc chạy bộ sáng sớm rất ấm.', 5),
    (9, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Nhẹ và thoáng nhưng chống nước chỉ ở mức nhẹ thôi.', 4),
    (9, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Thiết kế đẹp, tiện lợi. Mua thêm cho cả nhà.', 5),
    (9, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Chất lượng tốt nhưng túi áo hơi nhỏ, không để được điện thoại to.', 4),

    -- ===== Product 10: Hoodie (rated 4.8) → avg(5,5,5,5,4)=4.8 =====
    (10, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Nỉ bông ấm áp, hoodie này mặc mùa đông Đà Lạt là perfect.', 5),
    (10, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Chất nỉ mềm, không xù sau khi giặt. Coolmate chất lượng thật.', 5),
    (10, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Form đẹp, mũ trùm vừa đầu. Màu sắc giống hình.', 5),
    (10, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Hoodie xịn nhất mình từng mua trong tầm giá này!', 5),
    (10, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Áo ấm, đẹp. Chỉ tiếc là không có nhiều màu để chọn.', 4),

    -- ===== Product 11: Bomber (rated 4.3) → avg(5,4,4)=4.33 =====
    (11, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Style bomber cổ điển, mặc với quần jeans cực ngầu!', 5),
    (11, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Áo đẹp nhưng hơi nóng khi mặc trong nhà. Phù hợp ra ngoài.', 4),
    (11, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Chất lượng ổn so với giá, nhưng khóa kéo hơi khó kéo.', 4),

    -- ===== Product 12: Áo thun nữ basic (rated 4.7) → avg(5,5,5,4)=4.75 =====
    (12, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Vải cotton mềm, mặc rất thoải mái. Phối đồ dễ dàng.', 5),
    (12, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Áo basic nhưng chất lượng không basic chút nào! Rất thích.', 5),
    (12, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Mua cả lố mặc thay nhau, giá H&M rất phải chăng.', 5),
    (12, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Áo đẹp, hơi mỏng nhưng mặc mùa hè thì OK.', 4),

    -- ===== Product 13: Crop top (rated 4.4) → avg(5,4,4,5,4)=4.4 =====
    (13, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Crop top Zara kiểu dáng trẻ trung, phối quần cạp cao rất đẹp!', 5),
    (13, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Áo vừa vặn, chất vải co giãn tốt. Size đúng chuẩn.', 4),
    (13, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Kiểu dáng đẹp nhưng vải hơi mỏng, cần mặc áo lót bên trong.', 4),
    (13, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Siêu xinh luôn! Phối với chân váy hay quần jeans đều hợp.', 5),
    (13, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Áo đẹp, nhưng độ dài hơi ngắn hơn mong đợi.', 4),

    -- ===== Product 14: Peplum (rated 4.5) → avg(5,5,4,4)=4.5 =====
    (14, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Áo peplum Ivy Moda thanh lịch, mặc đi làm rất sang.', 5),
    (14, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Chất vải cao cấp xứng đáng với giá. Form rất tôn dáng.', 5),
    (14, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Đẹp nhưng cần chọn size cẩn thận, hơi rộng so với bảng size.', 4),
    (14, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Áo thanh lịch, đi tiệc hay đi làm đều phù hợp.', 4),

    -- ===== Product 15: Đầm suông (rated 4.6) → avg(5,5,4,5,4)=4.6 =====
    (15, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Đầm công sở chuẩn Ivy Moda, chất liệu tốt, không nhăn.', 5),
    (15, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Mặc đi làm cả ngày vẫn thoải mái. Form suông che khuyết điểm tốt.', 5),
    (15, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Đẹp nhưng hơi ngắn, mình cao 1m65 thì vừa đầu gối.', 4),
    (15, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Đầm sang trọng, đồng nghiệp ai cũng khen.', 5),
    (15, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Chất vải đẹp nhưng hơi nóng khi mặc mùa hè.', 4),

    -- ===== Product 16: Đầm maxi (rated 4.3) → avg(5,4,4)=4.33 =====
    (16, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Hoa nhí vintage đẹp lắm, mặc đi biển là chuẩn bài!', 5),
    (16, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Đầm dài nên cần chú ý chiều cao. Mình 1m55 phải sửa lai.', 4),
    (16, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Họa tiết đẹp, nhưng vải hơi mỏng cần mặc lót bên trong.', 4),

    -- ===== Product 17: Đầm bodycon (rated 4.4) → avg(5,4,4,5,4)=4.4 =====
    (17, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Đầm ôm sát tôn dáng cực kỳ! Mặc đi tiệc ai cũng nhìn.', 5),
    (17, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Chất vải co giãn tốt nhưng dễ bám bụi, cần chú ý.', 4),
    (17, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Form ôm body nên phải chọn đúng size. Size M vừa 55kg.', 4),
    (17, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Đầm quyến rũ, Ivy Moda thiết kế rất tinh tế.', 5),
    (17, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Đẹp nhưng giá hơi cao so với mặt bằng chung.', 4),

    -- ===== Product 18: Quần baggy (rated 4.5) → avg(5,5,4,4)=4.5 =====
    (18, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Ống rộng thoải mái, H&M luôn có form đẹp. Rất thích!', 5),
    (18, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Quần baggy này phối áo thun hay áo sơ mi đều hợp.', 5),
    (18, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Vải ổn nhưng nhăn nhanh, cần là ủi thường xuyên.', 4),
    (18, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Quần thoải mái cho mùa hè, giá cả hợp lý.', 4),

    -- ===== Product 19: Legging (rated 4.7) → avg(5,5,5,4)=4.75 =====
    (19, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Co giãn 4 chiều thật sự! Tập yoga mà thoải mái vô cùng.', 5),
    (19, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Legging Nike xịn, không bị trong suốt khi squat. Yên tâm tập.', 5),
    (19, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Chất vải mát, thoáng khí. Mặc chạy bộ cũng rất ổn.', 5),
    (19, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Legging đẹp nhưng dễ bám lông mèo. Ai nuôi thú cưng lưu ý.', 4),

    -- ===== Product 20: Nón lưỡi trai (rated 4.6) → avg(5,5,4,5,4)=4.6 =====
    (20, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Nón Nike chính hãng, logo thêu sắc nét. Chống nắng tốt.', 5),
    (20, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Đội vừa đầu, quai sau điều chỉnh được. Đẹp như hình.', 5),
    (20, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Nón ổn nhưng vải hơi nóng khi đội lâu dưới nắng.', 4),
    (20, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Phong cách sporty, phối đồ thể thao hay casual đều hợp.', 5),
    (20, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Nón đẹp, nhưng size one-size hơi nhỏ với đầu mình.', 4),

    -- ===== Product 21: Mũ bucket (rated 4.3) → avg(5,4,4)=4.33 =====
    (21, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Bucket hat Adidas phong cách đường phố, rất cool!', 5),
    (21, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Che nắng khá tốt, nhưng vành mũ hơi mềm, gió thổi hay bay.', 4),
    (21, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Mũ đẹp, đi chơi phố rất hợp. Giá tầm trung chấp nhận được.', 4),

    -- ===== Product 22: Balo (rated 4.5) → avg(5,5,4,4)=4.5 =====
    (22, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Balo Nike ngăn chứa rộng, để laptop 15 inch thoải mái.', 5),
    (22, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Chống nước nhẹ, đi mưa nhỏ không lo ướt đồ bên trong.', 5),
    (22, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Balo đẹp nhưng quai hơi mỏng, đeo lâu hơi đau vai.', 4),
    (22, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Thiết kế đẹp, nhiều ngăn tiện lợi. Dây kéo hơi cứng ban đầu.', 4),

    -- ===== Product 23: Túi tote (rated 4.4) → avg(5,4,4,5,4)=4.4 =====
    (23, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Túi tote canvas phong cách minimalist, mang đi học đi làm đều ok.', 5),
    (23, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Vải canvas dày dặn, khâu tay cẩn thận. Giá Uniqlo rất phải chăng.', 4),
    (23, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Túi rộng nhưng không có khóa kéo, hơi lo lắng khi đi xe buýt.', 4),
    (23, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Đơn giản mà đẹp, để đồ thoải mái. Mình rất thích style này!', 5),
    (23, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Túi ổn so với giá, nhưng quai hơi ngắn khi đeo vai.', 4),

    -- ===== Product 24: Air Max 90 (rated 4.9) → avg(5,5,5,5,5,5,5,5,5,4)=4.9 =====
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Huyền thoại Air Max! Đệm Air êm ái, đi cả ngày không mỏi chân.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Giày đẹp xuất sắc, chất lượng Nike không phải bàn.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Air Max 90 kinh điển, mang mãi không chán. Worth every penny!', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Êm chân, nhẹ, phong cách retro rất đẹp. 10/10!', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Mua tặng bạn trai, anh ấy mê lắm. Đóng gói cẩn thận.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Đi bộ cả ngày mà chân vẫn thoải mái. Công nghệ Air xịn thật.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Phối đồ cực dễ, mang với quần jeans hay jogger đều đẹp.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Chất lượng tuyệt vời, đúng hàng chính hãng Nike.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Giày iconic, mua đúng size là chuẩn luôn. Không cần break in.', 5),
    (24, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Giày đẹp lắm nhưng giá hơi cao. Nên canh sale để mua.', 4),

    -- ===== Product 25: Ultraboost (rated 4.8) → avg(5,5,5,5,4)=4.8 =====
    (25, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Boost êm nhất thị trường! Chạy bộ 10km mà chân như đi trên mây.', 5),
    (25, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Nhẹ vô cùng, đế Boost hấp thụ lực rất tốt khi chạy.', 5),
    (25, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Adidas Ultraboost không hổ danh, mang suốt ngày không đau chân.', 5),
    (25, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Giày chạy bộ tốt nhất mình từng mua. Rất recommend!', 5),
    (25, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Giày êm, nhẹ nhưng đế Boost dễ bám bụi bẩn khó lau.', 4),

    -- ===== Product 26: Old Skool (rated 4.7) → avg(5,5,5,4)=4.75 =====
    (26, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Vans Old Skool kinh điển, đi đâu cũng được compliment.', 5),
    (26, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Sọc trắng bên hông quá iconic! Phối streetwear là đỉnh.', 5),
    (26, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Giày đẹp, bền bỉ. Mang gần 2 năm vẫn chưa hỏng.', 5),
    (26, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Classic đẹp nhưng đế hơi cứng, cần thời gian break in.', 4),

    -- ===== Product 27: Chuck Taylor (rated 4.8) → avg(5,5,5,5,4)=4.8 =====
    (27, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Converse cổ cao huyền thoại! Phối quần jeans là bất bại.', 5),
    (27, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Giày Converse biểu tượng, ai cũng nên có 1 đôi trong tủ.', 5),
    (27, 'f47ac10b-58cc-4372-a567-0e02b2c3d405', 'Mua đôi trắng classic, đi với gì cũng đẹp.', 5),
    (27, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Converse All Star luôn là lựa chọn an toàn cho phong cách casual.', 5),
    (27, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Giày đẹp nhưng đế bằng phẳng, đi lâu hơi mỏi so với giày có đệm.', 4),

    -- ===== Product 28: Dép quai ngang Nike (rated 4.5) → avg(5,5,4,4)=4.5 =====
    (28, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Dép Nike êm chân, mang quanh nhà hay ra ngoài đều tiện.', 5),
    (28, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Quai ngang Nike êm ái, phù hợp mang sau khi tập gym xong.', 5),
    (28, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Dép đẹp nhưng đế hơi trơn khi đi trên sàn ướt.', 4),
    (28, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Êm chân, thiết kế đơn giản. Giá cao hơn dép thường nhiều.', 4),

    -- ===== Product 29: Sandal Adilette (rated 4.4) → avg(5,4,4,5,4)=4.4 =====
    (29, 'f47ac10b-58cc-4372-a567-0e02b2c3d402', 'Adilette cổ điển, ba sọc quen thuộc. Mang mùa hè siêu mát.', 5),
    (29, 'f47ac10b-58cc-4372-a567-0e02b2c3d403', 'Sandal ok nhưng quai hơi cứng khi mới mua, cần break in.', 4),
    (29, 'f47ac10b-58cc-4372-a567-0e02b2c3d407', 'Mang thoải mái, nhưng dễ trượt khi chân ướt.', 4),
    (29, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Sandal đẹp, đi biển hay đi dạo đều hợp. Logo 3 sọc nổi bật.', 5),
    (29, 'f47ac10b-58cc-4372-a567-0e02b2c3d410', 'Chất lượng Adidas đúng chuẩn, nhưng giá khá đắt cho 1 đôi sandal.', 4),

    -- ===== Product 30: Dép xỏ ngón (rated 4.1) → avg(5,4,4,3,4)=4.0 =====
    (30, 'f47ac10b-58cc-4372-a567-0e02b2c3d401', 'Dép xỏ ngón Coolmate giá rẻ mà êm, đi ở nhà rất tiện.', 5),
    (30, 'f47ac10b-58cc-4372-a567-0e02b2c3d404', 'Dép nhẹ, thoải mái cho mùa hè. Nhưng quai hơi mỏng.', 4),
    (30, 'f47ac10b-58cc-4372-a567-0e02b2c3d406', 'Dép ok cho giá tiền, mang trong nhà thì ổn.', 4),
    (30, 'f47ac10b-58cc-4372-a567-0e02b2c3d408', 'Dép mỏng quá, đế mòn nhanh. Chắc chỉ dùng được vài tháng.', 3),
    (30, 'f47ac10b-58cc-4372-a567-0e02b2c3d409', 'Giá sale hợp lý nhưng chất lượng cũng chỉ tầm đó thôi.', 4)

ON CONFLICT (product_id, user_id) DO UPDATE SET
    content = EXCLUDED.content,
    rating  = EXCLUDED.rating;

-- ===== Cập nhật lại rated của product dựa trên AVG(comments) =====
UPDATE tbl_products p
SET rated = sub.avg_rating
FROM (
    SELECT product_id, ROUND(AVG(rating)::numeric, 1)::real AS avg_rating
    FROM tbl_comments
    GROUP BY product_id
) sub
WHERE p.id = sub.product_id;
