INSERT INTO store_catalog (id, price, category, purchase_count)
VALUES ('00000000-0000-0000-0000-000000000001',
        25,
        'PARTY',
        21),
       ('00000000-0000-0000-0000-000000000002',
        45,
        'PARTY',
        54);

INSERT INTO posts (id, entry_id, title, image, type, content)
VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'Stepping Out of Early Access', 'https://media.istockphoto.com/id/1495443150/video/two-people-playing-tic-tac-toe-board-games.jpg?s=640x640&k=20&c=upKtfAXn9mZJ-poRN15631-TLAvAgchQSZNXJO5lFkg=', 'MAJOR_UPDATE',
        '' ||
        '<p>After months of experimentation, player feedback, and more grid-drawing than any sane developer should admit to, Tic Tac Toe is officially out of Early Access! 🎉</p>' ||
        '<p>What started as a humble three-by-three passion project has evolved into a polished, feature-complete experience that redefines what it means to place an X or an O with purpose.</p>' ||
        '<p>Early Access players helped shape everything—from the revamped UI to the surprisingly intense end-game scenarios where one wrong move can cost you eternal bragging rights. Now, with stable matchmaking, smarter AI, and the highly requested “I definitely meant to tap that square” confirmation toggle, the game is ready for the world.</p>' ||
        '<p>This is just the beginning. We’re already exploring post-launch updates like customizable themes and maybe even larger grids, for those who live dangerously.</p>' ||
        '<p>Thank you to everyone who believed in a tiny game with big ambitions.</p>' ||
        '<p>Go forth, place your marks, and may your lines always connect.</p>');

INSERT INTO posts (id, entry_id, title, image, type, content)
VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000002', 'Teaching Checkers Pieces to “Think Twice”', 'https://2018media.idtech.com/2022-07/minimax-algorithm-step-4.jpeg?da21773634',
        'DEVLOG', '' ||
                  '<p>Welcome back to another tiny peek behind the curtain of Checkers: Reinvented! This week’s devlog is all about something deceptively simple but surprisingly complex: hesitation logic.</p>' ||
                  '<p>Yes,
you read that right. Our checkers pieces now think twice before moving.</p>' ||
                  '<p>During testing,
we noticed that our AI played perfectly—but also instantly. It felt robotic, like watching a calculator solve a
board game.So we introduced a new system that makes the AI pause, evaluate, and simulate tiny “what if”
scenarios before committing to a move.The result ? A more natural, almost human rhythm that makes matches feel alive.</p>' ||
                  '<p>We also added subtle animations during these micro-deliberations: a slight wiggle,
a bounce, or a faint glow to show the piece is “thinking.” Early players have already described it as “weirdly
adorable” and “slightly threatening, ” which is exactly the middle ground we were aiming for.</p>' ||
                  '<p>Next week,
we’ll dive into our new crown effects for kings (spoiler: sparkles may be involved).</p>' ||
                  '<p>Until then—keep hopping,
keep capturing, and watch out for those pieces that think a little too hard.</ p>');