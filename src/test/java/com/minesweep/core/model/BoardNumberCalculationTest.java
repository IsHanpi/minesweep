package com.minesweep.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardNumberCalculationTest {

    @Test
    void testCalculateNumbersWithCenterMine() {
        // 创建3x3棋盘
        Board board = new Board(3, 3, 1);
        
        // 手动设置中心(1,1)为雷
        board.getCell(1, 1).setMine(true);
        
        // 计算数字
        board.calculateNumbers();
        
        // 验证周围8格的neighborMineCount都是1
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 1 && col == 1) {
                    // 中心是雷，跳过
                    continue;
                }
                assertEquals(1, board.getCell(row, col).getNeighborMineCount(), 
                        "Cell at (" + row + ", " + col + ") should have neighborMineCount = 1");
            }
        }
    }

    @Test
    void testCalculateNumbersWithDiagonalMines() {
        // 创建2x2棋盘
        Board board = new Board(2, 2, 2);
        
        // 手动设置对角为雷
        board.getCell(0, 0).setMine(true);
        board.getCell(1, 1).setMine(true);
        
        // 计算数字
        board.calculateNumbers();
        
        // 验证非雷格的数字是2
        assertEquals(2, board.getCell(0, 1).getNeighborMineCount());
        assertEquals(2, board.getCell(1, 0).getNeighborMineCount());
    }

    @Test
    void testCalculateNumbersWithNoMines() {
        // 创建2x2棋盘，无雷
        Board board = new Board(2, 2, 0);
        
        // 计算数字
        board.calculateNumbers();
        
        // 验证所有格neighborMineCount为0
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                assertEquals(0, board.getCell(row, col).getNeighborMineCount(), 
                        "Cell at (" + row + ", " + col + ") should have neighborMineCount = 0");
            }
        }
    }

    @Test
    void testCalculateNumbersWithMultipleMines() {
        // 创建3x3棋盘
        Board board = new Board(3, 3, 3);
        
        // 设置三个雷
        board.getCell(0, 0).setMine(true);
        board.getCell(0, 2).setMine(true);
        board.getCell(2, 0).setMine(true);
        
        // 计算数字
        board.calculateNumbers();
        
        // 验证特定位置的数字
        // (0,1) 旁边有两个雷：(0,0) 和 (0,2)
        assertEquals(2, board.getCell(0, 1).getNeighborMineCount());
        // (1,0) 旁边有两个雷：(0,0) 和 (2,0)
        assertEquals(2, board.getCell(1, 0).getNeighborMineCount());
        // (1,1) 旁边有三个雷：(0,0), (0,2), (2,0)
        assertEquals(3, board.getCell(1, 1).getNeighborMineCount());
        // (1,2) 旁边有一个雷：(0,2)
        assertEquals(1, board.getCell(1, 2).getNeighborMineCount());
        // (2,1) 旁边有一个雷：(2,0)
        assertEquals(1, board.getCell(2, 1).getNeighborMineCount());
        // (2,2) 旁边没有雷
        assertEquals(0, board.getCell(2, 2).getNeighborMineCount());
    }
}
