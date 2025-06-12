-- chat_room 테이블에 member_count, last_chat_id 컬럼 추가
ALTER TABLE chat_room
    ADD COLUMN member_count bigint NOT NULL DEFAULT 0;
ALTER TABLE chat_room
    ADD COLUMN last_chat_id bigint;