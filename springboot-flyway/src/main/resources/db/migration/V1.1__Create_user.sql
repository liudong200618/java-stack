CREATE TABLE  `USER`(
`USER_ID`          INT(11)           NOT NULL AUTO_INCREMENT,
`USER_NAME`        VARCHAR(100)      NOT NULL COMMENT '�û�����',
`AGE`              INT(3)            NOT NULL COMMENT '����',
`CREATED_TIME`     datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP,
`CREATED_BY`       varchar(100)      NOT NULL DEFAULT 'UNKNOWN',
`UPDATED_TIME`     datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP,
`UPDATED_BY`       varchar(100)      NOT NULL DEFAULT 'UNKNOWN',
PRIMARY KEY (`USER_ID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;