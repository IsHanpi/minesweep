package com.minesweep.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class BoardIntegrationTest {

    @Test
    void testComplexMapWithSpecificMines() {
        // 创建5x5棋盘
        Board board = new Board(5, 5, 3);
        
        // 在指定位置放置雷
        board.getCell(0, 0).setMine(true);
        board.getCell(2, 2).setMine(true);
        board.getCell(4, 4).setMine(true);
        
        // 计算数字
        board.calculateNumbers();
        
        // 使用assertAll分组断言，确保测试失败时能定位具体断言
        assertAll("验证复杂地图的数字计算",
            () -> {
                // 验证(0,0)周围的数字
                assertEquals(1, board.getCell(0, 1).getNeighborMineCount(), "(0,1) should have 1 mine neighbor");
                assertEquals(1, board.getCell(1, 0).getNeighborMineCount(), "(1,0) should have 1 mine neighbor");
                assertEquals(2, board.getCell(1, 1).getNeighborMineCount(), "(1,1) should have 2 mine neighbors (from (0,0) and (2,2))");
            },
            () -> {
                // 验证(2,2)周围的8个格数字至少为1
                int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
                int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
                for (int i = 0; i < 8; i++) {
                    int row = 2 + dr[i];
                    int col = 2 + dc[i];
                    if (row >= 0 && row < 5 && col >= 0 && col < 5) {
                        int count = board.getCell(row, col).getNeighborMineCount();
                        assertTrue(count >= 1, "Cell at (" + row + ", " + col + ") should have at least 1 mine neighbor");
                    }
                }
            },
            () -> {
                // 验证(4,4)周围的数字
                assertEquals(1, board.getCell(3, 4).getNeighborMineCount(), "(3,4) should have 1 mine neighbor");
                assertEquals(1, board.getCell(4, 3).getNeighborMineCount(), "(4,3) should have 1 mine neighbor");
                assertEquals(2, board.getCell(3, 3).getNeighborMineCount(), "(3,3) should have 2 mine neighbors (from (2,2) and (4,4))");
            }
        );
    }

    @Test
    void testNeighborsAndNumberConsistency() {
        // 创建3x3棋盘
        Board board = new Board(3, 3, 2);
        
        // 放置两个雷
        board.getCell(0, 0).setMine(true);
        board.getCell(2, 2).setMine(true);
        
        // 计算数字
        board.calculateNumbers();
        
        // 选择一个非雷Cell：(1,1)
        Cell centerCell = board.getCell(1, 1);
        assertFalse(centerCell.isMine(), "Center cell should not be a mine");
        
        // 手动计算其邻居中雷的数量
        List<Cell> neighbors = board.getNeighbors(1, 1);
        int manualMineCount = 0;
        for (Cell neighbor : neighbors) {
            if (neighbor.isMine()) {
                manualMineCount++;
            }
        }
        
        // 验证等于getNeighborMineCount()
        assertEquals(manualMineCount, centerCell.getNeighborMineCount(), 
                "Manual mine count should equal neighborMineCount");
    }

    @Test
    void test2DArrayIndexCorrectness() {
        // 创建2x2棋盘
        Board board = new Board(2, 2, 0);
        
        // 验证grid[row][col]的访问与getCell(row,col)返回同一对象
        assertAll("验证2D数组索引正确性",
            () -> {
                Cell cell00 = board.getCell(0, 0);
                Cell cell01 = board.getCell(0, 1);
                Cell cell10 = board.getCell(1, 0);
                Cell cell11 = board.getCell(1, 1);
                
                // 使用反射获取grid字段
                try {
                    java.lang.reflect.Field gridField = Board.class.getDeclaredField("grid");
                    gridField.setAccessible(true);
                    Cell[][] grid = (Cell[][]) gridField.get(board);
                    
                    // 验证返回同一对象
                    assertSame(cell00, grid[0][0], "getCell(0,0) should return same object as grid[0][0]");
                    assertSame(cell01, grid[0][1], "getCell(0,1) should return same object as grid[0][1]");
                    assertSame(cell10, grid[1][0], "getCell(1,0) should return same object as grid[1][0]");
                    assertSame(cell11, grid[1][1], "getCell(1,1) should return same object as grid[1][1]");
                    
                    // 验证修改getCell返回的Cell对象，能通过grid[row][col]观察到变化
                    cell00.reveal();
                    assertTrue(grid[0][0].isRevealed(), "Modifying getCell(0,0) should affect grid[0][0]");
                    
                    cell01.cycleMark(false);
                    assertTrue(grid[0][1].isFlagged(), "Modifying getCell(0,1) should affect grid[0][1]");
                    cell01.cycleMark(false); // 恢复到未标记状态
                    assertFalse(grid[0][1].isFlagged(), "Modifying getCell(0,1) should affect grid[0][1]");
                    
                } catch (Exception e) {
                    fail("Reflection failed: " + e.getMessage());
                }
            }
        );
    }

    @Test
    void testBoundaryProtection() {
        // 测试Board构造函数拒绝0行或0列
        assertAll("测试边界保护",
            () -> {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    new Board(0, 5, 0);
                });
                assertEquals("Rows must be greater than 0", exception.getMessage());
            },
            () -> {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    new Board(5, 0, 0);
                });
                assertEquals("Columns must be greater than 0", exception.getMessage());
            },
            () -> {
                // 测试雷数等于rows*cols时抛异常（必须至少有一个非雷格）
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    new Board(2, 2, 4); // 2x2=4，雷数等于总格子数
                });
                assertEquals("Total mines must be less than rows * columns", exception.getMessage());
            }
        );
    }
}