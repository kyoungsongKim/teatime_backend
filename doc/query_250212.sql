
-- faqs 테이블 생성
CREATE TABLE `faqs` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(128) NOT NULL COLLATE 'utf8_unicode_ci',
    `description` LONGTEXT NULL DEFAULT NULL COLLATE 'utf8_unicode_ci',
    PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

-- user_navigation_history 테이블 생성
CREATE TABLE `user_navigation_history` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `user_id` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
   `menu_name` VARCHAR(50) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
   `accessed_at` DATETIME NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
   `user_agent` VARCHAR(255) NULL DEFAULT '0' COLLATE 'utf8_general_ci',
   `ip_address` VARCHAR(50) NULL DEFAULT '0' COLLATE 'utf8_general_ci',
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `user_id` (`user_id`) USING BTREE,
   INDEX `menu_name` (`menu_name`) USING BTREE
)
    COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

-- assistance_group 테이블 생성
CREATE TABLE `assistance_group` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL COLLATE utf8_general_ci,
    `order` INT NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- assistance 외래키 참조를 위해 먼저 생성
ALTER TABLE assistance
ADD COLUMN group_id INT NULL;  -- NULL 허용 필수 (ON DELETE SET NULL 사용을 위해)

-- assistance 외래키 처리
ALTER TABLE assistance
ADD CONSTRAINT fk_service_group
FOREIGN KEY (group_id) REFERENCES assistance_group(id)
ON UPDATE CASCADE
ON DELETE SET NULL;