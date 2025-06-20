insert into book (book_id, title, description,  author, publisher, pulisher_at, isbn, price_original, price_sale, is_gift_wrappable, create_at, status, stock)
values
    (1, '테스트책1', '설명12','김', '출판사', '2020-02-02', '0000000000000', 30000, 25000, true, '2020-03-03 14:30:00', 'on_sale', 200),
    (2, '테스트책2', '설명34','김', '출판사', '2020-02-02', '0000000000001', 35000, 33000, true, '2020-03-03 14:30:00', 'on_sale', 100),
    (3, '테스트책3', '재미있는 책','이', 'A사', '2020-02-02', '0000000000002', 35000, 33000, true, '2020-03-03 14:30:00', 'on_sale', 100);


insert into book_tag (tag_id, name)
values
    (1,'태그1'),
    (2,'태그2');

insert into book_tag_map (book_id, tag_id)
values
    (1,1),
    (1,2),
    (2,1);

insert into book_category (category_id, name, created_at)
values
    (1, '소설', '2020-03-03 14:30:00');

insert into book_category (category_id, parent_id, name, created_at)
values
    (2,  1,'추리소설', '2020-03-05 15:30:00'),
    (3, 1, '공포소설', '2023-03-06 17:00:00');

insert into book_category_map (book_id, category_id)
values
    (1,2),
    (1,3);

insert into book_like (book_like_id, book_id, user_id, liked_at)
values
    (1,1, '유저1', '2023-01-01 10:00:00'),
    (2,1, '유저2', '2023-01-01 10:00:00');

