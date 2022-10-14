-- Data for Prod --
--
-- User: johnsmith@gmail.com, Jsmith123
-- 1 Business, 20 Products, 40 product images, 35 Inventory, 40 listings, 100 Sales

INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode) VALUES (8, '63', 'Victoria Street', 'Christchurch', 'Canterbury', 'New Zealand', '8013');
INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode) VALUES (9, '63', 'Kirkwood Avenue', 'Christchurch', 'Canterbury', 'New Zealand', '8041');

INSERT INTO user (user_id, first_name, middle_name, last_name, nickname, bio, email,
                  date_of_birth, phone_number, address_id, created, user_role,
                  password, session_ticket, country_for_currency)
VALUES (10,
        'John',
        'P',
        'Smith',
        'Jo',
        'Your friendly neighbourhood grocer :)',
        'johnsmith@gmail.com',
        '1952-09-25',
        '0275488225',
        8,
        '2018-03-04',
        'ROLE_USER',
        '$2a$10$niD0UAuFWzlk6dJbemy86.arnL3A/9T0/Vg6IKT79hMhZnbX06Dwe',
        null,
        'New Zealand');

INSERT INTO business (business_id, primary_administrator_id, name, description,
                      address_id, business_type, created)
VALUES (10,
        10,
        'Food Haven',
        'For all your grocery needs',
        9,
        'Retail Trade',
        '2018-03-05');

INSERT INTO business_administrators
VALUES (10, 10);




insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (51, 10, 'Pasta - Angel Hair', 'Innovative neutral utilisation', 'Babblestorm Corp.', 746.4, '2021-01-17', 10, 51);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (52, 10, 'Cinnamon Buns Sticky', 'Customizable local customer loyalty', 'Flipbug Corp.', 545.7, '2020-08-26', 10, 52);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (53, 10, 'Pie Shell - 5', 'Implemented fault-tolerant orchestration', 'Demizz Corp.', 556.3, '2020-07-06', 10, 53);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (54, 10, 'Cream - 18%', 'Reverse-engineered 5th generation middleware', 'Yodo Corp.', 992.5, '2021-04-12', 10, 54);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (55, 10, 'Snapple - Mango Maddness', 'De-engineered global approach', 'Dabtype Corp.', 350.0, '2021-01-14', 10, 55);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (56, 10, 'Wine - Ej Gallo Sonoma', 'Fundamental real-time support', 'Youtags Corp.', 821.1, '2020-07-22', 10, 56);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (57, 10, 'Irish Cream - Butterscotch', 'Stand-alone non-volatile moratorium', 'Yozio Corp.', 206.4, '2021-05-25', 10, 57);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (58, 10, 'Potatoes - Yukon Gold, 80 Ct', 'Multi-lateral dynamic circuit', 'Gigashots Corp.', 667.8, '2021-06-15', 10, 58);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (59, 10, 'Table Cloth 72x144 White', 'Distributed methodical hardware', 'Avaveo Corp.', 253.2, '2021-02-17', 10, 59);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (60, 10, 'Juice - Ocean Spray Cranberry', 'Exclusive grid-enabled matrix', 'Browseblab Corp.', 970.2, '2021-04-29', 10, 60);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (61, 10, 'Macaroons - Homestyle Two Bit', 'Upgradable global architecture', 'Twitterbeat Corp.', 389.4, '2020-12-18', 10, 61);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (62, 10, 'Cheese - Comtomme', 'Quality-focused fault-tolerant orchestration', 'Eayo Corp.', 802.2, '2021-02-16', 10, 62);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (63, 10, 'Bread - Multigrain Oval', 'Operative client-driven application', 'Browsezoom Corp.', 81.1, '2020-10-31', 10, 63);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (64, 10, 'Veal - Brisket, Provimi, Bone - In', 'Programmable zero administration functionalities', 'Vipe Corp.', 953.2, '2021-02-05', 10, 64);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (65, 10, 'Jolt Cola - Electric Blue', 'De-engineered heuristic function', 'Photobug Corp.', 935.1, '2021-06-30', 10, 65);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (66, 10, 'Raspberries - Frozen', 'Profound multi-tasking policy', 'Gigazoom Corp.', 761.3, '2021-05-15', 10, 66);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (67, 10, 'Wine - Jafflin Bourgongone', 'Assimilated leading edge interface', 'Ntags Corp.', 426.6, '2021-04-30', 10, 67);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (68, 10, 'Food Colouring - Green', 'Multi-channelled demand-driven website', 'Fivespan Corp.', 130.0, '2020-09-07', 10, 68);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (69, 10, 'Cocoa Butter', 'Future-proofed zero administration adapter', 'Linkbuzz Corp.', 784.5, '2020-10-10', 10, 69);
insert into product (product_id, business_id, name, description, manufacturer, recommended_retail_price, created, business, primary_image_id) values (70, 10, 'Calypso - Strawberry Lemonade', 'Cross-platform bandwidth-monitored orchestration', 'Wikibox Corp.', 307.9, '2020-12-02', 10, 70);


insert into product_image (image_id, filename, thumbnail_filename, product_id) values (51, '7_1_1_pexels-photo-6157055.jpeg', '7_1_1_thumbnail_pexels-photo-6157055.jpeg', 51);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (52, '10_1_1_pexels-photo-7267051.jpeg', '10_1_1_thumbnail_pexels-photo-7267051.jpeg', 52);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (53, '3_1_1_pexels-photo-4197445.jpeg', '3_1_1_thumbnail_pexels-photo-4197445.jpeg', 53);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (54, '7_1_1_pexels-photo-6157055.jpeg', '7_1_1_thumbnail_pexels-photo-6157055.jpeg', 54);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (55, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 55);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (56, '3_1_1_pexels-photo-4197445.jpeg', '3_1_1_thumbnail_pexels-photo-4197445.jpeg', 56);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (57, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 57);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (58, '10_1_1_pexels-photo-7267051.jpeg', '10_1_1_thumbnail_pexels-photo-7267051.jpeg', 58);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (59, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 59);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (60, '7_1_1_pexels-photo-6157055.jpeg', '7_1_1_thumbnail_pexels-photo-6157055.jpeg', 60);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (61, '1_1_1_pexels-photo-4022090.jpeg', '1_1_1_thumbnail_pexels-photo-4022090.jpeg', 61);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (62, '9_1_1_pexels-photo-7267050.jpeg', '9_1_1_thumbnail_pexels-photo-7267050.jpeg', 62);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (63, '2_1_1_pexels-photo-4033112.jpeg', '2_1_1_thumbnail_pexels-photo-4033112.jpeg', 63);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (64, '8_1_1_pexels-photo-6157056.jpeg', '8_1_1_thumbnail_pexels-photo-6157056.jpeg', 64);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (65, '4_1_1_pexels-photo-4197905.jpeg', '4_1_1_thumbnail_pexels-photo-4197905.jpeg', 65);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (66, '3_1_1_pexels-photo-4197445.jpeg', '3_1_1_thumbnail_pexels-photo-4197445.jpeg', 66);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (67, '4_1_1_pexels-photo-4197905.jpeg', '4_1_1_thumbnail_pexels-photo-4197905.jpeg', 67);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (68, '5_1_1_pexels-photo-5946090.jpeg', '5_1_1_thumbnail_pexels-photo-5946090.jpeg', 68);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (69, '5_1_1_pexels-photo-5946090.jpeg', '5_1_1_thumbnail_pexels-photo-5946090.jpeg', 69);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (70, '4_1_1_pexels-photo-4197905.jpeg', '4_1_1_thumbnail_pexels-photo-4197905.jpeg', 70);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (71, '2_1_1_pexels-photo-4033112.jpeg', '2_1_1_thumbnail_pexels-photo-4033112.jpeg', 56);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (72, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 59);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (73, '3_1_1_pexels-photo-4197445.jpeg', '3_1_1_thumbnail_pexels-photo-4197445.jpeg', 70);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (74, '10_1_1_pexels-photo-7267051.jpeg', '10_1_1_thumbnail_pexels-photo-7267051.jpeg', 51);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (75, '8_1_1_pexels-photo-6157056.jpeg', '8_1_1_thumbnail_pexels-photo-6157056.jpeg', 65);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (76, '8_1_1_pexels-photo-6157056.jpeg', '8_1_1_thumbnail_pexels-photo-6157056.jpeg', 61);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (77, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 70);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (78, '4_1_1_pexels-photo-4197905.jpeg', '4_1_1_thumbnail_pexels-photo-4197905.jpeg', 55);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (79, '1_1_1_pexels-photo-4022090.jpeg', '1_1_1_thumbnail_pexels-photo-4022090.jpeg', 52);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (80, '2_1_1_pexels-photo-4033112.jpeg', '2_1_1_thumbnail_pexels-photo-4033112.jpeg', 58);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (81, '1_1_1_pexels-photo-4022090.jpeg', '1_1_1_thumbnail_pexels-photo-4022090.jpeg', 53);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (82, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 67);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (83, '6_1_1_pexels-photo-6157049.jpeg', '6_1_1_thumbnail_pexels-photo-6157049.jpeg', 58);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (84, '10_1_1_pexels-photo-7267051.jpeg', '10_1_1_thumbnail_pexels-photo-7267051.jpeg', 60);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (85, '3_1_1_pexels-photo-4197445.jpeg', '3_1_1_thumbnail_pexels-photo-4197445.jpeg', 51);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (86, '8_1_1_pexels-photo-6157056.jpeg', '8_1_1_thumbnail_pexels-photo-6157056.jpeg', 51);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (87, '9_1_1_pexels-photo-7267050.jpeg', '9_1_1_thumbnail_pexels-photo-7267050.jpeg', 63);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (88, '10_1_1_pexels-photo-7267051.jpeg', '10_1_1_thumbnail_pexels-photo-7267051.jpeg', 70);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (89, '9_1_1_pexels-photo-7267050.jpeg', '9_1_1_thumbnail_pexels-photo-7267050.jpeg', 67);
insert into product_image (image_id, filename, thumbnail_filename, product_id) values (90, '8_1_1_pexels-photo-6157056.jpeg', '8_1_1_thumbnail_pexels-photo-6157056.jpeg', 63);

insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (51, '2021-12-21', '2022-05-08', '2021-02-01', 4.4, 64, 10, '2023-11-15', 24.6);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (52, '2022-01-01', '2022-05-29', '2021-03-23', 7.3, 65, 8, '2023-10-31', 72.1);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (53, '2022-01-30', '2022-04-10', '2021-06-08', 1.9, 59, 4, '2023-12-21', 21.5);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (54, '2021-12-31', '2022-04-21', '2021-03-08', 5.0, 68, 5, '2023-10-17', 54.7);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (55, '2021-12-31', '2022-06-29', '2021-02-25', 6.4, 61, 3, '2023-10-24', 85.8);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (56, '2021-12-22', '2022-03-15', '2020-11-19', 8.6, 53, 4, '2023-11-24', 40.7);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (57, '2022-01-26', '2022-07-10', '2020-10-28', 7.7, 60, 2, '2023-10-17', 12.5);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (58, '2022-01-03', '2022-02-06', '2020-09-29', 8.4, 51, 4, '2023-11-23', 96.5);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (59, '2022-01-17', '2022-06-06', '2020-11-01', 9.6, 68, 8, '2023-11-11', 99.7);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (60, '2021-12-22', '2022-05-01', '2020-12-23', 2.5, 69, 4, '2023-11-04', 89.4);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (61, '2022-01-03', '2022-06-26', '2021-02-21', 2.8, 57, 7, '2023-10-28', 84.9);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (62, '2022-01-15', '2022-04-21', '2021-06-08', 8.4, 70, 8, '2023-11-11', 89.6);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (63, '2022-01-03', '2022-05-31', '2021-03-07', 7.6, 70, 9, '2023-10-29', 31.3);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (64, '2022-01-08', '2022-06-25', '2020-10-05', 7.9, 57, 3, '2023-12-07', 55.0);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (65, '2022-01-14', '2022-03-31', '2020-09-19', 8.3, 63, 10, '2023-12-10', 10.7);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (66, '2021-12-21', '2022-06-19', '2020-12-10', 8.9, 66, 2, '2023-12-01', 39.3);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (67, '2022-01-19', '2022-06-03', '2020-11-23', 4.8, 67, 1, '2023-11-13', 88.1);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (68, '2021-12-30', '2022-03-29', '2021-06-02', 6.1, 62, 1, '2023-10-22', 100.0);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (69, '2021-12-23', '2022-04-03', '2021-06-09', 4.7, 62, 7, '2023-12-20', 54.3);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (70, '2022-01-03', '2022-05-03', '2021-04-20', 8.3, 54, 9, '2023-12-07', 43.9);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (71, '2021-12-21', '2022-04-06', '2021-04-25', 4.4, 61, 5, '2023-11-14', 44.2);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (72, '2021-12-29', '2022-05-23', '2020-09-28', 8.8, 60, 4, '2023-11-05', 29.9);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (73, '2022-01-27', '2022-02-05', '2021-01-13', 9.7, 52, 3, '2023-12-16', 14.9);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (74, '2022-01-14', '2022-07-04', '2021-06-16', 3.3, 70, 7, '2023-11-28', 76.7);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (75, '2021-12-26', '2022-02-19', '2021-05-20', 2.3, 55, 6, '2023-12-13', 57.1);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (76, '2022-01-06', '2022-03-31', '2021-03-04', 2.8, 59, 10, '2023-11-12', 19.1);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (77, '2021-12-21', '2022-05-31', '2020-12-06', 6.5, 62, 5, '2023-11-09', 64.8);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (78, '2021-12-29', '2022-06-19', '2021-03-09', 1.2, 63, 8, '2023-12-08', 59.9);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (79, '2022-01-19', '2022-03-23', '2021-03-18', 5.8, 56, 10, '2023-11-19', 27.8);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (80, '2022-01-04', '2022-02-12', '2021-07-10', 6.8, 54, 4, '2023-11-17', 97.2);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (81, '2022-01-27', '2022-02-03', '2021-01-04', 5.8, 67, 1, '2023-12-16', 21.7);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (82, '2022-01-25', '2022-05-03', '2021-02-09', 1.1, 56, 7, '2023-11-09', 30.2);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (83, '2022-01-13', '2022-04-06', '2021-02-02', 8.0, 66, 7, '2023-11-07', 10.0);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (84, '2022-01-11', '2022-02-12', '2020-12-04', 1.1, 52, 7, '2023-10-29', 12.0);
insert into inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) values (85, '2021-12-27', '2022-03-26', '2021-04-23', 7.9, 70, 10, '2023-12-21', 33.2);

insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (51, 68, 1, 33.6, 'Organic leading edge focus group', '2021-10-08', '2022-07-29');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (52, 74, 1, 3.0, 'Multi-layered logistical focus group', '2021-09-04', '2022-06-14');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (53, 75, 1, 80.5, 'Vision-oriented fresh-thinking capability', '2021-08-19', '2022-10-19');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (54, 53, 1, 28.9, 'Progressive content-based instruction set', '2021-09-22', '2022-09-29');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (55, 83, 1, 66.5, 'Managed static migration', '2021-09-21', '2022-09-02');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (56, 68, 1, 1.2, 'Switchable analyzing support', '2021-08-26', '2022-12-27');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (57, 59, 1, 93.5, 'Public-key optimizing moratorium', '2021-09-01', '2022-11-02');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (58, 83, 1, 36.1, 'Face to face holistic moratorium', '2021-08-18', '2022-08-27');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (59, 77, 1, 63.6, 'Future-proofed non-volatile middleware', '2021-09-30', '2022-07-04');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (60, 59, 1, 39.2, 'Ameliorated homogeneous system engine', '2021-09-05', '2022-07-17');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (61, 60, 1, 82.7, 'Ergonomic bifurcated local area network', '2021-09-10', '2022-12-17');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (62, 59, 1, 80.4, 'Front-line contextually-based ability', '2021-09-01', '2022-11-28');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (63, 68, 1, 1.7, 'Public-key needs-based Graphic Interface', '2021-09-08', '2022-11-04');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (64, 76, 1, 66.1, 'Persistent discrete matrices', '2021-09-29', '2022-08-11');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (65, 69, 1, 43.6, 'Fully-configurable clear-thinking Graphic Interface', '2021-09-15', '2022-09-17');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (66, 79, 1, 10.0, 'Distributed multi-tasking open system', '2021-09-16', '2022-07-09');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (67, 73, 1, 67.4, 'Future-proofed incremental website', '2021-09-05', '2022-11-01');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (68, 81, 1, 15.3, 'Pre-emptive leading edge infrastructure', '2021-08-25', '2022-11-02');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (69, 69, 1, 4.4, 'Sharable human-resource definition', '2021-09-11', '2022-06-19');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (70, 68, 1, 40.3, 'Object-based foreground capability', '2021-09-01', '2022-12-13');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (71, 64, 1, 97.4, 'Self-enabling global algorithm', '2021-09-20', '2022-11-25');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (72, 72, 1, 38.7, 'Grass-roots human-resource adapter', '2021-09-04', '2022-08-29');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (73, 61, 1, 64.8, 'Mandatory methodical superstructure', '2021-09-05', '2022-07-18');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (74, 81, 1, 5.8, 'Exclusive foreground projection', '2021-09-28', '2022-07-11');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (75, 84, 1, 95.8, 'Automated impactful encryption', '2021-09-06', '2022-08-06');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (76, 62, 1, 90.5, 'Enhanced motivating projection', '2021-08-25', '2022-11-09');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (77, 74, 1, 87.6, 'Public-key analyzing hardware', '2021-10-04', '2022-11-01');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (78, 58, 1, 60.0, 'Profit-focused grid-enabled conglomeration', '2021-09-26', '2022-06-18');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (79, 81, 1, 33.3, 'Function-based non-volatile algorithm', '2021-09-13', '2022-10-30');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (80, 82, 1, 71.1, 'Optional bi-directional groupware', '2021-09-19', '2022-08-18');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (81, 73, 1, 71.0, 'Monitored encompassing workforce', '2021-10-06', '2022-09-12');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (82, 79, 1, 69.1, 'Centralized client-driven open system', '2021-08-31', '2022-12-15');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (83, 74, 1, 65.9, 'Organic scalable core', '2021-08-24', '2022-07-25');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (84, 80, 1, 88.5, 'Function-based static paradigm', '2021-10-06', '2022-08-02');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (85, 74, 1, 14.8, 'Multi-lateral cohesive circuit', '2021-09-06', '2022-12-24');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (86, 85, 1, 34.5, 'Distributed disintermediate frame', '2021-08-20', '2022-09-10');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (87, 85, 1, 84.5, 'Sharable multi-state infrastructure', '2021-09-02', '2022-10-30');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (88, 64, 1, 73.2, 'Balanced incremental standardization', '2021-09-09', '2022-12-06');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (89, 75, 1, 54.3, 'Organic 24/7 throughput', '2021-09-05', '2022-12-22');
insert into listing (id, inventory_id, quantity, price, more_info, created, closes) values (90, 62, 1, 53.1, 'Networked asynchronous hub', '2021-10-09', '2022-10-31');

-- Cards --

-- Expired --
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (51, 10, 'ForSale', '2021-08-12', '2021-09-24', 'Yukon XL 2500', 'implement mission-critical architectures');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (52, 10, 'Wanted', '2021-08-04', '2021-09-05', 'Cherokee', 'synthesize dot-com eyeballs');

-- Expiring --
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (53, 10, 'Wanted', '2021-09-17', '2021-10-01', 'Explorer', 'architect mission-critical bandwidth');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (54, 10, 'ForSale', '2021-09-17', '2021-10-01', '500E', 'harness web-enabled users');

-- Not Expiring --
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (55, 10, 'ForSale', '2021-09-30', '2021-10-14', 'Ram', 'architect robust platforms');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (56, 10, 'Wanted', '2021-09-30', '2021-10-14', 'Optima', 'target virtual experiences');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (57, 10, 'Exchange', '2021-09-30', '2021-10-14', 'Fiesta', 'visualize one-to-one paradigms');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (58, 10, 'Wanted', '2021-09-30', '2021-10-14', 'Suburban 2500', 'enhance turn-key architectures');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (59, 10, 'Wanted', '2021-09-30', '2021-10-14', '850', 'optimize cutting-edge e-business');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (60, 10, 'Exchange', '2021-09-30', '2021-10-14', 'Golf', 'utilize frictionless partnerships');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (61, 10, 'Exchange', '2021-09-30', '2021-10-14', 'CR-Z', 'incentivize best-of-breed e-markets');
insert into card (card_id, card_creator, card_section, card_date_created, card_end_period, card_title, card_description) values (62, 10, 'ForSale', '2021-09-30', '2021-10-14', 'Jetta', 'utilize B2C e-markets');

-- Keywords --
insert into keyword (id, keyword_created, keyword_name) values (6, '2021-06-09', '100');
insert into keyword (id, keyword_created, keyword_name) values (7, '2020-08-12', 'Land Cruiser');
insert into keyword (id, keyword_created, keyword_name) values (8, '2021-01-17', 'Ridgeline');
insert into keyword (id, keyword_created, keyword_name) values (9, '2021-03-03', '6 Series');
insert into keyword (id, keyword_created, keyword_name) values (10, '2020-10-11', 'B-Series');
insert into keyword (id, keyword_created, keyword_name) values (11, '2020-09-18', 'Regal');
insert into keyword (id, keyword_created, keyword_name) values (12, '2020-09-02', '4Runner');
insert into keyword (id, keyword_created, keyword_name) values (13, '2020-08-31', 'S5');
insert into keyword (id, keyword_created, keyword_name) values (14, '2020-12-26', 'Cobalt');
insert into keyword (id, keyword_created, keyword_name) values (15, '2021-01-28', 'Navigator');
insert into keyword (id, keyword_created, keyword_name) values (16, '2021-02-23', 'SRX');
insert into keyword (id, keyword_created, keyword_name) values (17, '2021-03-20', 'Mighty Max Macro');
insert into keyword (id, keyword_created, keyword_name) values (18, '2021-02-27', 'Mustang');
insert into keyword (id, keyword_created, keyword_name) values (19, '2021-06-28', 'C-Class');
insert into keyword (id, keyword_created, keyword_name) values (20, '2021-07-14', 'Ram 1500');
insert into keyword (id, keyword_created, keyword_name) values (21, '2021-09-21', 'FCX Clarity');
insert into keyword (id, keyword_created, keyword_name) values (22, '2021-05-09', 'X3');
insert into keyword (id, keyword_created, keyword_name) values (23, '2021-03-14', 'BRZ');
insert into keyword (id, keyword_created, keyword_name) values (24, '2021-08-12', 'CL-Class');
insert into keyword (id, keyword_created, keyword_name) values (25, '2021-02-10', '900');
insert into keyword (id, keyword_created, keyword_name) values (26, '2021-03-07', '760');
insert into keyword (id, keyword_created, keyword_name) values (27, '2021-03-01', 'Mirage');
insert into keyword (id, keyword_created, keyword_name) values (28, '2021-08-02', 'GS');
insert into keyword (id, keyword_created, keyword_name) values (29, '2021-08-13', 'Journey');
insert into keyword (id, keyword_created, keyword_name) values (30, '2020-12-13', '3500 Club Coupe');
insert into keyword (id, keyword_created, keyword_name) values (31, '2021-09-17', 'Prizm');
insert into keyword (id, keyword_created, keyword_name) values (32, '2020-09-06', 'S40');
insert into keyword (id, keyword_created, keyword_name) values (33, '2021-05-15', '57');
insert into keyword (id, keyword_created, keyword_name) values (34, '2021-06-27', 'Thunderbird');
insert into keyword (id, keyword_created, keyword_name) values (35, '2021-05-03', 'RSX');
insert into keyword (id, keyword_created, keyword_name) values (36, '2021-07-21', 'Range Rover');
insert into keyword (id, keyword_created, keyword_name) values (37, '2021-09-23', '911');
insert into keyword (id, keyword_created, keyword_name) values (38, '2020-11-09', 'Outlook');
insert into keyword (id, keyword_created, keyword_name) values (39, '2020-12-02', 'Eldorado');
insert into keyword (id, keyword_created, keyword_name) values (40, '2021-07-04', 'Impala');
insert into keyword (id, keyword_created, keyword_name) values (41, '2020-10-14', 'Rendezvous');
insert into keyword (id, keyword_created, keyword_name) values (42, '2021-08-06', 'Elantra');
insert into keyword (id, keyword_created, keyword_name) values (43, '2020-12-31', 'Elan');
insert into keyword (id, keyword_created, keyword_name) values (44, '2020-08-12', '300M');
insert into keyword (id, keyword_created, keyword_name) values (45, '2021-06-03', 'Cayman');
insert into keyword (id, keyword_created, keyword_name) values (46, '2020-10-01', 'Swift');
insert into keyword (id, keyword_created, keyword_name) values (47, '2021-05-12', 'A6');
insert into keyword (id, keyword_created, keyword_name) values (48, '2021-02-09', 'B-Series Plus');
insert into keyword (id, keyword_created, keyword_name) values (49, '2021-06-27', 'Esprit');
insert into keyword (id, keyword_created, keyword_name) values (50, '2020-12-04', '3 Series');
insert into keyword (id, keyword_created, keyword_name) values (51, '2021-01-06', 'Eurovan');
insert into keyword (id, keyword_created, keyword_name) values (52, '2020-08-07', 'Avalanche 1500');
insert into keyword (id, keyword_created, keyword_name) values (53, '2021-08-12', 'E250');
insert into keyword (id, keyword_created, keyword_name) values (54, '2021-01-12', 'Passat');
insert into keyword (id, keyword_created, keyword_name) values (55, '2021-09-13', 'Veloster');
insert into keyword (id, keyword_created, keyword_name) values (56, '2021-03-16', 'Ford');
insert into keyword (id, keyword_created, keyword_name) values (57, '2021-03-24', 'Nissan');
insert into keyword (id, keyword_created, keyword_name) values (58, '2021-01-12', 'Buick');
insert into keyword (id, keyword_created, keyword_name) values (59, '2021-09-07', 'BMW');
insert into keyword (id, keyword_created, keyword_name) values (60, '2021-02-06', 'Chrysler');
insert into keyword (id, keyword_created, keyword_name) values (61, '2021-01-01', 'Cadillac');
insert into keyword (id, keyword_created, keyword_name) values (62, '2021-07-08', 'Mazda');
insert into keyword (id, keyword_created, keyword_name) values (63, '2021-02-20', 'Saab');
insert into keyword (id, keyword_created, keyword_name) values (64, '2020-09-28', 'Mercedes-Benz');
insert into keyword (id, keyword_created, keyword_name) values (65, '2021-06-12', 'Suzuki');
insert into keyword (id, keyword_created, keyword_name) values (66, '2021-07-20', 'Chrysler');
insert into keyword (id, keyword_created, keyword_name) values (67, '2021-04-04', 'Audi');
insert into keyword (id, keyword_created, keyword_name) values (68, '2020-10-14', 'Suzuki');
insert into keyword (id, keyword_created, keyword_name) values (69, '2020-12-03', 'Volkswagen');
insert into keyword (id, keyword_created, keyword_name) values (70, '2020-10-06', 'Lincoln');
insert into keyword (id, keyword_created, keyword_name) values (71, '2020-10-31', 'Mercedes-Benz');
insert into keyword (id, keyword_created, keyword_name) values (72, '2020-07-24', 'Acura');
insert into keyword (id, keyword_created, keyword_name) values (73, '2020-10-11', 'Acura');
insert into keyword (id, keyword_created, keyword_name) values (74, '2021-07-31', 'Chevrolet');
insert into keyword (id, keyword_created, keyword_name) values (75, '2020-10-24', 'Mazda');
insert into keyword (id, keyword_created, keyword_name) values (76, '2021-05-15', 'Bentley');
insert into keyword (id, keyword_created, keyword_name) values (77, '2021-02-03', 'Land Rover');
insert into keyword (id, keyword_created, keyword_name) values (78, '2021-08-08', 'Honda');
insert into keyword (id, keyword_created, keyword_name) values (79, '2021-07-31', 'Maserati');
insert into keyword (id, keyword_created, keyword_name) values (80, '2021-03-16', 'Chevrolet');
insert into keyword (id, keyword_created, keyword_name) values (81, '2021-09-19', 'Volvo');
insert into keyword (id, keyword_created, keyword_name) values (82, '2021-06-20', 'Chevrolet');
insert into keyword (id, keyword_created, keyword_name) values (83, '2021-09-19', 'Infiniti');
insert into keyword (id, keyword_created, keyword_name) values (84, '2020-11-19', 'Cadillac');
insert into keyword (id, keyword_created, keyword_name) values (85, '2020-08-08', 'Lotus');
insert into keyword (id, keyword_created, keyword_name) values (86, '2021-03-12', 'Dodge');
insert into keyword (id, keyword_created, keyword_name) values (87, '2021-04-28', 'Oldsmobile');
insert into keyword (id, keyword_created, keyword_name) values (88, '2021-06-04', 'Land Rover');
insert into keyword (id, keyword_created, keyword_name) values (89, '2021-09-16', 'Mercury');
insert into keyword (id, keyword_created, keyword_name) values (90, '2021-01-16', 'Chevrolet');
insert into keyword (id, keyword_created, keyword_name) values (91, '2021-06-17', 'Hyundai');
insert into keyword (id, keyword_created, keyword_name) values (92, '2021-01-10', 'Mercury');
insert into keyword (id, keyword_created, keyword_name) values (93, '2021-01-06', 'Chevrolet');
insert into keyword (id, keyword_created, keyword_name) values (94, '2021-08-27', 'Lincoln');
insert into keyword (id, keyword_created, keyword_name) values (95, '2021-03-07', 'Lincoln');
insert into keyword (id, keyword_created, keyword_name) values (96, '2021-09-26', 'Mitsubishi');
insert into keyword (id, keyword_created, keyword_name) values (97, '2021-08-07', 'Mazda');
insert into keyword (id, keyword_created, keyword_name) values (98, '2020-08-28', 'Ford');
insert into keyword (id, keyword_created, keyword_name) values (99, '2020-10-13', 'Ford');
insert into keyword (id, keyword_created, keyword_name) values (100, '2021-07-14', 'Infiniti');
insert into keyword (id, keyword_created, keyword_name) values (101, '2021-04-28', 'Mercedes-Benz');
insert into keyword (id, keyword_created, keyword_name) values (102, '2020-10-25', 'Volkswagen');
insert into keyword (id, keyword_created, keyword_name) values (103, '2020-12-31', 'Mazda');
insert into keyword (id, keyword_created, keyword_name) values (104, '2021-08-22', 'Mitsubishi');
insert into keyword (id, keyword_created, keyword_name) values (105, '2021-07-25', 'Dodge');
insert into keyword (id, keyword_created, keyword_name) values (106, '2021-02-22', 'Wasabi Powder');
insert into keyword (id, keyword_created, keyword_name) values (107, '2021-06-19', 'Pastry');
insert into keyword (id, keyword_created, keyword_name) values (108, '2020-10-08', 'Sauce');
insert into keyword (id, keyword_created, keyword_name) values (109, '2020-10-06', 'Slab');
insert into keyword (id, keyword_created, keyword_name) values (110, '2021-08-27', 'Bread');
insert into keyword (id, keyword_created, keyword_name) values (111, '2021-05-20', 'Pumpernickel');
insert into keyword (id, keyword_created, keyword_name) values (112, '2020-12-16', 'Croissant');
insert into keyword (id, keyword_created, keyword_name) values (113, '2021-04-14', 'Peppercorn');
insert into keyword (id, keyword_created, keyword_name) values (114, '2020-10-18', 'Beef');
insert into keyword (id, keyword_created, keyword_name) values (115, '2021-07-08', 'Pears');
insert into keyword (id, keyword_created, keyword_name) values (116, '2020-11-16', 'Swordfish');
insert into keyword (id, keyword_created, keyword_name) values (117, '2020-10-21', 'Hamburger');
insert into keyword (id, keyword_created, keyword_name) values (118, '2021-02-28', 'Tea');
insert into keyword (id, keyword_created, keyword_name) values (119, '2020-10-11', 'Cheese');
insert into keyword (id, keyword_created, keyword_name) values (120, '2021-05-13', 'Wine');
insert into keyword (id, keyword_created, keyword_name) values (121, '2021-07-13', 'Sherry');
insert into keyword (id, keyword_created, keyword_name) values (122, '2020-08-08', 'Flax');
insert into keyword (id, keyword_created, keyword_name) values (123, '2020-08-08', 'Chicken');
insert into keyword (id, keyword_created, keyword_name) values (124, '2021-08-20', 'Blueberries');
insert into keyword (id, keyword_created, keyword_name) values (125, '2021-03-03', 'Shrimp');
insert into keyword (id, keyword_created, keyword_name) values (126, '2020-10-28', 'Round');
insert into keyword (id, keyword_created, keyword_name) values (127, '2021-03-04', 'Avola');
insert into keyword (id, keyword_created, keyword_name) values (128, '2021-03-10', 'Loquat');
insert into keyword (id, keyword_created, keyword_name) values (129, '2021-05-28', 'Cream');
insert into keyword (id, keyword_created, keyword_name) values (130, '2021-02-04', 'Sausage');
insert into keyword (id, keyword_created, keyword_name) values (131, '2020-10-04', 'Oil');
insert into keyword (id, keyword_created, keyword_name) values (132, '2021-08-21', 'Veal');
insert into keyword (id, keyword_created, keyword_name) values (133, '2020-10-14', 'Gatorade');
insert into keyword (id, keyword_created, keyword_name) values (134, '2021-05-21', 'Appetizer');
insert into keyword (id, keyword_created, keyword_name) values (135, '2021-03-28', 'Rib');
insert into keyword (id, keyword_created, keyword_name) values (136, '2020-08-30', 'Vinegar');
insert into keyword (id, keyword_created, keyword_name) values (137, '2020-12-08', 'Napkin');
insert into keyword (id, keyword_created, keyword_name) values (138, '2020-09-02', 'Green');
insert into keyword (id, keyword_created, keyword_name) values (139, '2021-07-26', 'Hanawakaba');
insert into keyword (id, keyword_created, keyword_name) values (140, '2020-07-21', 'Miso');
insert into keyword (id, keyword_created, keyword_name) values (141, '2021-08-25', 'Strawberry');
insert into keyword (id, keyword_created, keyword_name) values (142, '2021-09-21', 'Rolls');
insert into keyword (id, keyword_created, keyword_name) values (143, '2021-05-05', 'Bababooyee');
insert into keyword (id, keyword_created, keyword_name) values (144, '2021-02-01', 'Cups');
insert into keyword (id, keyword_created, keyword_name) values (145, '2020-09-11', 'Salmon');
insert into keyword (id, keyword_created, keyword_name) values (146, '2020-09-22', 'Versatainer');
insert into keyword (id, keyword_created, keyword_name) values (147, '2021-04-01', 'Fudge');
insert into keyword (id, keyword_created, keyword_name) values (148, '2020-08-28', 'Potatoes');
insert into keyword (id, keyword_created, keyword_name) values (149, '2020-12-16', 'Penne');
insert into keyword (id, keyword_created, keyword_name) values (150, '2020-08-07', 'Kelp');
insert into keyword (id, keyword_created, keyword_name) values (151, '2020-12-03', 'Caviar');
insert into keyword (id, keyword_created, keyword_name) values (152, '2020-08-20', 'Langers');
insert into keyword (id, keyword_created, keyword_name) values (153, '2021-02-18', 'Sesame');
insert into keyword (id, keyword_created, keyword_name) values (154, '2020-12-23', 'Cake');
insert into keyword (id, keyword_created, keyword_name) values (155, '2021-07-02', 'Island');
insert into keyword (id, keyword_created, keyword_name) values (156, '2021-03-28', 'Tortillas');
insert into keyword (id, keyword_created, keyword_name) values (157, '2020-10-18', 'Marzipan');
insert into keyword (id, keyword_created, keyword_name) values (158, '2021-07-03', 'Peas');
insert into keyword (id, keyword_created, keyword_name) values (159, '2021-06-06', 'Pepper');
insert into keyword (id, keyword_created, keyword_name) values (160, '2020-12-26', 'Peach');
insert into keyword (id, keyword_created, keyword_name) values (161, '2021-09-02', 'Smoked');
insert into keyword (id, keyword_created, keyword_name) values (162, '2021-02-05', 'Tomato');
insert into keyword (id, keyword_created, keyword_name) values (163, '2020-11-14', 'CSeed');
insert into keyword (id, keyword_created, keyword_name) values (164, '2020-08-30', 'Peller');
insert into keyword (id, keyword_created, keyword_name) values (165, '2020-09-10', 'Goodhearth');
insert into keyword (id, keyword_created, keyword_name) values (166, '2021-01-25', 'Duck');
insert into keyword (id, keyword_created, keyword_name) values (167, '2021-09-17', 'Lamb');
insert into keyword (id, keyword_created, keyword_name) values (168, '2021-06-22', 'Cornflakes');
insert into keyword (id, keyword_created, keyword_name) values (169, '2020-10-04', 'Goldschalger');
insert into keyword (id, keyword_created, keyword_name) values (170, '2021-02-07', 'Pancetta');
insert into keyword (id, keyword_created, keyword_name) values (171, '2021-05-10', 'Scallops');
insert into keyword (id, keyword_created, keyword_name) values (172, '2020-08-11', 'Soup');
insert into keyword (id, keyword_created, keyword_name) values (173, '2021-02-19', 'Pheasants');
insert into keyword (id, keyword_created, keyword_name) values (174, '2020-10-12', 'Green Tea');
insert into keyword (id, keyword_created, keyword_name) values (175, '2021-06-06', 'Onions');
insert into keyword (id, keyword_created, keyword_name) values (176, '2020-09-30', 'Grapes');
insert into keyword (id, keyword_created, keyword_name) values (177, '2021-01-21', 'Cookie Choc');
insert into keyword (id, keyword_created, keyword_name) values (178, '2021-03-11', 'Lid');
insert into keyword (id, keyword_created, keyword_name) values (179, '2021-02-01', 'Focaccia');
insert into keyword (id, keyword_created, keyword_name) values (180, '2020-09-26', 'Cream Cheese');
insert into keyword (id, keyword_created, keyword_name) values (181, '2020-12-02', 'Chicken Thigh');
insert into keyword (id, keyword_created, keyword_name) values (182, '2021-08-04', 'Halibut');
insert into keyword (id, keyword_created, keyword_name) values (183, '2021-02-07', 'Lemonade');
insert into keyword (id, keyword_created, keyword_name) values (184, '2020-08-29', 'Shiraz');
insert into keyword (id, keyword_created, keyword_name) values (185, '2021-07-18', 'Cardamon Ground');
insert into keyword (id, keyword_created, keyword_name) values (186, '2021-04-29', 'Papadam');
insert into keyword (id, keyword_created, keyword_name) values (187, '2021-03-10', 'Steak');
insert into keyword (id, keyword_created, keyword_name) values (188, '2021-04-21', 'Toasted');
insert into keyword (id, keyword_created, keyword_name) values (189, '2020-07-22', 'Zucchini');
insert into keyword (id, keyword_created, keyword_name) values (190, '2021-01-22', 'Sugar');
insert into keyword (id, keyword_created, keyword_name) values (191, '2021-04-06', 'Lettuce');
insert into keyword (id, keyword_created, keyword_name) values (192, '2021-02-22', 'Salt');
insert into keyword (id, keyword_created, keyword_name) values (193, '2020-08-28', 'Chili Sauce');
insert into keyword (id, keyword_created, keyword_name) values (194, '2021-01-18', 'Paper');
insert into keyword (id, keyword_created, keyword_name) values (195, '2021-03-14', 'French');
insert into keyword (id, keyword_created, keyword_name) values (196, '2021-06-20', 'Peeled');
insert into keyword (id, keyword_created, keyword_name) values (197, '2020-07-24', 'Pork Loin');
insert into keyword (id, keyword_created, keyword_name) values (198, '2021-05-11', 'Orzo');
insert into keyword (id, keyword_created, keyword_name) values (199, '2021-02-23', 'Wool');
insert into keyword (id, keyword_created, keyword_name) values (200, '2020-10-12', 'Watermelon');
insert into keyword (id, keyword_created, keyword_name) values (201, '2020-12-23', 'Hat');
insert into keyword (id, keyword_created, keyword_name) values (202, '2020-09-11', 'Maintenance');
insert into keyword (id, keyword_created, keyword_name) values (203, '2020-08-06', 'Wings');
insert into keyword (id, keyword_created, keyword_name) values (204, '2020-09-24', 'Spice');
insert into keyword (id, keyword_created, keyword_name) values (205, '2021-06-13', 'Beer');


insert into card_keywords (card_id, keyword_id) values (51, 7);
insert into card_keywords (card_id, keyword_id) values (52, 8);
insert into card_keywords (card_id, keyword_id) values (53, 15);
insert into card_keywords (card_id, keyword_id) values (54, 123);
insert into card_keywords (card_id, keyword_id) values (55, 126);
insert into card_keywords (card_id, keyword_id) values (56, 205);
insert into card_keywords (card_id, keyword_id) values (57, 134);
insert into card_keywords (card_id, keyword_id) values (58, 176);
insert into card_keywords (card_id, keyword_id) values (59, 157);
insert into card_keywords (card_id, keyword_id) values (60, 128);
insert into card_keywords (card_id, keyword_id) values (61, 110);
insert into card_keywords (card_id, keyword_id) values (62, 123);
insert into card_keywords (card_id, keyword_id) values (53, 204);
insert into card_keywords (card_id, keyword_id) values (51, 203);
insert into card_keywords (card_id, keyword_id) values (52, 176);
insert into card_keywords (card_id, keyword_id) values (54, 202);
insert into card_keywords (card_id, keyword_id) values (56, 201);
insert into card_keywords (card_id, keyword_id) values (58, 123);
insert into card_keywords (card_id, keyword_id) values (60, 110);
insert into card_keywords (card_id, keyword_id) values (62, 143);
insert into card_keywords (card_id, keyword_id) values (52, 134);
insert into card_keywords (card_id, keyword_id) values (56, 176);
insert into card_keywords (card_id, keyword_id) values (59, 130);
insert into card_keywords (card_id, keyword_id) values (61, 103);
insert into card_keywords (card_id, keyword_id) values (62, 78);
insert into card_keywords (card_id, keyword_id) values (51, 56);
insert into card_keywords (card_id, keyword_id) values (53, 110);
insert into card_keywords (card_id, keyword_id) values (55, 55);
insert into card_keywords (card_id, keyword_id) values (57, 57);

