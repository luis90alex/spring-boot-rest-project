-- V2__add_cart_table_h2.sql
-- Cart table compatible con H2
CREATE TABLE cart (
                      id CHAR(36) NOT NULL,  -- to store UUID.toString()
                      date_created DATE DEFAULT CURRENT_DATE NOT NULL,
                      CONSTRAINT pk_cart PRIMARY KEY (id)
);

