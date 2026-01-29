package com.minesweep.core.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.minesweep.core.model.Board;

class GameEngineStructureTest {

    @Test
    void testConstructorInitializesStateToReady() {
        // 创建测试用的 Board 和 MapGenerator
        Board board = new Board(5, 5, 5);
        MapGenerator generator = new MapGenerator() {
            @Override
            public void generate(Board board, int firstRow, int firstCol) {
                // 空实现，仅用于测试
            }
        };
        
        // 创建 GameEngine
        GameEngine engine = new GameEngine(board, generator);
        
        // 验证状态初始化为 READY
        assertEquals(GameState.READY, engine.getState(), "Initial state should be READY");
        // 验证 Board 引用正确
        assertSame(board, engine.getBoard(), "Board reference should be the same");
        // 验证初始标记数为 0
        assertEquals(5, engine.getRemainingMines(), "Remaining mines should equal total mines initially");
    }

    @Test
    void testGetElapsedTimeReturnsZeroWhenReady() {
        // 创建测试用的 Board 和 MapGenerator
        Board board = new Board(5, 5, 5);
        MapGenerator generator = new MapGenerator() {
            @Override
            public void generate(Board board, int firstRow, int firstCol) {
                // 空实现，仅用于测试
            }
        };
        
        // 创建 GameEngine
        GameEngine engine = new GameEngine(board, generator);
        
        // 验证在 READY 状态下返回 0
        assertEquals(0, engine.getElapsedTime(), "Elapsed time should be 0 when state is READY");
    }

    @Test
    void testGetRemainingMinesInitiallyEqualsTotalMines() {
        // 测试不同雷数的情况
        for (int totalMines : new int[]{0, 5, 10, 20}) {
            Board board = new Board(10, 10, totalMines);
            MapGenerator generator = new MapGenerator() {
                @Override
                public void generate(Board board, int firstRow, int firstCol) {
                    // 空实现，仅用于测试
                }
            };
            
            GameEngine engine = new GameEngine(board, generator);
            assertEquals(totalMines, engine.getRemainingMines(), 
                    "Remaining mines should equal total mines initially for " + totalMines + " mines");
        }
    }

    @Test
    void testConstructorWithNullBoardThrowsException() {
        MapGenerator generator = new MapGenerator() {
            @Override
            public void generate(Board board, int firstRow, int firstCol) {
                // 空实现，仅用于测试
            }
        };
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new GameEngine(null, generator);
        });
        assertEquals("Board cannot be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNullGeneratorThrowsException() {
        Board board = new Board(5, 5, 5);
        
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new GameEngine(board, null);
        });
        assertEquals("MapGenerator cannot be null", exception.getMessage());
    }
}
