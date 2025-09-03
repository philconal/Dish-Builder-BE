create schema dish_builder_schema;

CREATE TABLE dish_builder_schema.category
(
    id          UUID NOT NULL,
    created_by  UUID,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_by  UUID,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    description VARCHAR(255),
    tenant_id   UUID,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema.dish_ingredients
(
    dish_id       UUID NOT NULL,
    ingredient_id UUID NOT NULL
);

CREATE TABLE dish_builder_schema.dishes
(
    id          UUID NOT NULL,
    created_by  UUID,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_by  UUID,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    description VARCHAR(255),
    tenant_id   UUID,
    total_price DECIMAL,
    discount    DECIMAL,
    vat         DECIMAL,
    user_id     UUID NOT NULL,
    CONSTRAINT pk_dishes PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema.ingredients
(
    id          UUID NOT NULL,
    created_by  UUID,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_by  UUID,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    description VARCHAR(255),
    tenant_id   UUID,
    price       DECIMAL,
    category_id UUID NOT NULL,
    CONSTRAINT pk_ingredients PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema.role
(
    id          UUID         NOT NULL,
    created_by  UUID,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_by  UUID,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(20)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema.tenant
(
    id         UUID         NOT NULL,
    created_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by UUID,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    name       VARCHAR(100) NOT NULL,
    phone      VARCHAR(15)  NOT NULL,
    email      VARCHAR(150) NOT NULL,
    address    VARCHAR(255),
    url_slug   VARCHAR(255) NOT NULL,
    logo_url   VARCHAR(255),
    sub_domain VARCHAR(255),
    status     SMALLINT     NOT NULL,
    CONSTRAINT pk_tenant PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema.tenant_customization
(
    id                   UUID         NOT NULL,
    created_by           UUID,
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_by           UUID,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    primary_color        VARCHAR(10)  NOT NULL,
    secondary_color      VARCHAR(10)  NOT NULL,
    third_color          VARCHAR(10)  NOT NULL,
    background_image_url VARCHAR(255) NOT NULL,
    font_style           VARCHAR(255) NOT NULL,
    tenant_id            UUID         NOT NULL,
    CONSTRAINT pk_tenant_customization PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema."user"
(
    id            UUID         NOT NULL,
    created_by    UUID,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_by    UUID,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    username      VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    logo_url      VARCHAR(255),
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    phone         VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    register_with INTEGER DEFAULT 0,
    status        SMALLINT     NOT NULL,
    user_type     SMALLINT     NOT NULL,
    tenant_id     UUID         NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE dish_builder_schema.user_role
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (role_id, user_id)
);

ALTER TABLE dish_builder_schema."user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE dish_builder_schema."user"
    ADD CONSTRAINT uc_user_username UNIQUE (username);

ALTER TABLE dish_builder_schema.dishes
    ADD CONSTRAINT FK_DISH_USER FOREIGN KEY (user_id) REFERENCES dish_builder_schema."user" (id);

ALTER TABLE dish_builder_schema.ingredients
    ADD CONSTRAINT FK_INGREDIENT_CATEGORY FOREIGN KEY (category_id) REFERENCES dish_builder_schema.category (id);

ALTER TABLE dish_builder_schema.tenant_customization
    ADD CONSTRAINT FK_TENANT_CUSTOMIZATION_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES dish_builder_schema.tenant (id);

ALTER TABLE dish_builder_schema."user"
    ADD CONSTRAINT FK_USER_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES dish_builder_schema.tenant (id);

ALTER TABLE dish_builder_schema.dish_ingredients
    ADD CONSTRAINT fk_dising_on_dish_entity FOREIGN KEY (dish_id) REFERENCES dish_builder_schema.dishes (id);

ALTER TABLE dish_builder_schema.dish_ingredients
    ADD CONSTRAINT fk_dising_on_ingredients_entity FOREIGN KEY (ingredient_id) REFERENCES dish_builder_schema.ingredients (id);

ALTER TABLE dish_builder_schema.user_role
    ADD CONSTRAINT fk_user_role_on_role_entity FOREIGN KEY (role_id) REFERENCES dish_builder_schema.role (id);

ALTER TABLE dish_builder_schema.user_role
    ADD CONSTRAINT fk_user_role_on_user_entity FOREIGN KEY (user_id) REFERENCES dish_builder_schema."user" (id);