/**
 * Position 类表示一个不可变的坐标值对象，用于记录网格中的位置。
 * <p>
 * 该类是不可变的，一旦创建，其状态就不能被修改。它提供了
 * 坐标操作所需的基本方法，并且可以作为 HashMap 的键使用。
 */
package com.minesweep.core.model;

import java.util.Objects;

/**
 * 不可变的坐标值对象，用于记录网格中的位置。
 * <p>
 * 该类是不可变的，一旦创建，其状态就不能被修改。它提供了
 * 坐标操作所需的基本方法，并且可以作为 HashMap 的键使用。
 */
public final class Position {
    private final int row;
    private final int col;

    /**
     * 创建一个新的 Position 实例。
     *
     * @param row 行坐标，必须大于等于 0
     * @param col 列坐标，必须大于等于 0
     * @throws IllegalArgumentException 如果 row 或 col 小于 0
     */
    public Position(int row, int col) {
        if (row < 0) {
            throw new IllegalArgumentException("Row must be non-negative");
        }
        if (col < 0) {
            throw new IllegalArgumentException("Column must be non-negative");
        }
        this.row = row;
        this.col = col;
    }

    /**
     * 静态工厂方法，创建一个新的 Position 实例。
     *
     * @param row 行坐标，必须大于等于 0
     * @param col 列坐标，必须大于等于 0
     * @return 新的 Position 实例
     * @throws IllegalArgumentException 如果 row 或 col 小于 0
     */
    public static Position of(int row, int col) {
        return new Position(row, col);
    }

    /**
     * 获取行坐标。
     *
     * @return 行坐标
     */
    public int getRow() {
        return row;
    }

    /**
     * 获取列坐标。
     *
     * @return 列坐标
     */
    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Position[row=" + row + ", col=" + col + "]";
    }
}