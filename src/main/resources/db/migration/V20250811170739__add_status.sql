set search_path TO dish_builder_schema;
ALTER TABLE dish_builder_schema.tenant
    ADD status SMALLINT NOT NULL DEFAULT 0;

ALTER TABLE dish_builder_schema."user"
    ADD status SMALLINT NOT NULL DEFAULT 0;
