CREATE DATABASE IF NOT EXISTS image_sharing DEFAULT CHARACTER SET utf8;

USE image_sharing;

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(256) NOT NULL,
  password VARCHAR(256) NOT NULL,
  email_address VARCHAR(256) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id)
) ENGINE = 'InnoDB', DEFAULT CHARSET=utf8mb4, ROW_FORMAT=DYNAMIC;

INSERT INTO users VALUES (1, 'suzuki',     'aaaa', 'aaaa@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (2, 'John',       'bbbb', 'bbbb@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (3, 'Johann',     'cccc', 'cccc@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (4, 'Sebastian',  'dddd', 'dddd@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (5, 'Bach',       'eeee', 'eeee@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (6, 'Jekyll',     'ffff', 'ffff@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (7, 'Hyde',       'gggg', 'gggg@gmail.com', current_timestamp, current_timestamp);
INSERT INTO users VALUES (8, 'Einstein',   'hhhh', 'hhhh@gmail.com', current_timestamp, current_timestamp);
