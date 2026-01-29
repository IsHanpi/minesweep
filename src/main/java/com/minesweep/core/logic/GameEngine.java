package com.minesweep.core.logic;

import com.minesweep.core.model.Board;
import com.minesweep.core.model.Cell;
import com.minesweep.core.model.Position;
import java.util.ArrayDeque;
import java.util.List;

/**
 * GameEngine 类是扫雷游戏的核心引擎，管理游戏状态和逻辑。
 */
public class GameEngine {
    private final Board board;
    private GameState state;
    private final MapGenerator generator;
    private boolean firstClickPending;
    private long startTime;
    private long endTime;
    private int flaggedMinesCount;

    /**
     * 创建一个新的 GameEngine 实例。
     *
     * @param board 游戏棋盘
     * @param generator 地图生成器
     * @throws NullPointerException 如果 board 或 generator 为 null
     */
    public GameEngine(Board board, MapGenerator generator) {
        if (board == null) {
            throw new NullPointerException("Board cannot be null");
        }
        if (generator == null) {
            throw new NullPointerException("MapGenerator cannot be null");
        }
        this.board = board;
        this.generator = generator;
        this.state = GameState.READY;
        this.firstClickPending = true;
        this.startTime = -1;
        this.endTime = -1;
        this.flaggedMinesCount = 0;
    }

    /**
     * 获取游戏棋盘。
     * <p>
     * 调用者可以读取棋盘状态，但不应直接修改 Cell 状态，应通过 Command 模式。
     *
     * @return 游戏棋盘
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 获取当前游戏状态。
     *
     * @return 当前游戏状态
     */
    public GameState getState() {
        return state;
    }

    /**
     * 获取游戏已经进行的时间（毫秒）。
     * <p>
     * 如果游戏状态为 READY，则返回 0。
     * 如果游戏状态为 WON 或 LOST，则返回游戏结束时的时间差。
     *
     * @return 游戏已经进行的时间（毫秒）
     */
    public long getElapsedTime() {
        if (state == GameState.READY) {
            return 0;
        }
        if (state == GameState.WON || state == GameState.LOST) {
            return endTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 获取剩余的雷数。
     *
     * @return 剩余的雷数
     */
    public int getRemainingMines() {
        return board.getTotalMines() - flaggedMinesCount;
    }

    /**
     * 开始游戏。
     * <p>
     * 调用地图生成器生成地图，确保首次点击的位置不是雷。
     *
     * @param firstRow 首次点击的行坐标
     * @param firstCol 首次点击的列坐标
     * @throws IllegalStateException 如果游戏状态不是 READY 或已经不是首次点击
     * @throws IndexOutOfBoundsException 如果坐标超出棋盘范围
     */
    public void startGame(int firstRow, int firstCol) {
        // 检查状态
        if (state != GameState.READY || !firstClickPending) {
            throw new IllegalStateException("Game already started or not in READY state");
        }
        
        // 边界检查
        board.getCell(firstRow, firstCol); // 复用已有的边界检查
        
        // 记录开始时间
        startTime = System.currentTimeMillis();
        
        // 调用生成器生成地图
        generator.generate(board, firstRow, firstCol);
        
        // 验证首次点击保护：首次点击位置及其邻居不是雷
        verifyFirstClickProtection(firstRow, firstCol);
        
        // 计算数字
        board.calculateNumbers();
        
        // 更新状态
        firstClickPending = false;
        state = GameState.PLAYING;
    }

    /**
     * 验证首次点击保护机制。
     * <p>
     * 确保首次点击位置及其周围8个邻居不是雷。
     *
     * @param firstRow 首次点击的行坐标
     * @param firstCol 首次点击的列坐标
     * @throws IllegalStateException 如果首次点击保护机制未被正确实现
     */
    private void verifyFirstClickProtection(int firstRow, int firstCol) {
        // 检查首次点击位置不是雷
        if (board.getCell(firstRow, firstCol).isMine()) {
            throw new IllegalStateException("First click position cannot be a mine");
        }
        
        // 检查周围8个邻居不是雷
        try {
            java.lang.reflect.Method getNeighborsMethod = Board.class.getDeclaredMethod("getNeighbors", int.class, int.class);
            getNeighborsMethod.setAccessible(true);
            java.util.List<?> neighbors = (java.util.List<?>) getNeighborsMethod.invoke(board, firstRow, firstCol);
            for (Object neighbor : neighbors) {
                if (neighbor instanceof com.minesweep.core.model.Cell) {
                    com.minesweep.core.model.Cell cell = (com.minesweep.core.model.Cell) neighbor;
                    if (cell.isMine()) {
                        throw new IllegalStateException("First click neighbor cannot be a mine");
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error verifying first click protection", e);
        }
    }

    /**
     * 揭示格子。
     * <p>
     * 如果是首次点击，先调用 startGame 开始游戏。
     *
     * @param row 行坐标
     * @param col 列坐标
     * @return 是否成功揭示（如果格子已经揭示或标记，则返回 false）
     * @throws IllegalStateException 如果游戏状态不是 PLAYING
     * @throws IndexOutOfBoundsException 如果坐标超出棋盘范围
     */
    public boolean reveal(int row, int col) {
        // 如果是首次点击，先开始游戏
        if (firstClickPending) {
            startGame(row, col);
        }
        
        // 边界检查
        Cell cell = board.getCell(row, col);
        
        // 如果单元格已经揭示或标记，直接返回 false
        if (cell.isRevealed() || cell.isFlagged()) {
            return false;
        }
        
        // 检查游戏状态
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("Game is not in PLAYING state");
        }
        
        // 检查是否是雷
        if (cell.isMine()) {
            // 揭示雷，游戏结束，玩家输
            cell.reveal();
            board.incrementRevealedCount();
            state = GameState.LOST;
            // 记录结束时间
            endTime = System.currentTimeMillis();
            return true;
        }
        
        // 如果是空白格子（周围无雷），则进行洪水填充
        if (cell.getNeighborMineCount() == 0) {
            floodFill(row, col);
        } else {
            // 如果是数字格子，直接揭示
            cell.reveal();
            board.incrementRevealedCount();
        }
        
        // 检查是否胜利
        if (checkWin()) {
            state = GameState.WON;
            // 记录结束时间
            endTime = System.currentTimeMillis();
            return true;
        }
        
        return true;
    }

    /**
     * 洪水填充算法，用于自动揭示空白格子及其周围的格子。
     * <p>
     * 使用 BFS 算法，确保每个格子最多处理一次。
     *
     * @param row 起始行坐标
     * @param col 起始列坐标
     */
    private void floodFill(int row, int col) {
        // 使用 ArrayDeque 作为队列，实现 BFS
        ArrayDeque<Position> queue = new ArrayDeque<>();
        queue.offer(Position.of(row, col));
        
        while (!queue.isEmpty()) {
            Position pos = queue.poll();
            int currentRow = pos.getRow();
            int currentCol = pos.getCol();
            
            // 获取当前位置的单元格
            Cell currentCell = board.getCell(currentRow, currentCol);
            
            // 如果单元格已经揭示或标记，跳过
            if (currentCell.isRevealed() || currentCell.isFlagged()) {
                continue;
            }
            
            // 揭示单元格
            currentCell.reveal();
            board.incrementRevealedCount();
            
            // 如果当前单元格是空白格子（周围无雷），则将其邻居加入队列
            if (currentCell.getNeighborMineCount() == 0) {
                // 遍历所有 8 个方向的邻居
                int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
                int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
                
                for (int i = 0; i < 8; i++) {
                    int neighborRow = currentRow + dr[i];
                    int neighborCol = currentCol + dc[i];
                    
                    // 检查邻居位置是否有效
                    if (neighborRow >= 0 && neighborRow < board.getRows() && 
                        neighborCol >= 0 && neighborCol < board.getCols()) {
                        
                        Cell neighborCell = board.getCell(neighborRow, neighborCol);
                        
                        // 如果邻居未揭示且未标记，则加入队列
                        if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                            queue.offer(Position.of(neighborRow, neighborCol));
                        }
                    }
                }
            }
        }
        
        // 洪水填充完成后检查是否胜利
        if (checkWin()) {
            state = GameState.WON;
            // 记录结束时间
            startTime = System.currentTimeMillis() - startTime;
        }
    }

    /**
     * 检查游戏是否胜利。
     * <p>
     * 胜利条件：所有非雷单元格都已揭示。
     *
     * @return 如果游戏胜利，返回 true；否则返回 false
     */
    private boolean checkWin() {
        int totalCells = board.getRows() * board.getCols();
        int nonMineCells = totalCells - board.getTotalMines();
        return board.getRevealedCount() == nonMineCells;
    }

    /**
     * 切换格子的标记状态。
     *
     * @param row 行坐标
     * @param col 列坐标
     * @throws IllegalStateException 如果游戏状态不是 PLAYING，或者格子已经揭示
     * @throws IndexOutOfBoundsException 如果坐标超出棋盘范围
     */
    public void toggleFlag(int row, int col) {
        // 检查游戏状态
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("Game is not in PLAYING state");
        }
        
        // 边界检查
        Cell cell = board.getCell(row, col);
        
        // 禁止标记已揭示格子
        if (cell.isRevealed()) {
            throw new IllegalStateException("Cannot flag revealed cell");
        }
        
        // 切换标记状态
        boolean wasFlagged = cell.isFlagged();
        cell.toggleFlag();
        
        // 根据结果更新标记计数
        if (!wasFlagged && cell.isFlagged()) {
            // 新增标记
            board.incrementFlaggedCount();
            flaggedMinesCount++;
        } else if (wasFlagged && !cell.isFlagged()) {
            // 取消标记
            board.decrementFlaggedCount();
            flaggedMinesCount--;
        }
    }

    /**
     * 获取游戏结果。
     *
     * @return 游戏结果
     * @throws IllegalStateException 如果游戏状态不是 WON 或 LOST
     */
    public GameResult getGameResult() {
        // 检查游戏状态
        if (!state.isFinished()) {
            throw new IllegalStateException("Game is not finished");
        }
        
        // 计算剩余雷数
        int remainingMines = board.getTotalMines() - board.getFlaggedCount();
        
        // 根据游戏状态创建结果
        if (state == GameState.WON) {
            return GameResult.victory(startTime, remainingMines, board.getRevealedCount());
        } else {
            return GameResult.defeat(startTime, remainingMines, board.getRevealedCount());
        }
    }

    /**
     * Chord 操作，当目标格已揭示、非雷、数字 > 0 且周围标记数等于数字时，批量揭示周围未标记未揭示的格子。
     *
     * @param row 行坐标
     * @param col 列坐标
     * @return 是否触发了揭示操作
     * @throws IllegalStateException 如果游戏状态不是 PLAYING，或者目标格未揭示、是雷、数字为 0
     * @throws IndexOutOfBoundsException 如果坐标超出棋盘范围
     */
    public boolean chord(int row, int col) {
        // 如果是首次点击，先开始游戏
        if (firstClickPending) {
            startGame(row, col);
        }
        
        // 检查游戏状态
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("Game is not in PLAYING state");
        }
        
        // 边界检查
        Cell targetCell = board.getCell(row, col);
        
        // 验证目标格状态：已揭示、非雷
        if (!targetCell.isRevealed()) {
            throw new IllegalStateException("Target cell must be revealed");
        }
        if (targetCell.isMine()) {
            throw new IllegalStateException("Target cell cannot be a mine");
        }
        
        // 计算周围标记数
        int flagCount = 0;
        // 遍历所有 8 个方向的邻居
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        // 第一次遍历：计算周围标记数
        for (int i = 0; i < 8; i++) {
            int neighborRow = row + dr[i];
            int neighborCol = col + dc[i];
            
            // 检查邻居位置是否有效
            if (neighborRow >= 0 && neighborRow < board.getRows() && 
                neighborCol >= 0 && neighborCol < board.getCols()) {
                
                Cell neighborCell = board.getCell(neighborRow, neighborCol);
                if (neighborCell.isFlagged()) {
                    flagCount++;
                }
            }
        }
        
        // 如果周围标记数不等于目标格的数字，则不执行任何操作
        if (flagCount != targetCell.getNeighborMineCount()) {
            return false;
        }
        
        // 第二次遍历：批量揭示未标记未揭示的邻居
        boolean hasRevealed = false;
        for (int i = 0; i < 8; i++) {
            int neighborRow = row + dr[i];
            int neighborCol = col + dc[i];
            
            // 检查邻居位置是否有效
            if (neighborRow >= 0 && neighborRow < board.getRows() && 
                neighborCol >= 0 && neighborCol < board.getCols()) {
                
                Cell neighborCell = board.getCell(neighborRow, neighborCol);
                
                // 如果邻居未揭示且未标记，则揭示
                if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                    // 调用 reveal 方法进行揭示
                    if (reveal(neighborRow, neighborCol)) {
                        hasRevealed = true;
                    }
                    
                    // 如果揭示后游戏状态变为 LOST（踩雷），则立即返回
                    if (state == GameState.LOST) {
                        return true;
                    }
                }
            }
        }
        
        return hasRevealed;
    }
}