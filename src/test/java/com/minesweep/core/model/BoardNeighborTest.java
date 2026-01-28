package com.minesweep.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import java.util.List;

class BoardNeighborTest {

    @Test
    void testCenterPositionHas8Neighbors() {
        Board board = new Board(3, 3, 0);
        List<Cell> neighbors = board.getNeighbors(1, 1);
        assertEquals(8, neighbors.size());
        // 验证所有返回的Cell都不为null
        for (Cell cell : neighbors) {
            assertNotNull(cell);
        }
    }

    @Test
    void testCornerPositionHas3Neighbors() {
        Board board = new Board(3, 3, 0);
        List<Cell> neighbors = board.getNeighbors(0, 0);
        assertEquals(3, neighbors.size());
        // 验证所有返回的Cell都不为null
        for (Cell cell : neighbors) {
            assertNotNull(cell);
        }
    }

    @Test
    void testEdgePositionHas5Neighbors() {
        Board board = new Board(3, 3, 0);
        List<Cell> neighbors = board.getNeighbors(0, 1);
        assertEquals(5, neighbors.size());
        // 验证所有返回的Cell都不为null
        for (Cell cell : neighbors) {
            assertNotNull(cell);
        }
    }

    @Test
    void testInvalidPositionThrowsException() {
        Board board = new Board(3, 3, 0);
        // 测试负数行
        IndexOutOfBoundsException rowException = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getNeighbors(-1, 1);
        });
        assertTrue(rowException.getMessage().contains("Row index out of bounds"));

        // 测试行越界
        rowException = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getNeighbors(3, 1);
        });
        assertTrue(rowException.getMessage().contains("Row index out of bounds"));

        // 测试负数列
        IndexOutOfBoundsException colException = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getNeighbors(1, -1);
        });
        assertTrue(colException.getMessage().contains("Column index out of bounds"));

        // 测试列越界
        colException = assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getNeighbors(1, 3);
        });
        assertTrue(colException.getMessage().contains("Column index out of bounds"));
    }

    @Test
    void testIsValidPosition() throws Exception {
        Board board = new Board(3, 3, 0);
        // 使用反射获取私有方法
        Method isValidPositionMethod = Board.class.getDeclaredMethod("isValidPosition", int.class, int.class);
        isValidPositionMethod.setAccessible(true);

        // 测试有效位置
        assertTrue((boolean) isValidPositionMethod.invoke(board, 0, 0));
        assertTrue((boolean) isValidPositionMethod.invoke(board, 1, 1));
        assertTrue((boolean) isValidPositionMethod.invoke(board, 2, 2));

        // 测试无效位置
        assertFalse((boolean) isValidPositionMethod.invoke(board, -1, 0));
        assertFalse((boolean) isValidPositionMethod.invoke(board, 0, -1));
        assertFalse((boolean) isValidPositionMethod.invoke(board, 3, 0));
        assertFalse((boolean) isValidPositionMethod.invoke(board, 0, 3));
        assertFalse((boolean) isValidPositionMethod.invoke(board, 3, 3));
        assertFalse((boolean) isValidPositionMethod.invoke(board, -1, -1));
    }

    @Test
    void testLargerBoardNeighbors() {
        Board board = new Board(5, 5, 0);
        
        // 测试中心位置 (2,2) 应该有8个邻居
        List<Cell> centerNeighbors = board.getNeighbors(2, 2);
        assertEquals(8, centerNeighbors.size());
        
        // 测试边缘位置 (0, 2) 应该有5个邻居
        List<Cell> edgeNeighbors = board.getNeighbors(0, 2);
        assertEquals(5, edgeNeighbors.size());
        
        // 测试角落位置 (4, 4) 应该有3个邻居
        List<Cell> cornerNeighbors = board.getNeighbors(4, 4);
        assertEquals(3, cornerNeighbors.size());
    }

    @Test
    void testNeighborsAreDistinct() {
        Board board = new Board(3, 3, 0);
        List<Cell> neighbors = board.getNeighbors(1, 1);
        
        // 验证所有邻居都是不同的对象
        assertEquals(8, neighbors.size());
        for (int i = 0; i < neighbors.size(); i++) {
            for (int j = i + 1; j < neighbors.size(); j++) {
                assertNotSame(neighbors.get(i), neighbors.get(j));
            }
        }
    }

    @Test
    void testNeighborsDoNotIncludeSelf() {
        Board board = new Board(3, 3, 0);
        Cell centerCell = board.getCell(1, 1);
        List<Cell> neighbors = board.getNeighbors(1, 1);
        
        // 验证邻居列表中不包含自身
        for (Cell cell : neighbors) {
            assertNotSame(centerCell, cell);
        }
    }
}
