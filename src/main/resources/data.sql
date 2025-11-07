-- 소비자 (consumers)
INSERT INTO consumers (email, password, nickname, address, detail_address, created_at)
VALUES
    ('ncc1825@naver.com', 'abcd', '이재원', '서울시 마포구', '202호', NOW()),
    ('ij1430807@gmail.com', 'abcd', '홍길동', '서울시 마포구', '202호', NOW());

-- 주문 (orders)
INSERT INTO orders (total_price, status, is_reservation, delivery_date_time, created_at, consumer_id)
VALUES
    (35000, 'PENDING', false, NOW() + INTERVAL '1 DAY', NOW(), 1),
    (21000, 'PENDING', true, NOW() + INTERVAL '1 DAY', NOW(), 2);