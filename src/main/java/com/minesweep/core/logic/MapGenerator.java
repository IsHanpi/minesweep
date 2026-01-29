package com.minesweep.core.logic;

import com.minesweep.core.model.Board;

/**
 * MapGenerator 接口用于生成扫雷游戏的地图。
 * <p>
 * 该接口定义了生成地图的方法，包括放置雷的逻辑。
 */
public interface MapGenerator {
    /**
     * 生成扫雷游戏地图。
     * <p>
     * 在指定的棋盘上放置雷，确保首次点击的位置不是雷。
     *
     * @param board 游戏棋盘
     * @param firstRow 首次点击的行坐标
     * @param firstCol 首次点击的列坐标
     */
    void generate(Board board, int firstRow, int firstCol);
}