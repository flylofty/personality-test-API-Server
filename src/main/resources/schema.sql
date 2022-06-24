drop table if exists test_result;
drop table if exists test_item;
drop table if exists test;

CREATE TABLE test (
    id             BIGINT UNSIGNED AUTO_INCREMENT,
    title          VARCHAR(255) NOT NULL,
    writer         VARCHAR(255) NOT NULL,
    password       VARCHAR(255) NOT NULL,
    image_name     VARCHAR(255) NOT NULL,
    created_date   DATETIME NOT NULL,
    modified_date  DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE test_result (
    id         BIGINT UNSIGNED AUTO_INCREMENT,
    test_id    BIGINT UNSIGNED NOT NULL,
    result_id  VARCHAR(255) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (test_id) REFERENCES test (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_result_1 ON test_result(test_id);
CREATE INDEX idx_result_2 ON test_result(test_id, result_id);

CREATE TABLE test_item (
    id              BIGINT UNSIGNED AUTO_INCREMENT,
    test_id         BIGINT UNSIGNED NOT NULL,
    question        VARCHAR(255) NOT NULL,
    selection_1     VARCHAR(255) NOT NULL,
    selection_1_id  VARCHAR(255) NOT NULL,
    selection_2     VARCHAR(255) NOT NULL,
    selection_2_id  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (test_id) REFERENCES test (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_item_1 ON test_item(test_id);