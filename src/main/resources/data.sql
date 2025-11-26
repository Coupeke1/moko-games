-- Game 1 in library (favourite = TRUE)
INSERT INTO user_library (id, user_id, game_id, purchased_at, favourite)
VALUES ('ae279f38-689a-4a20-bc51-05eb4a5c344d',
        'cf36d3e1-a1e0-4c18-89eb-95fa76fc81ee',
        '00000000-0000-0000-0000-000000000001',
        '2025-11-25T20:19:20.758Z',
        TRUE);

-- Game 2 in library (favourite = FALSE)
INSERT INTO user_library (id, user_id, game_id, purchased_at, favourite)
VALUES ('bb7f60af-3ea7-4e0f-a6c5-3bb3e8269aa9',
        'cf36d3e1-a1e0-4c18-89eb-95fa76fc81ee',
        '00000000-0000-0000-0000-000000000002',
        '2025-11-26T10:15:00.000Z',
        FALSE);
