package com.minesweep.core.logic;

import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

public class BasicMapUtil {

    /**
     * DummyMapGenerator 生成固定雷位置的9×9地图，雷固定在指定位置。
     */
    public static class DummyMapGenerator implements MapGenerator {
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

    /**
     * 创建并初始化游戏引擎，使用 DummyMapGenerator 并首次打开 8,8。
     * 
     * @param questionMarkEnabled 是否启用问号标记
     * @return 初始化好的游戏引擎
     */
    public static GameEngine createAndSetupEngine(boolean questionMarkEnabled) {
        // 创建 9×9 棋盘，13 个雷
        Board board = new Board(9, 9, 13);
        MapGenerator generator = new DummyMapGenerator();
        GameEngine engine = new GameEngine(board, generator, questionMarkEnabled);
        
        // 首次打开 8,8，确保游戏正常开局，此时将打开 8,8 及其相邻的 7,7(5) 7,8(2) 8,7(2)
        engine.reveal(8, 8);
        
        return engine;
    }

    /**
     * 创建并初始化游戏引擎，默认启用问号标记。
     * 
     * @return 初始化好的游戏引擎
     */
    public static GameEngine createAndSetupEngine() {
        return createAndSetupEngine(true);
    }
}
