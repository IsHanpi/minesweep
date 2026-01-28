package com.minesweep.console;

import com.minesweep.core.model.Board;

/**
 * Main 类是 Day 1 的临时验证程序，用于验证扫雷游戏的数据结构和基本功能。
 * <p>
 * 该类仅用于开发者验证 Day 1 数据结构的正确性，后续会被真正的 GameLoop 替换。
 * 它创建一个 16x16 的棋盘，在对角线上放置雷，计算数字，并打印 ASCII 地图到控制台。
 */
public class Main {

    /**
     * 主方法，执行验证程序。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        try {
            // 创建 16x16 的棋盘，设置 40 个雷的容量（实际只放置 16 个雷）
            Board board = new Board(16, 16, 40);
            
            // 手动在对角线上放置雷（共 16 个雷）
            System.out.println("Placing mines on diagonal positions...");
            for (int i = 0; i < 16; i++) {
                // 使用反射调用 setMine 方法，因为它是 package-private 的
                try {
                    java.lang.reflect.Method setMineMethod = com.minesweep.core.model.Cell.class.getDeclaredMethod("setMine", boolean.class);
                    setMineMethod.setAccessible(true);
                    setMineMethod.invoke(board.getCell(i, i), true);
                } catch (Exception e) {
                    System.err.println("Error setting mine at (" + i + ", " + i + "):");
                    e.printStackTrace();
                }
            }
            
            // 计算每个非雷单元格的周围雷数
            System.out.println("Calculating neighbor mine counts...");
            board.calculateNumbers();
            
            // 打印 ASCII 地图到控制台
            System.out.println("\nASCII Map:");
            System.out.println("----------------------------------------");
            for (int row = 0; row < 16; row++) {
                for (int col = 0; col < 16; col++) {
                    if (board.getCell(row, col).isMine()) {
                        System.out.print("* ");
                    } else {
                        System.out.print(board.getCell(row, col).getNeighborMineCount() + " ");
                    }
                }
                System.out.println(); // 每行结束打印换行
            }
            System.out.println("----------------------------------------");
            
            // 打印特定位置的详细信息（例如 (0,1) 位置）
            int targetRow = 0;
            int targetCol = 1;
            System.out.println("\nDetailed information for position (" + targetRow + ", " + targetCol + "):");
            System.out.println("Row: " + targetRow + ", Col: " + targetCol);
            System.out.println("Neighbors count: " + board.getNeighbors(targetRow, targetCol).size());
            System.out.println("Is mine: " + board.getCell(targetRow, targetCol).isMine());
            System.out.println("Number: " + board.getCell(targetRow, targetCol).getNeighborMineCount());
            
            System.out.println("\nDay 1 verification completed successfully!");
            System.out.println("This is a temporary verification program and will be replaced by the actual GameLoop.");
            
        } catch (Exception e) {
            System.err.println("Error during Day 1 verification:");
            System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();
        }
    }
}