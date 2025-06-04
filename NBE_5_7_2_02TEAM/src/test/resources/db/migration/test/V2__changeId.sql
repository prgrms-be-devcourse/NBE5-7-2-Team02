-- 외래 키 삭제
ALTER TABLE chat_member DROP FOREIGN KEY FKkb1n3ojelvlb9s166k70s43ju;
ALTER TABLE chat_member DROP FOREIGN KEY FKnvohh3wx5hc6293ob3kfne72f;
ALTER TABLE chat_message DROP FOREIGN KEY FKd8wlrwm0x4mdjo6m6tvy62ngf;
ALTER TABLE chat_message DROP FOREIGN KEY FKih3s89c9o7hn8qa63bdhdithc;
ALTER TABLE chat_room DROP FOREIGN KEY FKdedif34f1oocp49p9lxh3tglc;
ALTER TABLE follow DROP FOREIGN KEY FKkcoemc64xrm83cdmhyaphcuiu;
ALTER TABLE follow DROP FOREIGN KEY FKtps7gpodlrhxlji90u6r3mlng;
ALTER TABLE likes DROP FOREIGN KEY FKa4vkf1skcfu5r6o5gfb5jf295;
ALTER TABLE likes DROP FOREIGN KEY FKowd6f4s7x9f3w50pvlo6x3b41;
ALTER TABLE post DROP FOREIGN KEY FK83s99f4kx8oiqm3ro0sasmpww;
ALTER TABLE post_image DROP FOREIGN KEY FKsip7qv57jw2fw50g97t16nrjr;
ALTER TABLE post_tag DROP FOREIGN KEY FKac1wdchd2pnur3fl225obmlg0;
ALTER TABLE post_tag DROP FOREIGN KEY FKc2auetuvsec0k566l0eyvr9cs;
ALTER TABLE refresh_token DROP FOREIGN KEY FK5gdbafb2i76hk1ai18ah6an4w;
ALTER TABLE refresh_token_black_list DROP FOREIGN KEY FK982sbr7lb6a7u46bglnx8ghi7;

-- index 삭제
ALTER TABLE post DROP INDEX FK83s99f4kx8oiqm3ro0sasmpww;
ALTER TABLE chat_member DROP INDEX FKkb1n3ojelvlb9s166k70s43ju;
ALTER TABLE chat_member DROP INDEX FKnvohh3wx5hc6293ob3kfne72f;
ALTER TABLE chat_message DROP INDEX FKih3s89c9o7hn8qa63bdhdithc;
ALTER TABLE chat_message DROP INDEX FKd8wlrwm0x4mdjo6m6tvy62ngf;
ALTER TABLE follow DROP INDEX FKtps7gpodlrhxlji90u6r3mlng;
ALTER TABLE follow DROP INDEX FKkcoemc64xrm83cdmhyaphcuiu;
ALTER TABLE likes DROP INDEX FKowd6f4s7x9f3w50pvlo6x3b41;
ALTER TABLE post_tag DROP INDEX FKc2auetuvsec0k566l0eyvr9cs;
ALTER TABLE post_tag DROP INDEX FKac1wdchd2pnur3fl225obmlg0;
ALTER TABLE refresh_token DROP INDEX FK5gdbafb2i76hk1ai18ah6an4w;
ALTER TABLE refresh_token_black_list DROP INDEX FK982sbr7lb6a7u46bglnx8ghi7;
ALTER TABLE chat_room DROP INDEX UKpywjgvgd6fwfd4opqkkmtkng7;
ALTER TABLE likes DROP INDEX UK1poyingfbwf63xw3dq8vj3pvj;

-- id 수정
ALTER TABLE member CHANGE member_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE follow CHANGE follow_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE likes CHANGE likes_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE post CHANGE post_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE tag CHANGE tag_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE post_tag CHANGE post_tag_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE refresh_token CHANGE refresh_token_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE refresh_token_black_list CHANGE refresh_token_black_list_id id BIGINT NOT NULL AUTO_INCREMENT;

-- 외래키 설정
ALTER TABLE post
    ADD CONSTRAINT FK_post_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD INDEX IDX_post_member_id (member_id);
ALTER TABLE chat_room
    ADD CONSTRAINT FK_chat_room_post FOREIGN KEY (post_id) REFERENCES post(id),
    ADD UNIQUE KEY UK_chat_room_post_id (post_id);
ALTER TABLE chat_member
    ADD CONSTRAINT FK_chat_member_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD INDEX IDX_chat_member_member_id (member_id),
    Add CONSTRAINT FK_chat_member_chat_room FOREIGN KEY (chatroom_id) REFERENCES chat_room(id),
    ADD INDEX IDX_chat_member_chatroom_id (chatroom_id);
ALTER TABLE chat_message
    ADD CONSTRAINT FK_chat_message_chat_member FOREIGN KEY (chatroom_member_id) REFERENCES chat_member(id),
    ADD INDEX IDX_chat_message_chatroom_member_id (chatroom_member_id),
    ADD CONSTRAINT FK_chat_message_chat_room FOREIGN KEY (chatroom_id) REFERENCES chat_room(id),
    ADD INDEX IDX_chat_message_chatroom_id (chatroom_id);
ALTER TABLE follow
    ADD CONSTRAINT FK_following_member FOREIGN KEY (following_id) REFERENCES member(id),
    ADD INDEX IDX_follow_following_id (following_id),
    ADD CONSTRAINT FK_follower_member FOREIGN KEY (follower_id) REFERENCES member(id),
    ADD INDEX IDX_follow_follower_id (follower_id);
ALTER TABLE likes
    ADD CONSTRAINT FK_likes_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD INDEX IDX_likes_member_id (member_id),
    ADD CONSTRAINT FK_likes_post FOREIGN KEY (post_id) REFERENCES post(id),
    ADD UNIQUE KEY UK_likes_post_id (post_id);
ALTER TABLE post_image
    ADD CONSTRAINT FK_post_image_post FOREIGN KEY (post_id) REFERENCES post(id);
ALTER TABLE post_tag
    ADD CONSTRAINT FK_post_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id),
    ADD INDEX IDX_post_tag_tag_id (tag_id),
    ADD CONSTRAINT FK_post_tag_post FOREIGN KEY (post_id) REFERENCES post(id),
    ADD INDEX IDX_post_tag_post_id (post_id);
ALTER TABLE refresh_token
    ADD CONSTRAINT FK_refresh_token_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD INDEX IDX_refresh_token_member_id (member_id);
ALTER TABLE refresh_token_black_list
    ADD CONSTRAINT FK_refresh_token_black_list_refresh_token FOREIGN KEY (refresh_token_id) REFERENCES refresh_token(id),
    ADD INDEX IDX_refresh_token_black_list_refresh_token_id (refresh_token_id);