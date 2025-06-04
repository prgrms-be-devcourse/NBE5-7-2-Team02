--
-- Table structure for table `member`
--
CREATE TABLE `member`
(
    `member_id`     bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)             DEFAULT NULL,
    `updated_at`    datetime(6)             DEFAULT NULL,
    `course`        varchar(255)            DEFAULT NULL,
    `email`         varchar(255) NOT NULL,
    `github_id`     varchar(255) NOT NULL,
    `job`           varchar(255)            DEFAULT NULL,
    `name`          varchar(255)            DEFAULT NULL,
    `profile_image` varchar(255)            DEFAULT NULL,
    `role`          enum ('ADMIN','MEMBER') DEFAULT NULL,
    PRIMARY KEY (`member_id`)
);

--
-- Table structure for table `post`
--
CREATE TABLE `post`
(
    `post_id`            bigint       NOT NULL AUTO_INCREMENT,
    `created_at`         datetime(6)                       DEFAULT NULL,
    `updated_at`         datetime(6)                       DEFAULT NULL,
    `content`            text         NOT NULL,
    `recruitment_status` enum ('DONE','NONE','RECRUITING') DEFAULT NULL,
    `title`              varchar(255) NOT NULL,
    `member_id`          bigint DEFAULT NULL,
    PRIMARY KEY (`post_id`),
    KEY `FK83s99f4kx8oiqm3ro0sasmpww` (`member_id`),
    CONSTRAINT `FK83s99f4kx8oiqm3ro0sasmpww` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
);

--
-- Table structure for table `chat_room`
--
CREATE TABLE `chat_room`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `post_id`    bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKpywjgvgd6fwfd4opqkkmtkng7` (`post_id`),
    CONSTRAINT `FKdedif34f1oocp49p9lxh3tglc` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`)
);

--
-- Table structure for table `chat_member`
--
CREATE TABLE `chat_member`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)                      DEFAULT NULL,
    `updated_at`  datetime(6)                      DEFAULT NULL,
    `status`      enum ('LEFT','OFFLINE','ONLINE') DEFAULT NULL,
    `chatroom_id` bigint DEFAULT NULL,
    `member_id`   bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKkb1n3ojelvlb9s166k70s43ju` (`chatroom_id`),
    KEY `FKnvohh3wx5hc6293ob3kfne72f` (`member_id`),
    CONSTRAINT `FKkb1n3ojelvlb9s166k70s43ju` FOREIGN KEY (`chatroom_id`) REFERENCES `chat_room` (`id`),
    CONSTRAINT `FKnvohh3wx5hc6293ob3kfne72f` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
);

--
-- Table structure for table `chat_message`
--
CREATE TABLE `chat_message`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `created_at`         datetime(6)  DEFAULT NULL,
    `updated_at`         datetime(6)  DEFAULT NULL,
    `content`            varchar(255) DEFAULT NULL,
    `chatroom_member_id` bigint DEFAULT NULL,
    `chatroom_id`        bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKih3s89c9o7hn8qa63bdhdithc` (`chatroom_member_id`),
    KEY `FKd8wlrwm0x4mdjo6m6tvy62ngf` (`chatroom_id`),
    CONSTRAINT `FKd8wlrwm0x4mdjo6m6tvy62ngf` FOREIGN KEY (`chatroom_id`) REFERENCES `chat_room` (`id`),
    CONSTRAINT `FKih3s89c9o7hn8qa63bdhdithc` FOREIGN KEY (`chatroom_member_id`) REFERENCES `chat_member` (`id`)
);

--
-- Table structure for table `follow`
--
CREATE TABLE `follow`
(
    `follow_id`    bigint NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6) DEFAULT NULL,
    `updated_at`   datetime(6) DEFAULT NULL,
    `follower_id`  bigint NOT NULL,
    `following_id` bigint NOT NULL,
    PRIMARY KEY (`follow_id`),
    KEY `FKtps7gpodlrhxlji90u6r3mlng` (`follower_id`),
    KEY `FKkcoemc64xrm83cdmhyaphcuiu` (`following_id`),
    CONSTRAINT `FKkcoemc64xrm83cdmhyaphcuiu` FOREIGN KEY (`following_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `FKtps7gpodlrhxlji90u6r3mlng` FOREIGN KEY (`follower_id`) REFERENCES `member` (`member_id`)
);

--
-- Table structure for table `likes`
--
CREATE TABLE `likes`
(
    `likes_id`  bigint NOT NULL AUTO_INCREMENT,
    `member_id` bigint DEFAULT NULL,
    `post_id`   bigint DEFAULT NULL,
    PRIMARY KEY (`likes_id`),
    UNIQUE KEY `UK1poyingfbwf63xw3dq8vj3pvj` (`member_id`,`post_id`),
    KEY `FKowd6f4s7x9f3w50pvlo6x3b41` (`post_id`),
    CONSTRAINT `FKa4vkf1skcfu5r6o5gfb5jf295` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `FKowd6f4s7x9f3w50pvlo6x3b41` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`)
);

--
-- Table structure for table `post_image`
--
CREATE TABLE `post_image`
(
    `post_id`     bigint NOT NULL,
    `image_url`   varchar(255) DEFAULT NULL,
    `image_order` int    NOT NULL,
    PRIMARY KEY (`post_id`, `image_order`),
    CONSTRAINT `FKsip7qv57jw2fw50g97t16nrjr` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`)
);

--
-- Table structure for table `tag`
--
CREATE TABLE `tag`
(
    `tag_id` bigint       NOT NULL AUTO_INCREMENT,
    `name`   varchar(255) NOT NULL,
    PRIMARY KEY (`tag_id`)
);

--
-- Table structure for table `post_tag`
--
CREATE TABLE `post_tag`
(
    `post_tag_id` bigint NOT NULL AUTO_INCREMENT,
    `post_id`     bigint DEFAULT NULL,
    `tag_id`      bigint DEFAULT NULL,
    PRIMARY KEY (`post_tag_id`),
    KEY `FKc2auetuvsec0k566l0eyvr9cs` (`post_id`),
    KEY `FKac1wdchd2pnur3fl225obmlg0` (`tag_id`),
    CONSTRAINT `FKac1wdchd2pnur3fl225obmlg0` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`),
    CONSTRAINT `FKc2auetuvsec0k566l0eyvr9cs` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`)
);

--
-- Table structure for table `refresh_token`
--
CREATE TABLE `refresh_token`
(
    `refresh_token_id` bigint NOT NULL AUTO_INCREMENT,
    `created_at`       datetime(6)  DEFAULT NULL,
    `updated_at`       datetime(6)  DEFAULT NULL,
    `refresh_token`    varchar(255) DEFAULT NULL,
    `member_id`        bigint NOT NULL,
    PRIMARY KEY (`refresh_token_id`),
    KEY `FK5gdbafb2i76hk1ai18ah6an4w` (`member_id`),
    CONSTRAINT `FK5gdbafb2i76hk1ai18ah6an4w` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
);

--
-- Table structure for table `refresh_token_black_list`
--
CREATE TABLE `refresh_token_black_list`
(
    `refresh_token_black_list_id` bigint NOT NULL AUTO_INCREMENT,
    `created_at`                  datetime(6) DEFAULT NULL,
    `updated_at`                  datetime(6) DEFAULT NULL,
    `refresh_token_id`            bigint NOT NULL,
    PRIMARY KEY (`refresh_token_black_list_id`),
    KEY `FK982sbr7lb6a7u46bglnx8ghi7` (`refresh_token_id`),
    CONSTRAINT `FK982sbr7lb6a7u46bglnx8ghi7` FOREIGN KEY (`refresh_token_id`) REFERENCES `refresh_token` (`refresh_token_id`)
);