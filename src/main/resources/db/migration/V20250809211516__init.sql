create schema if not exists dish_builder_schema;

set search_path to dish_builder_schema;

create table if not exists dish_builder_schema.tenant
(
    id         uuid primary key default gen_random_uuid(),
    name       varchar(100) not null,
    phone      varchar(15)  not null,
    email      varchar(150) not null,
    address    varchar(255),
    url_slug   varchar(255) not null,
    logo_url   varchar(255),
    sub_domain varchar(255),
    created_at timestamp        default now(),
    updated_at timestamp        default now(),
    created_by uuid,
    updated_by uuid,
    constraint tenant_email_format_check check ( email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);
create table if not exists  dish_builder_schema.tenant_customization
(
    id                   uuid primary key default gen_random_uuid(),
    primary_color        varchar(10)  not null,
    secondary_color      varchar(10)  not null,
    third_color          varchar(10)  not null,
    background_image_url varchar(255) not null,
    font_style            varchar(255) not null,
    created_at           timestamp        default now(),
    updated_at           timestamp        default now(),
    created_by           uuid,
    updated_by           uuid,
    tenant_id            uuid         not null,
    constraint fk_tenant_customization_tenant
        foreign key (tenant_id) references dish_builder_schema.tenant (id)
);

create table if not exists  dish_builder_schema.user
(
    id            uuid primary key default gen_random_uuid(),
    username      varchar(255) not null,
    password      varchar(255) not null,
    logo_url      varchar(255),
    first_name    varchar(255) not null,
    last_name     varchar(255) not null,
    phone         varchar(255) not null,
    email         varchar(255) not null,
    created_at    timestamp        default now(),
    updated_at    timestamp        default now(),
    created_by    uuid,
    updated_by    uuid,
    tenant_id     uuid         not null,
    register_with int              default 0, --manual=0, fb=1, google=2, microsoft =3
    constraint tenant_email_format_check check ( email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    constraint fk_user_tenant foreign key (tenant_id) references dish_builder_schema.tenant (id)
);

create table if not exists  dish_builder_schema.role
(
    id          uuid primary key default gen_random_uuid(),
    name        varchar(20)  not null,
    description varchar(255) not null,
    created_at  timestamp        default now(),
    updated_at  timestamp        default now(),
    created_by  uuid,
    updated_by  uuid,
    tenant_id   uuid         not null
);

create table if not exists  dish_builder_schema.user_role
(
    user_id UUID NOT NULL REFERENCES dish_builder_schema.user (id),
    role_id UUID NOT NULL REFERENCES dish_builder_schema.role (id),
    PRIMARY KEY (user_id, role_id)
)