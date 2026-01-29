package com.minesweep.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Board 类表示扫雷游戏的棋盘。
 * <p>
 * 该类管理游戏棋盘的状态，包括网格大小、雷的数量以及已揭示和已标记的单元格数量。
 * 棋盘由 Cell 对象的二维数组组成，每个 Cell 可以包含雷或安全区域。
 */
public class Board {
    private final Cell[][] grid;
    private final int rows;
    private final int cols;
    private final int totalMines;
    private int revealedCount;
    private int flaggedCount;

    /**
     * 创建一个新的 Board 实例。
     *
     * @param rows       棋盘的行数，必须大于 0
     * @param cols       棋盘的列数，必须大于 0
     * @param totalMines 棋盘上的雷的总数，必须大于等于 0 且小于 rows * cols
     * @throws IllegalArgumentException 如果 rows 或 cols 小于等于 0，或者 totalMines 小于 0 或大于等于 rows * cols
     */
    public Board(int rows, int cols, int totalMines) {
        if (rows <= 0) {
            throw new IllegalArgumentException("Rows must be greater than 0");
        }
        if (cols <= 0) {
            throw new IllegalArgumentException("Columns must be greater than 0");
        }
        if (totalMines < 0) {
            throw new IllegalArgumentException("Total mines must be non-negative");
        }
        if (totalMines >= rows * cols) {
            throw new IllegalArgumentException("Total mines must be less than rows * columns");
        }

        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        this.revealedCount = 0;
        this.flaggedCount = 0;

        // 初始化网格，填充空 Cell
        this.grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    /**
     * 获取棋盘的行数。
     *
     * @return 棋盘的行数
     */
    public int getRows() {
        return rows;
    }

    /**
     * 获取棋盘的列数。
     *
     * @return 棋盘的列数
     */
    public int getCols() {
        return cols;
    }

    /**
     * 获取棋盘上的雷的总数。
     *
     * @return 棋盘上的雷的总数
     */
    public int getTotalMines() {
        return totalMines;
    }

    /**
     * 获取指定位置的 Cell 对象。
     *
     * @param row 行索引，从 0 开始
     * @param col 列索引，从 0 开始
     * @return 指定位置的 Cell 对象
     * @throws IndexOutOfBoundsException 如果 row 或 col 超出棋盘边界
     */
    public Cell getCell(int row, int col) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }
        return grid[row][col];
    }

    /**
     * 获取指定位置周围8个方向的邻居 Cell。
     *
     * @param row 行索引，从 0 开始
     * @param col 列索引，从 0 开始
     * @return 邻居 Cell 的列表，自动过滤越界位置
     * @throws IndexOutOfBoundsException 如果 row 或 col 超出棋盘边界
     */
    public List<Cell> getNeighbors(int row, int col) {
        // 首先检查输入位置是否有效
        getCell(row, col); // 复用已有的边界检查逻辑
        
        List<Cell> neighbors = new ArrayList<>();
        // 8个方向的偏移量
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        for (int i = 0; i < 8; i++) {
            int newRow = row + dr[i];
            int newCol = col + dc[i];
            if (isValidPosition(newRow, newCol)) {
                neighbors.add(grid[newRow][newCol]);
            }
        }
        
        return neighbors;
    }

    /**
     * 检查指定位置是否在棋盘边界内。
     *
     * @param row 行索引
     * @param col 列索引
     * @return 如果位置有效返回 true，否则返回 false
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * 获取已揭示的单元格数量。
     *
     * @return 已揭示的单元格数量
     */
    public int getRevealedCount() {
        return revealedCount;
    }

    /**
     * 增加已揭示的单元格数量。
     * <p>
     * 当单元格被揭示时调用此方法。
     */
    public void incrementRevealedCount() {
        revealedCount++;
    }

    /**
     * 获取已标记的单元格数量。
     *
     * @return 已标记的单元格数量
     */
    public int getFlaggedCount() {
        return flaggedCount;
    }

    /**
     * 增加已标记的单元格数量。
     * <p>
     * 当单元格被标记时调用此方法。
     */
    public void incrementFlaggedCount() {
        flaggedCount++;
    }

    /**
     * 减少已标记的单元格数量。
     * <p>
     * 当单元格的标记被移除时调用此方法。
     */
    public void decrementFlaggedCount() {
        flaggedCount--;
    }

    /**
     * 计算棋盘上所有非雷单元格的周围雷数。
     * <p>
     * 遍历整个棋盘，对于每个非雷单元格，计算其周围8个方向的雷数，并设置到该单元格的
     * neighborMineCount字段中。
     */
    public void calculateNumbers() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = grid[row][col];
                // 如果是雷，跳过，不需要计算数字
                if (cell.isMine()) {
                    continue;
                }
                // 获取当前单元格的所有邻居
                List<Cell> neighbors = getNeighbors(row, col);
                // 统计邻居中的雷数
                int mineCount = 0;
                for (Cell neighbor : neighbors) {
                    if (neighbor.isMine()) {
                        mineCount++;
                    }
                }
                // 设置当前单元格的周围雷数
                cell.setNeighborMineCount(mineCount);
            }
        }
    }
}