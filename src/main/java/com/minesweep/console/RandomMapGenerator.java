package com.minesweep.console;

import com.minesweep.core.logic.MapGenerator;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

import java.util.Random;

/**
 * RandomMapGenerator 实现 MapGenerator 接口，用于在控制台游戏中生成随机地图。
 */
public class RandomMapGenerator implements MapGenerator {
    private final Random random = new Random();

    @Override
    public void generate(Board board, int firstRow, int firstCol) {
        try {
            java.lang.reflect.Method setMineMethod = Cell.class.getDeclaredMethod("setMine", boolean.class);
            setMineMethod.setAccessible(true);

            int rows = board.getRows();
            int cols = board.getCols();
            int totalMines = board.getTotalMines();
            int placedMines = 0;

            // 确保首次点击位置及其周围不是雷
            boolean[][] safeZone = new boolean[rows][cols];
            for (int r = Math.max(0, firstRow - 1); r <= Math.min(rows - 1, firstRow + 1); r++) {
                for (int c = Math.max(0, firstCol - 1); c <= Math.min(cols - 1, firstCol + 1); c++) {
                    safeZone[r][c] = true;
                }
            }

            // 随机放置雷
            while (placedMines < totalMines) {
                int r = random.nextInt(rows);
                int c = random.nextInt(cols);

                // 跳过安全区
                if (!safeZone[r][c] && !board.getCell(r, c).isMine()) {
                    setMineMethod.invoke(board.getCell(r, c), true);
                    placedMines++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating map", e);
        }
    }
}
