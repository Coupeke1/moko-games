INSERT INTO games (id, name, base_url, start_endpoint, title, description, price, image_url, store_url, created_at,
                   updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'tic-tac-toe', 'http://localhost:8086', '/api/games', 'Tic Tac Toe',
        'Tic Tac Toe game', 20,
        'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Tic-tac-toe-game-board-with-lines.svg/1200px-Tic-tac-toe-game-board-with-lines.svg.png',
        'https://www.tictactoe.com/', now(), now()),
       ('00000000-0000-0000-0000-000000000002', 'checkers', 'http://localhost:8087', 'api/games', 'Checkers',
        'Checkers game', 60,
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Checkers_board_with_pieces.svg/1200px-Checkers_board_with_pieces.svg.png',
        'https://www.checkers.com/', now(), now());
