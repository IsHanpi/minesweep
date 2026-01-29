package com.minesweep.core.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

class GameEngineIntegrationTest {

    /**
     * DummyMapGenerator 生成固定雷位置的4×4地图，雷固定在 (2,2)、(2,3)、(3,2)。
     */
    private static class DummyMapGenerator implements MapGenerator {
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

    private Board board;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        // 创建 4×4 棋盘，3 个雷
        board = new Board(4, 4, 3);
        MapGenerator generator = new DummyMapGenerator();
        engine = new GameEngine(board, generator);
    }

    @Test
    void testCompleteWinFlow() {
        // 初始状态检查
        assertEquals(GameState.READY, engine.getState(), "Initial state should be READY");
        assertEquals(0, board.getRevealedCount(), "Initial revealed count should be 0");
        assertEquals(0, board.getFlaggedCount(), "Initial flagged count should be 0");
        
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证 Flood Fill 后的状态
        assertEquals(12, board.getRevealedCount(), "Revealed count should be 12 after flood fill");
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING after flood fill");
        
        // 点击 (3,3)，触发胜利
        engine.reveal(3, 3);
        
        // 验证胜利状态
        assertEquals(13, board.getRevealedCount(), "Revealed count should be 13 after clicking (3,3)");
        assertEquals(GameState.WON, engine.getState(), "Game state should be WON when all non-mine cells are revealed");
        
        // 验证游戏结果
        GameResult result = engine.getGameResult();
        assertNotNull(result, "Game result should not be null");
        assertTrue(result.isWin(), "Game result should be victory");
        assertEquals(3, result.getRemainingMines(), "Remaining mines should be 3 (no flags placed)");
        assertEquals(13, result.getTotalRevealed(), "Revealed cells should be 13");
        assertTrue(result.getDurationMillis() >= 0, "Elapsed time should be non-negative");
    }

    @Test
    void testLossFlow() {
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证 Flood Fill 后的状态
        assertEquals(12, board.getRevealedCount(), "Revealed count should be 12 after flood fill");
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING after flood fill");
        
        // 点击 (2,2) 踩雷
        engine.reveal(2, 2);
        
        // 验证失败状态
        assertEquals(GameState.LOST, engine.getState(), "Game state should be LOST when stepping on mine");
        assertEquals(13, board.getRevealedCount(), "Revealed count should be 13 after stepping on mine (including the mine itself)");
        
        // 验证游戏结果
        GameResult result = engine.getGameResult();
        assertNotNull(result, "Game result should not be null");
        assertFalse(result.isWin(), "Game result should be defeat");
        assertEquals(3, result.getRemainingMines(), "Remaining mines should be 3 (all mines)");
        assertEquals(13, result.getTotalRevealed(), "Revealed cells should be 13");
        assertTrue(result.getDurationMillis() >= 0, "Elapsed time should be non-negative");
    }

    @Test
    void testFloodFillBoundary() {
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证 Flood Fill 边界
        assertEquals(12, board.getRevealedCount(), "Revealed count should be 12 after flood fill");
        
        // 验证雷周围的格子是否被正确揭示（应该揭示但不是通过 Flood Fill 连续揭示）
        // 检查 (1,2) 应该被揭示，因为它是 Flood Fill 的边界
        assertTrue(board.getCell(1, 2).isRevealed(), "Cell (1,2) should be revealed as boundary of flood fill");
        // 检查 (2,1) 应该被揭示，因为它是 Flood Fill 的边界
        assertTrue(board.getCell(2, 1).isRevealed(), "Cell (2,1) should be revealed as boundary of flood fill");
        // 检查 (3,1) 应该被揭示，因为它是 Flood Fill 的边界
        assertTrue(board.getCell(3, 1).isRevealed(), "Cell (3,1) should be revealed as boundary of flood fill");
        
        // 检查雷的位置是否未被揭示
        assertFalse(board.getCell(2, 2).isRevealed(), "Mine cell (2,2) should not be revealed");
        assertFalse(board.getCell(2, 3).isRevealed(), "Mine cell (2,3) should not be revealed");
        assertFalse(board.getCell(3, 2).isRevealed(), "Mine cell (3,2) should not be revealed");
    }

    @Test
    void testFlagProtection() {
        // 首次点击 (0,0)，触发 Flood Fill
        engine.reveal(0, 0);
        
        // 验证初始标记计数
        assertEquals(0, board.getFlaggedCount(), "Initial flagged count should be 0");
        
        // 标记 (2,2)（雷的位置）
        engine.toggleFlag(2, 2);
        assertEquals(1, board.getFlaggedCount(), "Flagged count should be 1 after flagging (2,2)");
        
        // 尝试揭示已标记的格子（应该返回 false，不揭示）
        assertFalse(engine.reveal(2, 2), "Should not reveal flagged cell");
        assertFalse(board.getCell(2, 2).isRevealed(), "Flagged mine cell should not be revealed");
        assertEquals(1, board.getFlaggedCount(), "Flagged count should remain 1 after trying to reveal flagged cell");
        
        // 取消标记
        engine.toggleFlag(2, 2);
        assertEquals(0, board.getFlaggedCount(), "Flagged count should be 0 after unflagging (2,2)");
        
        // 尝试标记已揭示的格子（应该抛异常）
        IllegalStateException flaggedRevealedCellException = assertThrows(IllegalStateException.class, () -> {
            engine.toggleFlag(0, 0); // (0,0) 已被揭示
        });
        assertEquals("Cannot flag revealed cell", flaggedRevealedCellException.getMessage());
    }

    @Test
    void testTimerAccuracy() {
        // 初始状态，计时器应该为 0
        long initialTime = engine.getElapsedTime();
        assertEquals(0, initialTime, "Initial elapsed time should be 0");
        
        // 首次点击，启动计时器
        engine.reveal(0, 0);
        
        // 等待一小段时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 验证计时器是否递增
        long timeAfterDelay = engine.getElapsedTime();
        assertTrue(timeAfterDelay > initialTime, "Elapsed time should increase after delay");
        
        // 触发胜利，停止计时器
        engine.reveal(3, 3);
        long finalTime = engine.getElapsedTime();
        
        // 验证计时器值是否为非负数
        assertTrue(finalTime >= 0, "Final elapsed time should be non-negative");
    }

    @Test
    void testIllegalOperationSequence() {
        // 测试 1: 在 READY 状态尝试标记
        IllegalStateException readyToggleFlagException = assertThrows(IllegalStateException.class, () -> {
            engine.toggleFlag(0, 0);
        });
        assertEquals("Game is not in PLAYING state", readyToggleFlagException.getMessage());
        
        // 测试 2: 在 READY 状态尝试获取游戏结果
        IllegalStateException readyGetGameResultException = assertThrows(IllegalStateException.class, () -> {
            engine.getGameResult();
        });
        assertEquals("Game is not finished", readyGetGameResultException.getMessage());
        
        // 进入 PLAYING 状态
        engine.reveal(0, 0);
        
        // 测试 3: 在 PLAYING 状态尝试获取游戏结果
        IllegalStateException playingGetGameResultException = assertThrows(IllegalStateException.class, () -> {
            engine.getGameResult();
        });
        assertEquals("Game is not finished", playingGetGameResultException.getMessage());
        
        // 进入 LOST 状态
        engine.reveal(2, 2);
        
        // 测试 4: 在 LOST 状态尝试揭示
        IllegalStateException lostRevealException = assertThrows(IllegalStateException.class, () -> {
            engine.reveal(3, 3);
        });
        assertEquals("Game is not in PLAYING state", lostRevealException.getMessage());
        
        // 测试 5: 在 LOST 状态尝试标记
        IllegalStateException lostToggleFlagException = assertThrows(IllegalStateException.class, () -> {
            engine.toggleFlag(3, 3);
        });
        assertEquals("Game is not in PLAYING state", lostToggleFlagException.getMessage());
    }
}