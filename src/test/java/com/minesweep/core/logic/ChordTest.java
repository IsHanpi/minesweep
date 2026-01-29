package com.minesweep.core.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

class ChordTest {

    /**
     * DummyMapGenerator 生成固定雷位置的9×9地图，雷固定在指定位置。
     */
    private static class DummyMapGenerator implements MapGenerator {
        @Override
        public void generate(Board board, int firstRow, int firstCol) {
            try {
                java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
                setMineMethod.setAccessible(true);
                
                // 固定雷位置：0,0 2,2 2,6 3,4 4,3 4,4 5,4 6,2 6,6 6,7 6,8 7,6 8,6
                int[][] minePositions = {
                    {0, 0},
                    {2, 2},
                    {2, 6},
                    {3, 4},
                    {4, 3},
                    {4, 4},
                    {5, 4},
                    {6, 2},
                    {6, 6},
                    {6, 7},
                    {6, 8},
                    {7, 6},
                    {8, 6}
                };
                
                // 放置雷
                for (int[] pos : minePositions) {
                    setMineMethod.invoke(board.getCell(pos[0], pos[1]), true);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error setting mine", e);
            }
        }
    }

    private Board board;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        // 创建 9×9 棋盘，13 个雷
        board = new Board(9, 9, 13);
        MapGenerator generator = new DummyMapGenerator();
        engine = new GameEngine(board, generator);
        
        // 首次打开 8,8
        engine.reveal(8, 8);
    }

    @Test
    void testScenarioA_BatchReveal() {
        // 先揭示目标格子 (3,3)
        engine.reveal(3, 3);
        
        // 标记 (2,2), (3,4), (4,3), (4,4) 为雷
        engine.toggleFlag(2, 2);
        engine.toggleFlag(3, 4);
        engine.toggleFlag(4, 3);
        engine.toggleFlag(4, 4);
        
        // 记录初始揭示计数
        int initialRevealedCount = board.getRevealedCount();
        
        // 执行 chord(3,3)
        boolean chordResult = engine.chord(3, 3);
        
        // 验证 chord 操作成功
        assertTrue(chordResult, "Chord operation should return true");
        
        // 验证揭示计数增加 4
        assertEquals(initialRevealedCount + 4, board.getRevealedCount(), "Revealed count should increase by 4");
        
        // 验证状态为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING");
        
        // 验证 (2,3), (2,4), (3,2), (4,2) 已揭示
        assertTrue(board.getCell(2, 3).isRevealed(), "Cell (2,3) should be revealed");
        assertTrue(board.getCell(2, 4).isRevealed(), "Cell (2,4) should be revealed");
        assertTrue(board.getCell(3, 2).isRevealed(), "Cell (3,2) should be revealed");
        assertTrue(board.getCell(4, 2).isRevealed(), "Cell (4,2) should be revealed");
    }

    @Test
    void testScenarioB_ChainReaction() {
        // 先揭示目标格子 (2,4)
        engine.reveal(2, 4);
        
        // 标记 (3,4) 为雷（1 个 ●）
        engine.toggleFlag(3, 4);
        
        // 执行 chord(2,4)
        boolean chordResult = engine.chord(2, 4);
        
        // 验证 chord 操作成功
        assertTrue(chordResult, "Chord operation should return true");
        
        // 验证 (1,4) 已揭示且为空白
        assertTrue(board.getCell(1, 4).isRevealed(), "Cell (1,4) should be revealed");
        assertEquals(0, board.getCell(1, 4).getNeighborMineCount(), "Cell (1,4) should be blank (neighbor mine count 0)");
        
        // 验证 Flood Fill 连锁揭示
        int revealedCount = board.getRevealedCount();
        assertTrue(revealedCount > 1, "Revealed count should increase by more than 1 due to flood fill");
        
        // 验证状态为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING");
    }

    @Test
    void testScenarioC_WrongFlagStepOnMine() {
        // 先揭示目标格子 (3,3)
        engine.reveal(3, 3);
        
        // 错误标记 (2,2), (3,4), (4,3),(2,4)，漏标 (4,4)（真雷）
        engine.toggleFlag(2, 2);
        engine.toggleFlag(3, 4);
        engine.toggleFlag(4, 3);
        engine.toggleFlag(2, 4);
        
        // 执行 chord(3,3)
        boolean chordResult = engine.chord(3, 3);
        
        // 验证 chord 操作成功
        assertTrue(chordResult, "Chord operation should return true");
        
        // 验证状态立即变为 LOST
        assertEquals(GameState.LOST, engine.getState(), "Game state should be LOST when stepping on mine");
        
        // 验证揭示计数增加
        assertTrue(board.getRevealedCount() > 0, "Revealed count should increase after stepping on mine");
    }

    @Test
    void testScenarioD_InsufficientFlags() {
        // 先揭示目标格子 (3,3)
        engine.reveal(3, 3);
        
        // 仅标记 3 个雷 (2,2), (3,4), (4,3)，标记数不足
        engine.toggleFlag(2, 2);
        engine.toggleFlag(3, 4);
        engine.toggleFlag(4, 3);
        
        // 记录初始揭示计数
        int initialRevealedCount = board.getRevealedCount();
        
        // 执行 chord(3,3)
        boolean chordResult = engine.chord(3, 3);
        
        // 验证 chord 操作返回 false（无任何揭示）
        assertFalse(chordResult, "Chord operation should return false when flag count is insufficient");
        
        // 验证揭示计数不变
        assertEquals(initialRevealedCount, board.getRevealedCount(), "Revealed count should remain unchanged");
        
        // 验证状态为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING");
    }

    @Test
    void testScenarioE_ZeroChord() {
        // 完成开图后标记 0,7 1,7 1,8
        engine.toggleFlag(0, 7);
        engine.toggleFlag(1, 7);
        engine.toggleFlag(1, 8);
        
        // 揭示 0,8
        engine.reveal(0, 8);
        
        // 取消 0,7 1,7 1,8 的标记
        engine.toggleFlag(0, 7);
        engine.toggleFlag(1, 7);
        engine.toggleFlag(1, 8);
        
        // 记录初始揭示计数
        int initialRevealedCount = board.getRevealedCount();
        
        // 执行 chord(0,8)
        boolean chordResult = engine.chord(0, 8);
        
        // 验证 chord 操作成功
        assertTrue(chordResult, "Chord operation should return true for zero cell");
        
        // 验证揭示计数增加
        assertTrue(board.getRevealedCount() > initialRevealedCount, "Revealed count should increase after chord on zero cell");
        
        // 验证状态为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING");
    }

    @Test
    void testScenarioF_ZeroChordSilentFailure() {
        // 完成开图后标记 0,7 1,7 1,8
        engine.toggleFlag(0, 7);
        engine.toggleFlag(1, 7);
        engine.toggleFlag(1, 8);
        
        // 揭示 0,8
        engine.reveal(0, 8);
        
        // 取消 0,7 1,7 的标记
        engine.toggleFlag(0, 7);
        engine.toggleFlag(1, 7);
        // 保留 1,8 的标记
        
        // 记录初始揭示计数
        int initialRevealedCount = board.getRevealedCount();
        
        // 执行 chord(0,8)
        boolean chordResult = engine.chord(0, 8);
        
        // 验证 chord 操作返回 false（条件不满足）
        assertFalse(chordResult, "Chord operation should return false when flag count is not zero for zero cell");
        
        // 验证揭示计数不变
        assertEquals(initialRevealedCount, board.getRevealedCount(), "Revealed count should remain unchanged");
        
        // 验证状态为 PLAYING
        assertEquals(GameState.PLAYING, engine.getState(), "Game state should be PLAYING");
    }
}
