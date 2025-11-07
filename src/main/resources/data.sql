INSERT INTO categories (category_name) VALUES ('VEGETABLES');
INSERT INTO categories (category_name) VALUES ('FROZEN_FOODS');
INSERT INTO categories (category_name) VALUES ('DAIRY');
INSERT INTO categories (category_name) VALUES ('MEAT');
INSERT INTO categories (category_name) VALUES ('SEAFOOD');
INSERT INTO categories (category_name) VALUES ('FRUIT');
INSERT INTO categories (category_name) VALUES ('BREAD');

INSERT INTO products (name, weight_pieces, price, photo_url, stock, category_id)
VALUES ('오이 고추', '200g', 3410, '/images/oi_gochu.jpg', 100, 1),
       ('양파', '1kg', 5990, '/images/yangpa.jpg', 100, 1),
       ('유기농 브로콜리', '250g', 10600, '/images/brocolli.jpg', 100, 1),
       ('오이', '2개입', 4290, '/images/oi.jpg', 100, 1),
       ('적상추', '120g', 2690, '/images/sangchu.jpg', 100, 1),
       ('샐러드 믹스', '100g', 5730, '/images/saladmix.jpg', 100, 1);

INSERT INTO products (name, weight_pieces, price, photo_url, stock, category_id)
VALUES ('그릭요거트', '100g 3개입', 11400, '/images/yogart.jpg', 100, 3),
       ('유정랑', '15구', 10980, '/images/egg_15ea.jpg', 100, 3),
       ('유정랑', '10구', 8900, '/images/Egg_10ea.jpg', 100, 3),
       ('우유', '750ml 2개', 9630, '/images/Milk.jpg', 100, 3),
       ('가염 버터', '10g 60개', 29450, '/images/butter.jpg', 100, 3),
       ('무항생제 우유', '2.3L', 11900, '/images/no_hangsaengje_milk.jpg', 100, 3),
       ('파르미지아노 레지아노 치즈', '150g', 9780, '/images/cheese.jpg', 100, 3);

INSERT INTO products (name, weight_pieces, price, photo_url, stock, category_id)
VALUES ('닭 안심', '800g', 11000, '/images/chicken_ansim.jpg', 100, 4),
       ('닭 가슴살', '800g', 10800, '/images/chicken_gaseum.jpg', 100, 4),
       ('닭 다리살', '800g', 15000, '/images/chicken_dari.jpg', 100, 4),
       ('닭 11호', '2kg', 18000, '/images/chicken_tong.jpg', 100, 4),
       ('소 국거리', '300g', 17800, '/images/cow_gook.jpg', 100, 4),
       ('소 다짐육', '150g', 11400, '/images/cow_crashed.jpg', 100, 4),
       ('소 불고기용', '300g', 23340, '/images/cow_boolgogi.jpg', 100, 4),
       ('소 스테이크용', '200g', 40670, '/images/cow_stake.jpg', 100, 4),
       ('돼지 삼겹살', '500g', 18800, '/images/fork_samgyeb.jpg', 100, 4),
       ('돼지 등심', '300g', 6900, '/images/fork_deungsim.jpg', 100, 4),
       ('돼지 뒷다리살', '500g', 8000, '/images/fork_dari.jpg', 100, 4),
       ('돼지 앞다리', '600g', 11050, '/images/fork_frontdari.jpg', 100, 4);

INSERT INTO products (name, weight_pieces, price, photo_url, stock, category_id)
VALUES ('생물 새우', '1kg', 29900, '/images/saewoo.jpg', 100, 5),
       ('블랙타이거 새우', '500g', 24000, '/images/blacktiger_saewoo.jpg', 100, 5),
       ('냉동 새우', '500g', 21900, '/images/frozen_saewoo.jpg', 100, 5),
       ('순살 고등어', '900g', 18200, '/images/godeungeo.jpg', 100, 5),
       ('곱창 김', '30개입', 11500, '/images/kim.jpg', 100, 5),
       ('전복', '1kg', 40000, '/images/jeonbok.jpg', 100, 5),
       ('훈제 연어', '150g', 11570, '/images/salmon.jpg', 100, 5);

INSERT INTO products (name, weight_pieces, price, photo_url, stock, category_id)
VALUES ('황금 사과', '1kg', 24900, '/images/goldapple.jpg', 100, 6),
       ('홍사과', '1.2kg', 22900, '/images/apple.jpg', 100, 6),
       ('미니사과', '500g', 16000, '/images/apple_mini.jpg', 100, 6),
       ('아보카도', '1kg', 14000, '/images/avocado.jpg', 100, 6),
       ('감귤', '1kg', 13990, '/images/gyul.jpg', 100, 6),
       ('블루베리', '278g', 12000, '/images/blueberry.jpg', 100, 6),
       ('청포도', '500g', 5380, '/images/chung_podo.jpg', 100, 6),
       ('키위', '550g', 5540, '/images/kiwi.jpg', 100, 6),
       ('파인애플', '540g', 8900, '/images/pineapple.jpg', 100, 6);

INSERT INTO products (name, weight_pieces, price, photo_url, stock, category_id)
VALUES ('쌀 식빵', '480g', 5500, '/images/bread.jpg', 100, 7),
       ('호밀 식빵', '480g', 5300, '/images/homil_bread.jpg', 100, 7),
       ('데니쉬 식빵', '440g', 7900, '/images/denish_bread.jpg', 100, 7),
       ('당근 케이크', '480g', 15500, '/images/carrot_cake.jpg', 100, 7),
       ('블루베리번', '180g', 4800, '/images/blueberry_burn.jpg', 100, 7),
       ('모닝빵', '250g 2개', 14400, '/images/morning_bread.jpg', 100, 7),
       ('바게트빵', '280g', 4940, '/images/bargett.jpg', 100, 7);
