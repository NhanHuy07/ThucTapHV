-- ============================================================
-- Seed data for 50 Users — IDEMPOTENT (Fixed version)
-- Database: PostgreSQL
-- Password plain-text: 123456
-- Password BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- Run AFTER schema.sql (migrations)
-- ============================================================

-- ===================== BƯỚC 0: Dọn duplicate nếu có =====================
DELETE FROM tbl_users
WHERE id NOT IN (
    SELECT DISTINCT ON (acc_id) id
    FROM tbl_users
    WHERE acc_id IS NOT NULL
    ORDER BY acc_id, created_at ASC
);

-- ===================== tbl_accounts =====================
INSERT INTO tbl_accounts (username, password, role, email, verify, status) VALUES
    ('nguyenvana',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'nguyenvana@gmail.com',   true,  'ACTIVE'),
    ('tranthib',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'tranthib@gmail.com',     true,  'ACTIVE'),
    ('lehongc',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'lehongc@gmail.com',      true,  'ACTIVE'),
    ('phamquocd',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'phamquocd@gmail.com',    true,  'ACTIVE'),
    ('hoangmine',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'hoangmine@gmail.com',    true,  'ACTIVE'),
    ('vuthif',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'vuthif@gmail.com',       true,  'ACTIVE'),
    ('dangvangh',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'dangvangh@gmail.com',    true,  'ACTIVE'),
    ('buithih',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'buithih@gmail.com',      true,  'ACTIVE'),
    ('dothanhj',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'dothanhj@gmail.com',     false, 'ACTIVE'),
    ('ngothik',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'ngothik@gmail.com',      true,  'ACTIVE'),
    ('lyvanhlong',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'lyvanhlong@gmail.com',   true,  'ACTIVE'),
    ('trinhthim',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'trinhthim@gmail.com',    true,  'ACTIVE'),
    ('phanvann',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'phanvann@gmail.com',     true,  'ACTIVE'),
    ('lethio',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'lethio@gmail.com',       true,  'ACTIVE'),
    ('maithip',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'maithip@gmail.com',      true,  'ACTIVE'),
    ('caovansq',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'caovansq@gmail.com',     false, 'ACTIVE'),
    ('dinhthir',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'dinhthir@gmail.com',     true,  'ACTIVE'),
    ('nguyenhais',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'nguyenhais@gmail.com',   true,  'ACTIVE'),
    ('tranvanlt',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'tranvanlt@gmail.com',    true,  'ACTIVE'),
    ('luongthiu',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'luongthiu@gmail.com',    true,  'ACTIVE'),
    ('vothiv',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'vothiv@gmail.com',       true,  'ACTIVE'),
    ('truongvanw',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'truongvanw@gmail.com',   true,  'ACTIVE'),
    ('ngothanhx',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'ngothanhx@gmail.com',    false, 'ACTIVE'),
    ('hathiy',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'hathiy@gmail.com',       true,  'ACTIVE'),
    ('trongvanz',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'trongvanz@gmail.com',    true,  'ACTIVE'),
    ('lamthiai',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'lamthiai@gmail.com',     true,  'ACTIVE'),
    ('kieuvanbi',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'kieuvanbi@gmail.com',    true,  'ACTIVE'),
    ('ngosuci',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'ngosuci@gmail.com',      true,  'ACTIVE'),
    ('duongthidi',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'duongthidi@gmail.com',   true,  'ACTIVE'),
    ('lyvanei',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'lyvanei@gmail.com',      true,  'ACTIVE'),
    ('quanghafi',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'quanghafi@gmail.com',    true,  'ACTIVE'),
    ('bachthigi',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'bachthigi@gmail.com',    false, 'ACTIVE'),
    ('thaivanhhi',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'thaivanhhi@gmail.com',   true,  'ACTIVE'),
    ('huynhthiii',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'huynhthiii@gmail.com',   true,  'ACTIVE'),
    ('sontranji',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'sontranki@gmail.com',    true,  'ACTIVE'),
    ('longnguyki',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'longnguyki@gmail.com',   true,  'ACTIVE'),
    ('hanhleli',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'hanhleli@gmail.com',     true,  'ACTIVE'),
    ('phucdo01',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'phucdo01@gmail.com',     true,  'ACTIVE'),
    ('minhtran02',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'minhtran02@gmail.com',   false, 'ACTIVE'),
    ('thuhang03',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'thuhang03@gmail.com',    true,  'ACTIVE'),
    ('quynhle04',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'quynhle04@gmail.com',    true,  'ACTIVE'),
    ('tuantran05',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'tuantran05@gmail.com',   true,  'ACTIVE'),
    ('lanpham06',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'lanpham06@gmail.com',    true,  'ACTIVE'),
    ('khanhngo07',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'khanhngo07@gmail.com',   true,  'ACTIVE'),
    ('hieule08',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'hieule08@gmail.com',     true,  'ACTIVE'),
    ('namvu09',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'namvu09@gmail.com',      false, 'ACTIVE'),
    ('anhnguyen10',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'anhnguyen10@gmail.com',  true,  'ACTIVE'),
    ('tuyenho11',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'tuyenho11@gmail.com',    true,  'ACTIVE'),
    ('ngocbui12',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'ngocbui12@gmail.com',    true,  'ACTIVE'),
    ('thanhtrinh13', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER', 'thanhtrinh13@gmail.com', true,  'ACTIVE'),
    ('lmaoqua123', '12345678', 'ADMIN', 'bacon@gmail.com', true,  'ACTIVE'),
    ('lmaoqua1234', '12345678', 'EMPLOYEE', 'baconTIME@gmail.com', true,  'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- ===================== tbl_users =====================
-- Dùng WHERE NOT EXISTS để đảm bảo mỗi acc_id chỉ có 1 row (idempotent thật sự)
INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn A',    'Nguyễn', a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'nguyenvana'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị B',   'Trần',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'tranthib'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Hồng C',  'Lê',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'lehongc'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Quốc D',  'Phạm',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'phamquocd'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Minh E',  'Hoàng',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'hoangmine'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị F',   'Vũ',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'vuthif'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn G',   'Đặng',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'dangvangh'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị H',   'Bùi',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'buithih'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thành J', 'Đỗ',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'dothanhj'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị K',   'Ngô',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'ngothik'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn Long','Lý',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'lyvanhlong'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị M',   'Trịnh',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'trinhthim'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn N',   'Phan',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'phanvann'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị O',   'Lê',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'lethio'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị P',   'Mai',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'maithip'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn S',   'Cao',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'caovansq'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị R',   'Đinh',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'dinhthir'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Hải S',   'Nguyễn', a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'nguyenhais'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn T',   'Trần',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'tranvanlt'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị U',   'Lương',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'luongthiu'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị V',   'Võ',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'vothiv'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn W',   'Trương', a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'truongvanw'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thành X', 'Ngô',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'ngothanhx'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị Y',   'Hà',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'hathiy'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn Z',   'Trọng',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'trongvanz'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị Ai',  'Lâm',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'lamthiai'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn Bi',  'Kiều',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'kieuvanbi'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Sú Ci',   'Ngô',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'ngosuci'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị Di',  'Dương',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'duongthidi'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn Ei',  'Lý',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'lyvanei'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Hà Fi',   'Quảng',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'quanghafi'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị Gi',  'Bạch',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'bachthigi'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Văn Hi',  'Thái',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'thaivanhhi'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thị Ii',  'Huỳnh',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'huynhthiii'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Sơn Ji',  'Trần',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'sontranji'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Long Ki', 'Nguy',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'longnguyki'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Hạnh Li', 'Lê',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'hanhleli'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Phúc',    'Đỗ',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'phucdo01'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Minh',    'Trần',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'minhtran02'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thu Hằng','',       a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'thuhang03'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Quỳnh',   'Lê',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'quynhle04'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Tuấn',    'Trần',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'tuantran05'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Lan',     'Phạm',   a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'lanpham06'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Khánh',   'Ngô',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'khanhngo07'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Hiếu',    'Lê',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'hieule08'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Nam',     'Vũ',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'namvu09'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Ánh',     'Nguyễn', a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'anhnguyen10'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Tuyến',   'Hồ',     a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'tuyenho11'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Ngọc',    'Bùi',    a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'ngocbui12'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);

INSERT INTO tbl_users (f_name, l_name, acc_id, rank_id)
SELECT 'Thành',   'Trịnh',  a.id, (SELECT id FROM tbl_ranks WHERE type = 'Bronze' LIMIT 1)
FROM tbl_accounts a WHERE a.username = 'thanhtrinh13'
AND NOT EXISTS (SELECT 1 FROM tbl_users u WHERE u.acc_id = a.id);
