package com.minesweep.core.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void testGameStateIsFinished() {
        // 测试 READY 状态
        assertFalse(GameState.READY.isFinished(), "READY state should not be finished");
        
        // 测试 PLAYING 状态
        assertFalse(GameState.PLAYING.isFinished(), "PLAYING state should not be finished");
        
        // 测试 WON 状态
        assertTrue(GameState.WON.isFinished(), "WON state should be finished");
        
        // 测试 LOST 状态
        assertTrue(GameState.LOST.isFinished(), "LOST state should be finished");
    }

    @Test
    void testGameResultConstructorAndGetters() {
        GameResult result = new GameResult(true, 5000, 5, 20);
        
        assertTrue(result.isWin(), "isWin should return true");
        assertEquals(5000, result.getDurationMillis(), "durationMillis should be 5000");
        assertEquals(5, result.getRemainingMines(), "remainingMines should be 5");
        assertEquals(20, result.getTotalRevealed(), "totalRevealed should be 20");
        
        // 测试失败结果
        GameResult defeatResult = new GameResult(false, 3000, 8, 15);
        
        assertFalse(defeatResult.isWin(), "isWin should return false");
        assertEquals(3000, defeatResult.getDurationMillis(), "durationMillis should be 3000");
        assertEquals(8, defeatResult.getRemainingMines(), "remainingMines should be 8");
        assertEquals(15, defeatResult.getTotalRevealed(), "totalRevealed should be 15");
    }

    @Test
    void testGameResultStaticFactoryMethods() {
        // 测试胜利工厂方法
        GameResult victoryResult = GameResult.victory(10000, 2, 25);
        
        assertTrue(victoryResult.isWin(), "victory() should create a win result");
        assertEquals(10000, victoryResult.getDurationMillis(), "duration should be 10000");
        assertEquals(2, victoryResult.getRemainingMines(), "remainingMines should be 2");
        assertEquals(25, victoryResult.getTotalRevealed(), "totalRevealed should be 25");
        
        // 测试失败工厂方法
        GameResult defeatResult = GameResult.defeat(7500, 4, 18);
        
        assertFalse(defeatResult.isWin(), "defeat() should create a loss result");
        assertEquals(7500, defeatResult.getDurationMillis(), "duration should be 7500");
        assertEquals(4, defeatResult.getRemainingMines(), "remainingMines should be 4");
        assertEquals(18, defeatResult.getTotalRevealed(), "totalRevealed should be 18");
    }

    @Test
    void testGameResultToString() {
        GameResult victoryResult = GameResult.victory(5000, 5, 20);
        String victoryString = victoryResult.toString();
        assertTrue(victoryString.contains("WIN"), "toString should contain WIN for victory");
        assertTrue(victoryString.contains("time=5.0s"), "toString should contain correct time");
        assertTrue(victoryString.contains("revealed=20"), "toString should contain correct revealed count");
        
        GameResult defeatResult = GameResult.defeat(3000, 8, 15);
        String defeatString = defeatResult.toString();
        assertTrue(defeatString.contains("LOSS"), "toString should contain LOSS for defeat");
        assertTrue(defeatString.contains("time=3.0s"), "toString should contain correct time");
        assertTrue(defeatString.contains("revealed=15"), "toString should contain correct revealed count");
    }
}