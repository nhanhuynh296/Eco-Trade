-- -- --- Inserting sample users into table ---
INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode)
VALUES (1, '1', 'There Street', 'Thereland', 'Therefield', 'Therethere', '8022');

INSERT INTO address (ad_id, street_number, street_name, country)
VALUES (2, '14A', 'Ballantyne Avenue', 'United States');

INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode)
VALUES (3, '3', 'Here Street', 'Hereland', 'Herefield', 'Herehere', '8022');

INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode)
VALUES (4, '4', 'There Cres', 'Thereland', 'Therefield', 'Therethere', '8022');

INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode)
VALUES (5, '5', 'James Street', 'Kaiapoi', 'Canterbury', 'New Zealand', '8011');

INSERT INTO address (ad_id, street_number, street_name, city, region, country, postcode)
VALUES (6, '5', 'James Street', 'Kaiapoi', 'Canterbury', 'New Zealand', '8011');

INSERT INTO user (user_id, first_name, middle_name, last_name, nickname, bio, email,
                  date_of_birth, phone_number, address_id, created, user_role,
                  password, session_ticket, country_for_currency)
VALUES (1,
        'Christian',
        'Julian',
        'Askey',
        'Rapskey',
        '3rd year Computer Science student',
        'cja128@uclive.ac.nz',
        '2000-06-27',
        '+64 22 350 5775',
        1,
        '2021-03-04',
        'ROLE_USER',
        '$2a$10$UlyUwUfvp6LYXk.Abnih1eP6r.ghWS5NaAJ3ujiKYe9cHy6Qpssee',
        null,
        'Therethere');
--  PASSWORD is password123

INSERT INTO user (user_id, first_name, middle_name, last_name, nickname, bio, email,
                  date_of_birth, phone_number, address_id, created, user_role,
                  password, session_ticket, country_for_currency)
VALUES (2,
        'Swapnil',
        NULL,
        'Bhagat',
        'Bwatty',
        '2nd Pro Software Engineering student',
        'sbh94@uclive.ac.nz',
        '2000-04-29',
        '+64 27 910 2080',
        2,
        '2021-03-04',
        'ROLE_USER',
        '$2a$10$UlyUwUfvp6LYXk.Abnih1eP6r.ghWS5NaAJ3ujiKYe9cHy6Qpssee',
        null,
        'United States');

INSERT INTO user (user_id, first_name, middle_name, last_name, nickname, bio, email,
                  date_of_birth, phone_number, address_id, created, user_role,
                  password, session_ticket, country_for_currency)
VALUES (3,
        'John',
        NULL,
        'Johnson',
        'Johnny',
        'empty Bio',
        'email@here.com.co',
        '2000-04-29',
        '0200 9020',
        3,
        '2021-03-04',
        'ROLE_USER',
        '$2a$10$UlyUwUfvp6LYXk.Abnih1eP6r.ghWS5NaAJ3ujiKYe9cHy6Qpssee',
        null,
        'Herehere');

INSERT INTO user (user_id, first_name, middle_name, last_name, nickname, bio, email,
                  date_of_birth, phone_number, address_id, created, user_role,
                  password, session_ticket, country_for_currency)
VALUES (4,
        'Mary',
        NULL,
        'Mason',
        'Johnny',
        'Words words words',
        'differentEmail@cc.co',
        '2000-04-29',
        '64 00224211',
        4,
        '2021-03-04',
        'ROLE_USER',
        'mary222',
        null,
        'New Zealand');

-- TEST USER
INSERT INTO user (user_id, first_name, middle_name, last_name, nickname, bio, email,
                  date_of_birth, phone_number, address_id, created, user_role,
                  password, session_ticket, country_for_currency)
VALUES (5,
        'Brodan',
        '',
        'Smith',
        'Smithy',
        'N/A',
        'Temporary@email.com',
        '2000-06-27',
        '+64 22 350 5775',
        6,
        '2021-03-04',
        'ROLE_USER',
        '$2a$10$UlyUwUfvp6LYXk.Abnih1eP6r.ghWS5NaAJ3ujiKYe9cHy6Qpssee',
        null,
        'New Zealand');
-- password123


INSERT INTO business (business_id, primary_administrator_id, name, description,
                      address_id, business_type, created)
VALUES (1,
        1,
        'Lumbridge General Store',
        'Sells an assortment of friendly neighbourhood war criminals',
        5,
        'Retail Trade',
        '2021-02-04');

INSERT INTO business_administrators
VALUES (1, 1);

INSERT INTO business (business_id, primary_administrator_id, name, description,
                      address_id, business_type, created)
VALUES (2,
        1,
        'Macs Donald',
        'Deals exclusively in Macaroni and Cheese',
        3,
        'Retail Trade',
        '2011-07-08');


INSERT INTO business_administrators
VALUES (2, 1),
       (2, 4);

INSERT INTO product (product_id, primary_image_id, business, business_id, name, description, manufacturer,
                     recommended_retail_price, created)
VALUES (1,
        null,
        1,
        1,
        'Watties Baked Beans - 420g can',
        'Baked Beans as they should be.',
        'Heinz Wattie''s Limited',
        2.2,
        '2020-07-14'),
       (2,
        null,
        1,
        1,
        'Indomie Instant Noodles',
        'Spicy BBQ noodles',
        'IndoFood',
        3.5,
        '2020-07-14');

INSERT INTO inventory_item (inventory_id, best_before, expires, manufactured, price_per_item, product_id, quantity,
                            sell_by, total_price)
VALUES (null,
        '2022-01-01',
        '2023-01-01',
        '2021-01-01',
        1.5,
        1,
        5,
        null,
        15),
       (null,
        '2022-01-01',
        '2023-01-01',
        '2021-01-01',
        2,
        1,
        16,
        null,
        5),
       (null,
        '2022-01-01',
        '2023-01-01',
        '2021-01-01',
        3,
        1,
        6,
        null,
        20),
       (null,
        '2022-01-01',
        '2023-01-01',
        '2021-01-01',
        4,
        2,
        12,
        null,
        25),
       (null,
        '2022-01-01',
        '2023-01-01',
        '2021-01-01',
        5,
        2,
        20,
        null,
        35);

INSERT INTO listing (inventory_id, quantity, price, more_info, created, closes)
VALUES (1,
        3,
        2.0,
        'Please work',
        '2021-01-02',
        '2023-01-01');

INSERT INTO listing (inventory_id, quantity, price, more_info, created, closes)
VALUES (2,
        3,
        2.0,
        'Some Description',
        '2021-01-02',
        '2023-01-01');

-- FOR SALE CARDS FOR USER 1 --

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (1,
        1,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 1');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (2,
        2,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 2');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (3,
        3,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 3');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (4,
        4,
        'ForSale',
        '2021-07-30',
        '2021-07-29',
        'Subaru',
        'Card for sale 4');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (5,
        5,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 5');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (6,
        2,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 6');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (7,
        5,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 7');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (8,
        1,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 8');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (9,
        2,
        'ForSale',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card for sale 9');

-- WANTED CARDS FOR USER 1 --

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (10,
        3,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 1');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (11,
        4,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 2');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (12,
        5,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 3');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (13,
        2,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 4');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (14,
        3,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 5');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (15,
        1,
        'Wanted',
        '2021-07-30',
        '2021-08-05',
        'Subaru',
        'Card Wanted 6');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (16,
        2,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 7');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (17,
        3,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 8');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (18,
        4,
        'Wanted',
        '2021-07-30',
        '2022-08-27',
        'Subaru',
        'Card Wanted 9');

-- EXCHANGE CARDS FOR USER 1 --

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (19,
        1,
        'Exchange',
        '2021-07-30',
        '2022-08-27',
        'Item 1',
        'Card Exchange 1');

INSERT INTO card(card_id, card_creator, card_section, card_date_created, card_end_period,
                 card_title, card_description)
VALUES (20,
        1,
        'Exchange',
        '2021-07-30',
        '2021-10-01',
        'Item 2',
        'Card Exchange 2 Lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum');

-- KEYWORDS --

INSERT INTO keyword(keyword_created, keyword_name)
VALUES ('2021-08-15', 'Vehicle');

INSERT INTO keyword(keyword_created, keyword_name)
VALUES ('2021-08-15', 'Car');

INSERT INTO keyword(keyword_created, keyword_name)
VALUES ('2021-08-14', 'Auto');

INSERT INTO keyword(keyword_created, keyword_name)
VALUES ('2021-08-13', 'Food');

INSERT INTO keyword(keyword_created, keyword_name)
VALUES ('2021-08-12', 'Free');

INSERT INTO card_keywords(card_id, keyword_id)
VALUES ( 1, 1 );

INSERT INTO card_keywords(card_id, keyword_id)
VALUES ( 1, 2 );

INSERT INTO card_keywords(card_id, keyword_id)
VALUES ( 1, 3 );

INSERT INTO card_keywords(card_id, keyword_id)
VALUES ( 1, 4 );

INSERT INTO card_keywords(card_id, keyword_id)
VALUES ( 1, 5 );

INSERT INTO card_keywords(card_id, keyword_id)
VALUES ( 2, 1 );


-- NOTIFICATIONS --

INSERT INTO notification(id, created_on, delete_on, keyword_id, message, type, recipient_id, category)
VALUES (99, '2021-08-12', null, 1, 'System wide notification', 0, 1, 1);

INSERT INTO notification(id, created_on, delete_on, keyword_id, message, type, recipient_id, category)
VALUES (100, '2021-08-13', null, 1, 'Community listing for "blah" has expired and has been removed', 1, 1, 1);

INSERT INTO notification(id, created_on, delete_on, keyword_id, message, type, recipient_id, category)
VALUES (102, '2021-08-15', null, 1, 'A card you have starred has expired', 3, 1, 1);

INSERT INTO notification(id, created_on, card_id, delete_on, keyword_id, message, type, recipient_id, sender_id, category)
VALUES (null, '2021-08-12', 1, null, 1, 'Hello Christian. I noticed you were selling some fresh goods. ' ||
                                        'In trade I would like to offer you my glorious beard. ' ||
                                        'I know that you cant grow one yourself so I would be willing to ' ||
                                        'give you my own in exchange for your goods. I can always grow another', 5, 1, 2, 1);

INSERT INTO notification(id, created_on, delete_on, keyword_id, message, type, recipient_id, sender_id, listing_id, category)
VALUES (null, '2021-08-15', null, null, 'You liked a listing: ''Indomie Instant Noodles''', 6, 1, null, 2, 1);


INSERT INTO sale(sale_id, business_id, listing_date, number_of_likes, quantity, sale_date, sold_for, product)
VALUES (1, 1, '2021-08-12', 12, 12, '2000-08-12', 23032, 1);

INSERT INTO sale(sale_id, business_id, listing_date, number_of_likes, quantity, sale_date, sold_for, product)
VALUES (2, 1, '2021-08-12', 12, 12, '2021-08-13', 23031, 1);

INSERT INTO sale(sale_id, business_id, listing_date, number_of_likes, quantity, sale_date, sold_for, product)
VALUES (3, 2, '2021-08-12', 12, 12, '2021-08-13', 2332, 2);

