package com.minesweep.core.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

class FloodFillTest {

    /**
     * EmptyMapGenerator 不放置任何雷，用于测试洪水填充算法。
     */
    private static class EmptyMapGenerator implements MapGenerator {
        @Override
        public void generate(Board board, int firstRow, int firstCol) {
            // 不放置任何雷
        }
    }

    /**
     * BorderedMapGenerator 生成一个带数字边界的空白区。
     * 中心 2x2 区域无雷，周围是数字。
     */
    private static class BorderedMapGenerator implements MapGenerator {
        @Override
        public void generate(Board board, int firstRow, int firstCol) {
            // 在周围放置雷，形成数字边界
            try {
                java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
                setMineMethod.setAccessible(true);
                
                // 放置雷在周围，形成数字边界
                // 例如，对于 5x5 棋盘，在边缘放置雷
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 5; col++) {
                        // 只在边缘放置雷
                        if (row == 0 || row == 4 || col == 0 || col == 4) {
                            setMineMethod.invoke(board.getCell(row, col), true);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error setting mine", e);
            }
        }
    }

    @Test
    void test3x3EmptyAreaFloodFill() {
        // 创建 3x3 棋盘，0 个雷
        Board board = new Board(3, 3, 0);
        MapGenerator generator = new EmptyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 点击中心位置
        engine.reveal(1, 1);
        
        // 验证所有 9 个格子都被揭示
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertTrue(board.getCell(row, col).isRevealed(), "Cell at (" + row + ", " + col + ") should be revealed");
            }
        }
        
        // 验证揭示计数为 9
        assertEquals(9, board.getRevealedCount(), "All 9 cells should be revealed");
    }

    @Test
    void testBorderedEmptyAreaFloodFill() {
        // 创建 5x5 棋盘，16 个雷（周围一圈）
        Board board = new Board(5, 5, 16);
        MapGenerator generator = new BorderedMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 点击中心位置（2,2），这是 2x2 空白区的一部分
        engine.reveal(2, 2);
        
        // 验证中心 2x2 区域的格子都被揭示
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                assertTrue(board.getCell(row, col).isRevealed(), "Cell at (" + row + ", " + col + ") should be revealed");
            }
        }
        
        // 验证边缘的雷没有被揭示（它们是雷，应该不会被揭示）
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (row == 0 || row == 4 || col == 0 || col == 4) {
                    assertFalse(board.getCell(row, col).isRevealed(), "Mine at (" + row + ", " + col + ") should not be revealed");
                }
            }
        }
    }

    @Test
    void testLargeBoardFloodFill() {
        // 创建 30x16 大棋盘，0 个雷，测试性能
        Board board = new Board(30, 16, 0);
        MapGenerator generator = new EmptyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 点击左上角位置，触发洪水填充
        engine.reveal(0, 0);
        
        // 验证所有格子都被揭示
        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 16; col++) {
                assertTrue(board.getCell(row, col).isRevealed(), "Cell at (" + row + ", " + col + ") should be revealed");
            }
        }
        
        // 验证揭示计数为 30*16=480
        assertEquals(480, board.getRevealedCount(), "All 480 cells should be revealed");
    }

    @Test
    void testFlaggedCellProtection() {
        // 创建 3x3 棋盘，0 个雷
        Board board = new Board(3, 3, 0);
        MapGenerator generator = new EmptyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 手动标记中心格子
        Cell centerCell = board.getCell(1, 1);
        try {
            java.lang.reflect.Method toggleFlagMethod = Cell.class.getDeclaredMethod("toggleFlag");
            toggleFlagMethod.setAccessible(true);
            toggleFlagMethod.invoke(centerCell);
            // 更新标记计数
            java.lang.reflect.Method incrementFlaggedCountMethod = Board.class.getDeclaredMethod("incrementFlaggedCount");
            incrementFlaggedCountMethod.setAccessible(true);
            incrementFlaggedCountMethod.invoke(board);
        } catch (Exception e) {
            throw new RuntimeException("Error toggling flag", e);
        }
        
        // 验证中心格子被标记
        assertTrue(board.getCell(1, 1).isFlagged(), "Center cell should be flagged");
        
        // 点击左上角位置，触发洪水填充
        engine.reveal(0, 0);
        
        // 验证除了中心标记的格子外，其他格子都被揭示
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 1 && col == 1) {
                    // 中心格子应该保持标记状态，不被揭示
                    assertTrue(board.getCell(row, col).isFlagged(), "Center flagged cell should remain flagged");
                    assertFalse(board.getCell(row, col).isRevealed(), "Center flagged cell should not be revealed");
                } else {
                    // 其他格子应该被揭示
                    assertTrue(board.getCell(row, col).isRevealed(), "Cell at (" + row + ", " + col + ") should be revealed");
                }
            }
        }
        
        // 验证揭示计数为 8（总共 9 个格子，中心一个未揭示）
        assertEquals(8, board.getRevealedCount(), "8 cells should be revealed, leaving the flagged center cell");
    }

    @Test
    void testRevealReturnsFalseForAlreadyRevealedCell() {
        // 创建 3x3 棋盘，0 个雷
        Board board = new Board(3, 3, 0);
        MapGenerator generator = new EmptyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 首次点击，触发洪水填充
        engine.reveal(0, 0);
        
        // 再次点击同一个格子，应该返回 false
        boolean result = engine.reveal(0, 0);
        assertFalse(result, "Revealing an already revealed cell should return false");
        
        // 揭示计数应该保持不变
        assertEquals(9, board.getRevealedCount(), "Revealed count should remain the same");
    }

    @Test
    void testRevealReturnsFalseForFlaggedCell() {
        // 创建 3x3 棋盘，0 个雷
        Board board = new Board(3, 3, 0);
        MapGenerator generator = new EmptyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 手动标记中心格子
        Cell centerCell = board.getCell(1, 1);
        try {
            java.lang.reflect.Method toggleFlagMethod = Cell.class.getDeclaredMethod("toggleFlag");
            toggleFlagMethod.setAccessible(true);
            toggleFlagMethod.invoke(centerCell);
            // 更新标记计数
            java.lang.reflect.Method incrementFlaggedCountMethod = Board.class.getDeclaredMethod("incrementFlaggedCount");
            incrementFlaggedCountMethod.setAccessible(true);
            incrementFlaggedCountMethod.invoke(board);
        } catch (Exception e) {
            throw new RuntimeException("Error toggling flag", e);
        }
        
        // 点击标记的格子，应该返回 false
        boolean result = engine.reveal(1, 1);
        assertFalse(result, "Revealing a flagged cell should return false");
        
        // 验证格子仍然是标记状态，未被揭示
        assertTrue(board.getCell(1, 1).isFlagged(), "Flagged cell should remain flagged");
        assertFalse(board.getCell(1, 1).isRevealed(), "Flagged cell should not be revealed");
    }
}
