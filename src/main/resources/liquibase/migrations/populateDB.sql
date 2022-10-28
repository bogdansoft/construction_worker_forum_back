--liquibase formatted sql
--changeset sysoiev:2

INSERT INTO users (account_status,
                   created_at,
                   email,
                   first_name,
                   last_name,
                   password,
                   user_role,
                   username)
VALUES ('ACTIVE',
        CURRENT_TIMESTAMP(),
        'jake@example.com',
        'Jake',
        'Johnson',
        '$2y$10$790tAElGoR.OwefHib1Zp.Ew.NIpBT9i3A6//EhKvzFs3clAQypz2',
        'ADMINISTRATOR',
        'jake123'),
       ('ACTIVE',
        CURRENT_TIMESTAMP(),
        'ben@example.com',
        'Ben',
        'Peterson',
        '$2a$10$yaivzREajtcrmdQRwZdPBOlx5o/WJjmHMtSTTmHy2d7i11genkP5y',
        'SUPPORT',
        'benny'),
       ('ACTIVE',
        CURRENT_TIMESTAMP(),
        'den@example.com',
        'Denny',
        'Smith',
        '$2a$10$pr.tOBXEyTcLIkCXhmZobu6ne1AOt8QanF.6o2hc8UAuMeuXyFNHS',
        'USER',
        'denny');

INSERT INTO topics (name, created_at)
VALUES ('Tower cranes', CURRENT_TIMESTAMP()),
       ('Building materials', CURRENT_TIMESTAMP()),
       ('Foundation', CURRENT_TIMESTAMP()),
       ('Engineering', CURRENT_TIMESTAMP());

INSERT INTO posts (content, created_at, title, topic_id, user_id)
VALUES ('A crane is a type of machine, generally equipped with a hoist rope.',
        CURRENT_TIMESTAMP(),
        'Building cranes',
        1,
        1),
       ('Building material is material used for construction. Many naturally occurring substances, such as clay, rocks, sand, wood, and even twigs and leaves, have been used to construct buildings. Apart from naturally occurring materials, many man-made products are in use, some more and some less synthetic.',
        CURRENT_TIMESTAMP(),
        'Materials',
        2,
        2),
       ('With decades of innovation and experience under our belts, we’re achieving remarkable results in building technologies, energy, and environmental performance for our clients. We’re with you from initial concept through design, construction, commissioning, and validation.',
        CURRENT_TIMESTAMP(),
        'Building engineering',
        4,
        1);

INSERT INTO comments (content, created_at, post_id, user_id)
VALUES ('Wow, great!!!', CURRENT_TIMESTAMP(), 1, 1),
       ('Awesome', CURRENT_TIMESTAMP(), 1, 3),
       ('It`s exactly what I need!', CURRENT_TIMESTAMP(), 2, 3),
       ('I do not agree!!', CURRENT_TIMESTAMP(), 3, 2);


