package be.kdg.team22.checkersservice.domain.board;

import be.kdg.team22.checkersservice.domain.game.exceptions.BoardSizeException;
import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static be.kdg.team22.checkersservice.domain.move.MoveValidator.isCaptureMove;

@ValueObject
public class Board {
    private final int size;
    private final Map<Integer, Piece> grid;

    private Board(final int size) {
        this.size = size;
        this.grid = new HashMap<>();
    }

    public Board(final int size, final Map<Integer, Piece> grid) {
        this.size = size;
        this.grid = grid;
    }

    public static Board create(final int size) {
        if (size != 8 && size != 10) {
            throw new BoardSizeException();
        }

        Board board = new Board(size);
        board.setupInitialPieces();

        return board;
    }

    public void setupInitialPieces() {
        int totalCells = (size * size) / 2;
        for (int cell = 1; cell <= totalCells; cell++) {
            grid.put(cell, null);
        }

        int rowsPerPlayer = switch (size) {
            case 8 -> 3;
            case 10 -> 4;
            default -> throw new BoardSizeException();
        };

        placePlayerPieces(PlayerRole.WHITE, 1, rowsPerPlayer);
        placePlayerPieces(PlayerRole.BLACK, size + 1 - rowsPerPlayer, size);
    }

    public void move(final Move move) {
        Piece piece = grid.get(move.fromCell());

        int row = convertCellNumberToCoordinates(move.toCell())[0];
        boolean shouldPromote = false;
        if (piece.color() == PlayerRole.WHITE && row == size - 1) {
            shouldPromote = true;
        } else if (piece.color() == PlayerRole.BLACK && row == 0) {
            shouldPromote = true;
        }

        if (shouldPromote && !piece.isKing()) {
            piece.promoteToKing();
        }

        if (isCaptureMove(this, piece.color(), move)) {
            int[] fromCoords = convertCellNumberToCoordinates(move.fromCell());
            int[] toCoords = convertCellNumberToCoordinates(move.toCell());
            int middleRow = (fromCoords[0] + toCoords[0]) / 2;
            int middleCol = (fromCoords[1] + toCoords[1]) / 2;
            int middleCell = convertCoordinatesToCellNumber(middleRow, middleCol);
            grid.put(middleCell, null);
        }

        grid.put(move.fromCell(), null);
        grid.put(move.toCell(), piece);
    }

    private void placePlayerPieces(final PlayerRole player, final int startRow, final int endRow) {
        for (int row = startRow; row <= endRow; row++) {
            for (int cell : getCellsInRow(row)) {
                grid.put(cell, new Piece(player, false));
            }
        }
    }

    private int[] getCellsInRow(final int row) {
        final int cellsPerRow = size / 2;
        int[] cells = new int[cellsPerRow];

        int startCell = (row - 1) * cellsPerRow + 1;
        for (int i = 0; i < cellsPerRow; i++) {
            cells[i] = startCell + i;
        }

        return cells;
    }

    public String[][] state() {
        String[][] state = new String[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ((row + col) % 2 != 0) {
                    int cellNumber = convertCoordinatesToCellNumber(row, col);
                    Piece piece = grid.get(cellNumber);
                    state[row][col] = piece != null ? piece.toString() : "  ";
                } else {
                    state[row][col] = "  ";
                }
            }
        }
        return state;
    }

    public int convertCoordinatesToCellNumber(final int row, final int col) {
        if ((row + col) % 2 == 0) {
            throw new OutsidePlayingFieldException();
        }

        return (row * (size / 2)) + (col / 2) + 1;
    }

    public int[] convertCellNumberToCoordinates(final int cellNumber) {
        if (cellNumber < 1 || cellNumber > (size * size) / 2) {
            throw new OutsidePlayingFieldException();
        }

        int cellsPerRow = size / 2;
        int row = (cellNumber - 1) / cellsPerRow;
        int colInRow = (cellNumber - 1) % cellsPerRow;

        int col;
        if (row % 2 == 0) {
            col = 2 * colInRow + 1;
        } else {
            col = 2 * colInRow;
        }

        return new int[]{row, col};
    }

    public int[][] cellsBetween(int[] fromCoords, int[] toCoords) {
        int rowDiff = toCoords[0] - fromCoords[0];
        int colDiff = toCoords[1] - fromCoords[1];
        int distance = Math.abs(rowDiff);

        int[][] cellsBetween = new int[distance - 1][2];

        int rowDirection = rowDiff > 0 ? 1 : -1;
        int colDirection = colDiff > 0 ? 1 : -1;

        for (int i = 1; i < distance; i++) {
            cellsBetween[i - 1][0] = fromCoords[0] + (i * rowDirection);
            cellsBetween[i - 1][1] = fromCoords[1] + (i * colDirection);
        }

        return cellsBetween;
    }

    public Optional<Piece> pieceAt(final int cellNumber) {
        if (cellNumber < 1 || cellNumber > (size * size) / 2) {
            return Optional.empty();
        }
        return Optional.ofNullable(grid.get(cellNumber));
    }

    public int size() {
        return size;
    }

    public Map<Integer, Piece> grid() {
        return grid;
    }
}