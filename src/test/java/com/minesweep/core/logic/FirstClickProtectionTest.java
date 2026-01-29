package com.minesweep.core.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

class FirstClickProtectionTest {

    /**
     * DummyMapGenerator 仅在 (0,0) 位置放置雷，用于测试首次点击保护机制。
     */
    private static class DummyMapGenerator implements MapGenerator {
        @Override
        public void generate(Board board, int firstRow, int firstCol) {
            // 仅在 (0,0) 位置放置雷
            try {
                java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
                setMineMethod.setAccessible(true);
                setMineMethod.invoke(board.getCell(0, 0), true);
            } catch (Exception e) {
                throw new RuntimeException("Error setting mine", e);
            }
        }
    }

    @Test
    void testFirstClickChangesStateToPlaying() {
        // 创建 10x10 棋盘
        Board board = new Board(10, 10, 1);
        MapGenerator generator = new DummyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 验证初始状态为 READY
        assertEquals(GameState.READY, engine.getState());
        
        // 执行首次点击
        engine.startGame(5, 5);
        
        // 验证状态变为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState());
        // 验证 startTime 被设置
        assertTrue(engine.getElapsedTime() >= 0, "Elapsed time should be >= 0 after start");
    }

    @Test
    void testFirstClickPositionAndNeighborsAreNotMines() {
        // 创建 10x10 棋盘
        Board board = new Board(10, 10, 1);
        MapGenerator generator = new DummyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 执行首次点击
        engine.startGame(5, 5);
        
        // 验证首次点击位置不是雷
        assertFalse(board.getCell(5, 5).isMine(), "First click position should not be a mine");
        
        // 获取首次点击位置的邻居
        java.util.List<Cell> neighbors = board.getNeighbors(5, 5);
        
        // 验证所有邻居都不是雷
        for (Cell neighbor : neighbors) {
            assertFalse(neighbor.isMine(), "First click neighbor should not be a mine");
        }
    }

    @Test
    void testFirstClickProtectionVerification() {
        // 创建 10x10 棋盘
        Board board = new Board(10, 10, 1);
        
        // 创建一个会在首次点击位置放置雷的错误生成器
        MapGenerator badGenerator = new MapGenerator() {
            @Override
            public void generate(Board board, int firstRow, int firstCol) {
                // 在首次点击位置放置雷，这应该被验证机制捕获
                try {
                    java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
                    setMineMethod.setAccessible(true);
                    setMineMethod.invoke(board.getCell(firstRow, firstCol), true);
                } catch (Exception e) {
                    throw new RuntimeException("Error setting mine", e);
                }
            }
        };
        
        GameEngine engine = new GameEngine(board, badGenerator);
        
        // 验证会抛出异常
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            engine.startGame(5, 5);
        });
        assertTrue(exception.getMessage().contains("First click position cannot be a mine"));
    }

    @Test
    void testNonReadyStateThrowsException() {
        // 创建 10x10 棋盘
        Board board = new Board(10, 10, 1);
        MapGenerator generator = new DummyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 首次点击后状态变为 PLAYING
        engine.startGame(5, 5);
        
        // 再次调用 startGame 应该抛出异常
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            engine.startGame(5, 5);
        });
        assertTrue(exception.getMessage().contains("Game already started or not in READY state"));
    }

    @Test
    void testOutOfBoundsThrowsException() {
        // 创建 5x5 棋盘
        Board board = new Board(5, 5, 1);
        MapGenerator generator = new DummyMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 测试行越界
        IndexOutOfBoundsException rowException = assertThrows(IndexOutOfBoundsException.class, () -> {
            engine.startGame(5, 2); // 行索引 5 超出范围
        });
        assertTrue(rowException.getMessage().contains("Row index out of bounds"));
        
        // 测试列越界
        IndexOutOfBoundsException colException = assertThrows(IndexOutOfBoundsException.class, () -> {
            engine.startGame(2, 5); // 列索引 5 超出范围
        });
        assertTrue(colException.getMessage().contains("Column index out of bounds"));
    }

    @Test
    void testRevealTriggersFirstClick() {
        // 创建 10x10 棋盘
        Board board = new Board(10, 10, 3);
        MapGenerator generator = new MapGenerator() {
            @Override
            public void generate(Board board, int firstRow, int firstCol) {
                try {
                    java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
                    setMineMethod.setAccessible(true);
                    setMineMethod.invoke(board.getCell(8, 8), true);
                    setMineMethod.invoke(board.getCell(8, 9), true);
                    setMineMethod.invoke(board.getCell(9, 8), true);
                } catch (Exception e) {
                    throw new RuntimeException("Error setting mine", e);
                }
            }
        };
        GameEngine engine = new GameEngine(board, generator);
        
        // 验证初始状态为 READY
        assertEquals(GameState.READY, engine.getState());
        
        // 调用 reveal 方法，应该触发首次点击
        engine.reveal(5, 5);
        
        // 验证状态变为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState());
        // 验证首次点击位置不是雷
        assertFalse(board.getCell(5, 5).isMine());
    }
}
