-- 모집 분야 테이블 생성
CREATE TABLE recruitment_field (
                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                   post_id BIGINT,
                                   field_name VARCHAR(255) NOT NULL,
                                   total_count INT NOT NULL,
                                   current_count INT NOT NULL DEFAULT 0,
                                   closed BOOLEAN NOT NULL DEFAULT FALSE,
                                   PRIMARY KEY (id)
);

-- 외래키 및 인덱스 추가
ALTER TABLE recruitment_field
    ADD CONSTRAINT FK_recruitment_field_post FOREIGN KEY (post_id) REFERENCES post(id),
    ADD INDEX IDX_recruitment_field_post_id (post_id);

-- 모집 지원 테이블 생성
CREATE TABLE post_application (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  member_id BIGINT NOT NULL,
                                  post_id BIGINT NOT NULL,
                                  field_id BIGINT NOT NULL,
                                  PRIMARY KEY (id)
);

-- 외래키 및 인덱스 추가
ALTER TABLE post_application
    ADD CONSTRAINT FK_post_application_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD INDEX IDX_post_application_member_id (member_id),
    ADD CONSTRAINT FK_post_application_post FOREIGN KEY (post_id) REFERENCES post(id),
    ADD INDEX IDX_post_application_post_id (post_id),
    ADD CONSTRAINT FK_post_application_field FOREIGN KEY (field_id) REFERENCES recruitment_field(id),
    ADD INDEX IDX_post_application_field_id (field_id);

-- Post 테이블에 모집 마감일 컬럼 추가
ALTER TABLE post
    ADD COLUMN recruitment_deadline DATE;
