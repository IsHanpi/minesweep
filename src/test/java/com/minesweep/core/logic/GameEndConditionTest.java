package com.minesweep.core.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

class GameEndConditionTest {

    /**
     * FixedMineMapGenerator 生成固定雷位置的4×4地图，雷固定在 (2,2)、(2,3)、(3,2)。
     */
    private static class FixedMineMapGenerator implements MapGenerator {
        @Override
        public void generate(Board board, int firstRow, int firstCol) {
            // 固定在 (2,2)、(2,3)、(3,2) 放置雷
            try {
                java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
                setMineMethod.setAccessible(true);
                
                // 放置雷
                setMineMethod.invoke(board.getCell(2, 2), true);
                setMineMethod.invoke(board.getCell(2, 3), true);
                setMineMethod.invoke(board.getCell(3, 2), true);
            } catch (Exception e) {
                throw new RuntimeException("Error setting mine", e);
            }
        }
    }

    @Test
    void testWinCondition() {
        // 创建 4×4 棋盘，3 个雷
        Board board = new Board(4, 4, 3);
        MapGenerator generator = new FixedMineMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证揭示计数为 12，状态为 PLAYING
        assertEquals(12, board.getRevealedCount(), "Revealed count should be 12 after flood fill");
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING after flood fill");
        
        // 点击 (3,3)，应该触发胜利
        engine.reveal(3, 3);
        
        // 验证揭示计数为 13，状态为 WON
        assertEquals(13, board.getRevealedCount(), "Revealed count should be 13 after clicking (3,3)");
        assertEquals(GameState.WON, engine.getState(), "Game state should be WON when all non-mine cells are revealed");
    }

    @Test
    void testLossCondition() {
        // 创建 4×4 棋盘，3 个雷
        Board board = new Board(4, 4, 3);
        MapGenerator generator = new FixedMineMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证揭示计数为 12，状态为 PLAYING
        assertEquals(12, board.getRevealedCount(), "Revealed count should be 12 after flood fill");
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING after flood fill");
        
        // 点击 (2,2) 踩雷
        engine.reveal(2, 2);
        
        // 验证状态为 LOST，揭示计数为 13（包括雷本身）
        assertEquals(GameState.LOST, engine.getState(), "Game state should be LOST when stepping on mine");
        assertEquals(13, board.getRevealedCount(), "Revealed count should be 13 after stepping on mine (including the mine itself)");
    }

    @Test
    void testFlagSync() {
        // 创建 4×4 棋盘，3 个雷
        Board board = new Board(4, 4, 3);
        MapGenerator generator = new FixedMineMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证初始标记计数为 0
        assertEquals(0, board.getFlaggedCount(), "Flagged count should be 0 initially");
        
        // 标记 (2,2)（雷的位置，肯定没有被揭示）
        engine.cycleMark(2, 2);
        assertEquals(1, board.getFlaggedCount(), "Flagged count should be 1 after flagging (2,2)");
        
        // 再次点击取消标记
        engine.cycleMark(2, 2);
        assertEquals(0, board.getFlaggedCount(), "Flagged count should be 0 after unflagging (2,2)");
        
        // 标记 (2,3)（雷的位置，肯定没有被揭示）
        engine.cycleMark(2, 3);
        assertEquals(1, board.getFlaggedCount(), "Flagged count should be 1 after flagging (2,3)");
        
        // 标记 (3,2)（雷的位置，肯定没有被揭示）
        engine.cycleMark(3, 2);
        assertEquals(2, board.getFlaggedCount(), "Flagged count should be 2 after flagging (3,2)");
    }

    @Test
    void testIllegalOperations() {
        // 创建 4×4 棋盘，3 个雷
        Board board = new Board(4, 4, 3);
        MapGenerator generator = new FixedMineMapGenerator();
        GameEngine engine = new GameEngine(board, generator);
        
        // 测试 READY 状态 cycleMark 抛异常
        IllegalStateException readyCycleMarkException = assertThrows(IllegalStateException.class, () -> {
            engine.cycleMark(0, 0);
        });
        assertEquals("Game is not in PLAYING state", readyCycleMarkException.getMessage());
        
        // 首次点击 (0,0)，进入 PLAYING 状态
        engine.reveal(0, 0);
        
        // 测试 PLAYING 状态 getGameResult 抛异常
        IllegalStateException playingGetGameResultException = assertThrows(IllegalStateException.class, () -> {
            engine.getGameResult();
        });
        assertEquals("Game is not finished", playingGetGameResultException.getMessage());
        
        // 点击 (2,2) 踩雷，进入 LOST 状态
        engine.reveal(2, 2);
        
        // 验证状态为 LOST，揭示计数为 13（包括雷本身）
        assertEquals(GameState.LOST, engine.getState(), "Game state should be LOST when stepping on mine");
        assertEquals(13, board.getRevealedCount(), "Revealed count should be 13 after stepping on mine (including the mine itself)");
        
        // 测试 LOST 状态 reveal 抛异常
        IllegalStateException lostRevealException = assertThrows(IllegalStateException.class, () -> {
            engine.reveal(3, 3);
        });
        assertEquals("Game is not in PLAYING state", lostRevealException.getMessage());
        
        // 测试标记已揭示格子抛异常
        Board newBoard = new Board(4, 4, 3);
        GameEngine newEngine = new GameEngine(newBoard, generator);
        newEngine.reveal(0, 0);
        
        IllegalStateException flagRevealedCellException = assertThrows(IllegalStateException.class, () -> {
            newEngine.cycleMark(0, 0); // (0,0) 已被揭示
        });
        assertEquals("Cannot cycle mark on revealed cell", flagRevealedCellException.getMessage());
    }
}
