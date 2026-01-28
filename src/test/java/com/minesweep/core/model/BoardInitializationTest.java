package com.minesweep.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardInitializationTest {

    @Test
    void testConstructorWithValidParameters() {
        Board board = new Board(5, 5, 5);
        assertEquals(5, board.getRows());
        assertEquals(5, board.getCols());
        assertEquals(5, board.getTotalMines());
        assertEquals(0, board.getRevealedCount());
        assertEquals(0, board.getFlaggedCount());
    }

    @Test
    void testConstructorWithZeroRows() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(0, 5, 5);
        });
        assertEquals("Rows must be greater than 0", exception.getMessage());
    }

    @Test
    void testConstructorWithNegativeRows() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(-1, 5, 5);
        });
        assertEquals("Rows must be greater than 0", exception.getMessage());
    }

    @Test
    void testConstructorWithZeroColumns() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(5, 0, 5);
        });
        assertEquals("Columns must be greater than 0", exception.getMessage());
    }

    @Test
    void testConstructorWithNegativeColumns() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(5, -1, 5);
        });
        assertEquals("Columns must be greater than 0", exception.getMessage());
    }

    @Test
    void testConstructorWithNegativeMines() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(5, 5, -1);
        });
        assertEquals("Total mines must be non-negative", exception.getMessage());
    }

    @Test
    void testConstructorWithTooManyMines() {
        int rows = 3;
        int cols = 3;
        int totalCells = rows * cols;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(rows, cols, totalCells); // 雷数等于总格子数，应该抛异常
        });
        assertEquals("Total mines must be less than rows * columns", exception.getMessage());

        // 雷数超过总格子数，也应该抛异常
        exception = assertThrows(IllegalArgumentException.class, () -> {
            new Board(rows, cols, totalCells + 1);
        });
        assertEquals("Total mines must be less than rows * columns", exception.getMessage());
    }

    @Test
    void testGridInitialization() {
        int rows = 4;
        int cols = 4;
        Board board = new Board(rows, cols, 5);

        // 检查每个位置都有非null的Cell
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                assertNotNull(cell, "Cell at (" + i + ", " + j + ") should not be null");
                // 初始状态下，Cell应该不是雷，未揭示，未标记
                assertFalse(cell.isMine());
                assertFalse(cell.isRevealed());
                assertFalse(cell.isFlagged());
                assertEquals(0, cell.getNeighborMineCount());
            }
        }
    }

    @Test
    void testGetCellWithValidIndices() {
        Board board = new Board(3, 3, 3);
        
        // 测试边界值
        Cell cell00 = board.getCell(0, 0);
        Cell cell22 = board.getCell(2, 2);
        Cell cell11 = board.getCell(1, 1);
        
        assertNotNull(cell00);
        assertNotNull(cell22);
        assertNotNull(cell11);
    }

    @Test
    void testGetCellWithNegativeRow() {
        Board board = new Board(3, 3, 3);
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(-1, 0);
        });
        assertTrue(exception.getMessage().contains("Row index out of bounds"));
    }

    @Test
    void testGetCellWithRowOutOfBounds() {
        Board board = new Board(3, 3, 3);
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(3, 0); // 行索引3超出范围（0-2）
        });
        assertTrue(exception.getMessage().contains("Row index out of bounds"));
    }

    @Test
    void testGetCellWithNegativeColumn() {
        Board board = new Board(3, 3, 3);
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(0, -1);
        });
        assertTrue(exception.getMessage().contains("Column index out of bounds"));
    }

    @Test
    void testGetCellWithColumnOutOfBounds() {
        Board board = new Board(3, 3, 3);
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(0, 3); // 列索引3超出范围（0-2）
        });
        assertTrue(exception.getMessage().contains("Column index out of bounds"));
    }

    @Test
    void testGettersReturnCorrectValues() {
        Board board = new Board(10, 15, 20);
        assertEquals(10, board.getRows());
        assertEquals(15, board.getCols());
        assertEquals(20, board.getTotalMines());
        assertEquals(0, board.getRevealedCount());
        assertEquals(0, board.getFlaggedCount());
    }

    @Test
    void testEdgeCases() {
        // 测试最小尺寸棋盘
        Board minBoard = new Board(1, 1, 0);
        assertEquals(1, minBoard.getRows());
        assertEquals(1, minBoard.getCols());
        assertEquals(0, minBoard.getTotalMines());
        assertNotNull(minBoard.getCell(0, 0));

        // 测试没有雷的棋盘
        Board noMinesBoard = new Board(3, 3, 0);
        assertEquals(0, noMinesBoard.getTotalMines());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = noMinesBoard.getCell(i, j);
                assertNotNull(cell);
                assertFalse(cell.isMine());
            }
        }
    }
}