package com.minesweep.core.logic;

/**
 * GameState 枚举表示扫雷游戏的状态。
 */
public enum GameState {
    /**
     * 游戏未开始
     */
    READY,
    
    /**
     * 游戏进行中
     */
    PLAYING,
    
    /**
     * 游戏胜利
     */
    WON,
    
    /**
     * 游戏失败
     */
    LOST;

    /**
     * 检查游戏是否已结束。
     * <p>
     * 当游戏状态为 WON 或 LOST 时，返回 true。
     * 
     * @return 如果游戏已结束返回 true，否则返回 false
     */
    public boolean isFinished() {
        return this == WON || this == LOST;
    }
}