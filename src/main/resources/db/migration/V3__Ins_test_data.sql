INSERT INTO users (is_moderator, reg_time, name, email, password)
VALUES (false,TIMESTAMP '2019-08-04 01:00:00','Ivan Ivanov','ivanov@mail.ru','$2a$12$OgBxbLWsr/YWczxGhDI1OuAG3eZ2OrPqAv6RmDIx0mGzoEKTAzpE.');
INSERT INTO users (is_moderator, reg_time, name, email, password)
VALUES (true,TIMESTAMP '2019-06-04 01:00:00','Vasiliy Petrov','petrov@mail.ru','$2a$12$eahL8rF5kgdDZ.Zx5woiSO80khoSSiJUtJBBOCMkVhjjJ67.MCnlO');

INSERT INTO posts (is_active, moderation_status, moderator_id, text, time, title, user_id, view_count)
VALUES (true, 'ACCEPTED', 2, 'test01', TIMESTAMP '2019-10-02 10:23:54', 'title01', 1, 2);
INSERT INTO posts (is_active, moderation_status, moderator_id, text, time, title, user_id, view_count)
VALUES (true, 'ACCEPTED', 2, 'test02', TIMESTAMP '2020-08-02 10:23:54', 'testWord02', 2, 5);
INSERT INTO posts (is_active, moderation_status, moderator_id, text, time, title, user_id, view_count)
VALUES (true, 'ACCEPTED', 2, 'test03', TIMESTAMP '2020-08-03 10:23:54', 'title03', 1, 9);
INSERT INTO posts (is_active, moderation_status, moderator_id, text, time, title, user_id, view_count)
VALUES (true, 'ACCEPTED', 2, 'test04', TIMESTAMP '2020-08-04 10:23:54', 'title04', 2, 6);

INSERT INTO tags (name) VALUES ('java');
INSERT INTO tags (name) VALUES ('spring');

INSERT INTO tag2post (post_id, tag_id) VALUES (1, 1);
INSERT INTO tag2post (post_id, tag_id) VALUES (2, 1);
INSERT INTO tag2post (post_id, tag_id) VALUES (3, 1);
INSERT INTO tag2post (post_id, tag_id) VALUES (1, 2);
INSERT INTO tag2post (post_id, tag_id) VALUES (2, 2);

INSERT INTO post_votes (user_id, post_id, time, value) VALUES (2, 1, TIMESTAMP '2020-08-04 10:23:54', true);
INSERT INTO post_votes (user_id, post_id, time, value) VALUES (2, 4, TIMESTAMP '2020-08-04 10:23:54', true);
INSERT INTO post_votes (user_id, post_id, time, value) VALUES (1, 4, TIMESTAMP '2020-08-04 10:23:54', true);
INSERT INTO post_votes (user_id, post_id, time, value) VALUES (1, 2, TIMESTAMP '2020-08-04 10:23:54', false);
INSERT INTO post_votes (user_id, post_id, time, value) VALUES (2, 3, TIMESTAMP '2020-08-04 10:23:54', true);

INSERT INTO post_comments (user_id, post_id, time, text) VALUES (2, 1, TIMESTAMP '2020-08-04 10:23:54', 'super01');
INSERT INTO post_comments (user_id, post_id, time, text) VALUES (1, 1, TIMESTAMP '2020-08-04 10:23:54', 'super02');
INSERT INTO post_comments (user_id, post_id, time, text) VALUES (2, 3, TIMESTAMP '2020-08-04 10:23:54', 'super03');
INSERT INTO post_comments (user_id, post_id, time, text) VALUES (2, 4, TIMESTAMP '2020-08-04 10:23:54', 'super04');
INSERT INTO post_comments (user_id, post_id, time, text) VALUES (1, 2, TIMESTAMP '2020-08-04 10:23:54', 'super05');
INSERT INTO post_comments (user_id, post_id, time, text) VALUES (2, 2, TIMESTAMP '2020-08-04 10:23:54', 'super06');