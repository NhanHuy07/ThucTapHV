-- ============================================================
-- Fix: Gán rank 'Bronze' cho các user chưa có rank_id
-- Chạy sau khi đã chạy user_seed.sql
-- ============================================================

UPDATE tbl_users
SET rank_id = (
    SELECT id
    FROM tbl_ranks
    WHERE type = 'Bronze'
    LIMIT 1
)
WHERE rank_id IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS tbl_users_acc
ON tbl_users(acc_id);
