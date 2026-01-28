package com.minesweep.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

class PositionTest {

    @Test
    void testEquality() {
        Position pos1 = new Position(1, 2);
        Position pos2 = new Position(1, 2);
        Position pos3 = new Position(2, 1);
        Position pos4 = new Position(0, 0);

        // 相同坐标应该相等
        assertEquals(pos1, pos2);
        // 不同坐标应该不相等
        assertNotEquals(pos1, pos3);
        assertNotEquals(pos1, pos4);
        // 与null比较应该不相等
        assertNotEquals(pos1, null);
        // 与不同类型对象比较应该不相等
        assertNotEquals(pos1, "Position[row=1, col=2]");
    }

    @Test
    void testHashCodeConsistency() {
        Position pos1 = new Position(3, 4);
        Position pos2 = new Position(3, 4);
        Position pos3 = new Position(4, 3);

        // 相等的对象应该有相同的hashCode
        assertEquals(pos1.hashCode(), pos2.hashCode());
        // 不同的对象可能有不同的hashCode（但不是必须的）
        assertNotEquals(pos1.hashCode(), pos3.hashCode());
    }

    @Test
    void testIllegalParameters() {
        // 测试负数行坐标
        IllegalArgumentException rowException = assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1, 0);
        });
        assertEquals("Row must be non-negative", rowException.getMessage());

        // 测试负数列坐标
        IllegalArgumentException colException = assertThrows(IllegalArgumentException.class, () -> {
            new Position(0, -1);
        });
        assertEquals("Column must be non-negative", colException.getMessage());

        // 测试两个坐标都是负数
        IllegalArgumentException bothException = assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1, -1);
        });
        assertTrue(bothException.getMessage().contains("non-negative"));
    }

    @Test
    void testAsHashMapKey() {
        Map<Position, String> map = new HashMap<>();
        Position pos1 = new Position(2, 3);
        Position pos2 = new Position(2, 3); // 与pos1相同坐标
        Position pos3 = new Position(3, 2); // 不同坐标

        // 放入pos1作为键
        map.put(pos1, "Value for (2,3)");

        // 应该能通过pos2（相同坐标）获取到值，因为equals返回true且hashCode相同
        assertEquals("Value for (2,3)", map.get(pos2));
        // 通过pos3（不同坐标）应该获取不到值
        assertNull(map.get(pos3));

        // 测试size
        assertEquals(1, map.size());

        // 测试containsKey
        assertTrue(map.containsKey(pos1));
        assertTrue(map.containsKey(pos2));
        assertFalse(map.containsKey(pos3));
    }

    @Test
    void testStaticFactoryMethod() {
        Position pos1 = Position.of(1, 2);
        Position pos2 = new Position(1, 2);

        // 静态工厂方法创建的对象应该与构造函数创建的对象相等
        assertEquals(pos1, pos2);
        assertEquals(pos1.hashCode(), pos2.hashCode());

        // 测试静态工厂方法的参数校验
        assertThrows(IllegalArgumentException.class, () -> {
            Position.of(-1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Position.of(0, -1);
        });
    }

    @Test
    void testGetters() {
        Position pos = new Position(5, 7);
        assertEquals(5, pos.getRow());
        assertEquals(7, pos.getCol());
    }

    @Test
    void testToString() {
        Position pos = new Position(2, 3);
        assertEquals("Position[row=2, col=3]", pos.toString());
    }

    @Test
    void testEdgeCases() {
        // 测试边界值 0
        Position pos0 = new Position(0, 0);
        assertEquals(0, pos0.getRow());
        assertEquals(0, pos0.getCol());

        // 测试较大的正值
        Position posLarge = new Position(1000, 2000);
        assertEquals(1000, posLarge.getRow());
        assertEquals(2000, posLarge.getCol());
    }
}