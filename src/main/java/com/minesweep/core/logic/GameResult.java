package com.minesweep.core.logic;

/**
 * GameResult 类表示扫雷游戏的结果，是一个不可变的值对象。
 */
public class GameResult {
    private final boolean isWin;
    private final long durationMillis;
    private final int remainingMines;
    private final int totalRevealed;

    /**
     * 创建一个新的 GameResult 实例。
     *
     * @param isWin 是否胜利
     * @param durationMillis 游戏耗时毫秒
     * @param remainingMines 剩余雷数
     * @param totalRevealed 揭示的格子数
     */
    public GameResult(boolean isWin, long durationMillis, int remainingMines, int totalRevealed) {
        this.isWin = isWin;
        this.durationMillis = durationMillis;
        this.remainingMines = remainingMines;
        this.totalRevealed = totalRevealed;
    }

    /**
     * 静态工厂方法，创建胜利的游戏结果。
     *
     * @param duration 游戏耗时毫秒
     * @param remainingMines 剩余雷数
     * @param totalRevealed 揭示的格子数
     * @return 胜利的 GameResult 实例
     */
    public static GameResult victory(long duration, int remainingMines, int totalRevealed) {
        return new GameResult(true, duration, remainingMines, totalRevealed);
    }

    /**
     * 静态工厂方法，创建失败的游戏结果。
     *
     * @param duration 游戏耗时毫秒
     * @param remainingMines 剩余雷数
     * @param totalRevealed 揭示的格子数
     * @return 失败的 GameResult 实例
     */
    public static GameResult defeat(long duration, int remainingMines, int totalRevealed) {
        return new GameResult(false, duration, remainingMines, totalRevealed);
    }

    /**
     * 获取游戏是否胜利。
     *
     * @return 如果游戏胜利返回 true，否则返回 false
     */
    public boolean isWin() {
        return isWin;
    }

    /**
     * 获取游戏耗时毫秒。
     *
     * @return 游戏耗时毫秒
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * 获取剩余雷数。
     *
     * @return 剩余雷数
     */
    public int getRemainingMines() {
        return remainingMines;
    }

    /**
     * 获取揭示的格子数。
     *
     * @return 揭示的格子数
     */
    public int getTotalRevealed() {
        return totalRevealed;
    }

    @Override
    public String toString() {
        String result = isWin ? "WIN" : "LOSS";
        double durationSeconds = durationMillis / 1000.0;
        return "GameResult[" + result + ", time=" + String.format("%.1f", durationSeconds) + "s, revealed=" + totalRevealed + "]";
    }
}