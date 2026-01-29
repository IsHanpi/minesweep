package com.minesweep.console;

import com.minesweep.core.logic.GameEngine;
import com.minesweep.core.logic.GameState;
import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;

import java.util.Scanner;

/**
 * Main 类实现控制台版扫雷游戏，基于核心游戏逻辑。
 */
public class Main {

    /**
     * 主方法，启动控制台扫雷游戏。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            
            // 游戏设置
            int rows = 9;
            int cols = 9;
            int totalMines = 10;
            
            System.out.println("========================================");
            System.out.println("          控制台扫雷游戏");
            System.out.println("========================================");
            System.out.println("游戏规则：");
            System.out.println("1. 输入 'r 行 列' 揭示格子");
            System.out.println("2. 输入 'f 行 列' 标记/取消标记格子");
            System.out.println("3. 输入 'c 行 列' 执行 Chord 操作（快速揭示）");
            System.out.println("4. 输入 'q' 退出游戏");
            System.out.println("========================================");
            System.out.println("开始游戏！");
            
            // 初始化游戏
            Board board = new Board(rows, cols, totalMines);
            RandomMapGenerator generator = new RandomMapGenerator();
            GameEngine engine = new GameEngine(board, generator);
            
            // 游戏主循环
            while (true) {
                // 打印游戏状态
                printGameState(engine);
                printBoard(engine);
                
                // 读取用户输入
                System.out.print("请输入命令: ");
                String input = scanner.nextLine().trim();
                
                // 处理用户输入
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("游戏退出！");
                    break;
                } else if (input.isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = input.split(" ");
                    if (parts.length < 3) {
                        System.out.println("命令格式错误，请重新输入！");
                        continue;
                    }
                    
                    String command = parts[0].toLowerCase();
                    int row = Integer.parseInt(parts[1]) - 1; // 转换为 0-based 索引
                    int col = Integer.parseInt(parts[2]) - 1; // 转换为 0-based 索引
                    
                    // 验证坐标
                    if (row < 0 || row >= rows || col < 0 || col >= cols) {
                        System.out.println("坐标超出范围，请重新输入！");
                        continue;
                    }
                    
                    // 执行命令
                    switch (command) {
                        case "r":
                            // 揭示格子
                            boolean revealed = engine.reveal(row, col);
                            if (!revealed) {
                                System.out.println("该格子已揭示或已标记！");
                            }
                            break;
                        case "f":
                            // 标记/取消标记格子
                            engine.toggleFlag(row, col);
                            break;
                        case "c":
                            // 执行 Chord 操作
                            boolean chordResult = engine.chord(row, col);
                            if (!chordResult) {
                                System.out.println("Chord 条件不满足，无操作！");
                            }
                            break;
                        default:
                            System.out.println("未知命令，请重新输入！");
                            break;
                    }
                    
                    // 检查游戏是否结束
                    if (engine.getState() == GameState.WON) {
                        printBoard(engine);
                        System.out.println("========================================");
                        System.out.println("恭喜你，游戏胜利！");
                        System.out.println("用时: " + (engine.getElapsedTime() / 1000) + " 秒");
                        System.out.println("========================================");
                        break;
                    } else if (engine.getState() == GameState.LOST) {
                        printBoard(engine);
                        System.out.println("========================================");
                        System.out.println("游戏失败，你踩到了雷！");
                        System.out.println("用时: " + (engine.getElapsedTime() / 1000) + " 秒");
                        System.out.println("========================================");
                        break;
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("坐标格式错误，请输入数字！");
                } catch (IllegalStateException e) {
                    System.out.println("错误: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("发生错误: " + e.getMessage());
                }
            }
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("游戏初始化失败:");
            e.printStackTrace();
        }
    }
    
    /**
     * 打印游戏状态。
     *
     * @param engine 游戏引擎
     */
    private static void printGameState(GameEngine engine) {
        System.out.println("========================================");
        System.out.println("游戏状态: " + getGameStateString(engine.getState()));
        System.out.println("剩余雷数: " + engine.getRemainingMines());
        System.out.println("用时: " + (engine.getElapsedTime() / 1000) + " 秒");
        System.out.println("========================================");
    }
    
    /**
     * 获取游戏状态的字符串表示。
     *
     * @param state 游戏状态
     * @return 游戏状态的字符串表示
     */
    private static String getGameStateString(GameState state) {
        switch (state) {
            case READY:
                return "准备中";
            case PLAYING:
                return "游戏中";
            case WON:
                return "胜利";
            case LOST:
                return "失败";
            default:
                return "未知";
        }
    }
    
    /**
     * 打印游戏棋盘。
     *
     * @param engine 游戏引擎
     */
    private static void printBoard(GameEngine engine) {
        Board board = engine.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        // 打印列号
        System.out.print("  ");
        for (int c = 0; c < cols; c++) {
            System.out.print((c + 1) + " ");
        }
        System.out.println();
        
        // 打印分隔线
        System.out.print("  ");
        for (int c = 0; c < cols; c++) {
            System.out.print("- ");
        }
        System.out.println();
        
        // 打印棋盘内容
        for (int r = 0; r < rows; r++) {
            // 打印行号
            System.out.print((r + 1) + "|");
            
            // 打印每行的格子
            for (int c = 0; c < cols; c++) {
                Cell cell = board.getCell(r, c);
                if (cell.isFlagged()) {
                    System.out.print("F ");
                } else if (!cell.isRevealed()) {
                    System.out.print(". ");
                } else if (cell.isMine()) {
                    System.out.print("* ");
                } else {
                    int neighborCount = cell.getNeighborMineCount();
                    if (neighborCount == 0) {
                        System.out.print("  ");
                    } else {
                        System.out.print(neighborCount + " ");
                    }
                }
            }
            System.out.println();
        }
    }
}
