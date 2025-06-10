CREATE TABLE IF NOT EXISTS `book` (
                        `book_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                        `title` VARCHAR(255) NOT NULL,
                        `description` TEXT,
                        `toc` TEXT,
                        `publisher` VARCHAR(255) NOT NULL,
                        `author` VARCHAR(255) NOT NULL,
                        `publisher_at` DATE NOT NULL,
                        `isbn` VARCHAR(13) NOT NULL,
                        `price_original` INT NOT NULL,
                        `price_sale` INT NOT NULL,
                        `is_gift_wrappable` BOOLEAN NOT NULL,
                        `created_at` DATETIME NOT NULL,
                        `updated_at` DATETIME DEFAULT NULL,
                        `status` VARCHAR(20) NOT NULL COMMENT '판매중 or 판매종료',
                        `stock` INT NOT NULL
);
