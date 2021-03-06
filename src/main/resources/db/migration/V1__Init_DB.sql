create table users (
    id int8 generated by default as identity,
    is_moderator BOOLEAN not null,
    reg_time TIMESTAMP not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    code varchar(255),
    photo TEXT,
    primary key (id)
);

create table posts (
    id int8 generated by default as identity,
    is_active BOOLEAN not null,
    moderation_status varchar(8) default 'NEW' not null,
    moderator_id int8,
    user_id int8 not null,
    time TIMESTAMP not null,
    title varchar(255) not null,
    text TEXT not null,
    view_count int4 not null,
    primary key (id)
);

create table post_votes (
    id int8 generated by default as identity,
    user_id int8 not null,
    post_id int8 not null,
    time TIMESTAMP not null,
    value BOOLEAN not null,
    primary key (id)
);

create table tags (
    id int8 generated by default as identity,
    name varchar(255) not null,
    primary key (id)
);

create table tag2post (
    id int8 generated by default as identity,
    post_id int8 not null,
    tag_id int8 not null,
    primary key (id)
);

create table post_comments (
    id int8 generated by default as identity,
    parent_id int8,
    post_id int8 not null,
    user_id int8 not null,
    time TIMESTAMP not null,
    text TEXT not null,
    primary key (id)
);

create table captcha_codes (
    id int8 generated by default as identity,
    time TIMESTAMP not null,
    code varchar(16) not null,
    secret_code varchar(16) not null,
    primary key (id)
);

create table global_settings (
    id int8 generated by default as identity,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
);

alter table if exists post_comments
    add constraint post_comments_parent_fk
    foreign key (parent_id) references post_comments;

alter table if exists post_comments
    add constraint post_comments_post_fk
    foreign key (post_id) references posts;

alter table if exists post_comments
    add constraint post_comments_user_fk
    foreign key (user_id) references users;

alter table if exists post_votes
    add constraint post_votes_post_fk
    foreign key (post_id) references posts;

alter table if exists post_votes
    add constraint post_votes_user_fk
    foreign key (user_id) references users;

alter table if exists posts
    add constraint posts_moderator_fk
    foreign key (moderator_id) references users;

alter table if exists posts
    add constraint posts_user_fk
    foreign key (user_id) references users;

alter table if exists tag2post
    add constraint tag2post_post_fk
    foreign key (post_id) references posts;

alter table if exists tag2post
    add constraint tag2post_tag_fk
    foreign key (tag_id) references tags;