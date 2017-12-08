/*
    DROP TABLE IF EXISTS `ignition`.`user`;
*/

CREATE TABLE `ignition`.`user` (
  `user_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (user_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;