package com.minesweep.core.model;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private MarkState markState;
    private int neighborMineCount;

    public enum MarkState {
        NONE,
        FLAGGED,
        QUESTIONED
    }

    public Cell() {
        this.isMine = false;
        this.isRevealed = false;
        this.markState = MarkState.NONE;
        this.neighborMineCount = 0;
    }

    public Cell(boolean isMine) {
        this.isMine = isMine;
        this.isRevealed = false;
        this.markState = MarkState.NONE;
        this.neighborMineCount = 0;
    }

    public boolean reveal() {
        if (isRevealed) return false;
        
        // 绝对保护：Flag 禁止揭示，直接返回
        if (markState == MarkState.FLAGGED) return false;
        
        // 执行揭示（此时只能是 NONE 或 QUESTIONED）
        isRevealed = true;
        
        // 清除标记（Question 重置为 NONE，保持数据整洁）
        if (markState != MarkState.NONE) {
            markState = MarkState.NONE;
        }
        return true;
    }

    public void cycleMark(boolean questionEnabled) {
        if (isRevealed) {
            throw new IllegalStateException("Cannot cycle mark on revealed cell");
        }
        if (questionEnabled) {
            // 三态循环：NONE → FLAGGED → QUESTIONED → NONE
            switch (markState) {
                case NONE:
                    markState = MarkState.FLAGGED;
                    break;
                case FLAGGED:
                    markState = MarkState.QUESTIONED;
                    break;
                case QUESTIONED:
                    markState = MarkState.NONE;
                    break;
            }
        } else {
            // 二态切换：NONE ↔ FLAGGED
            if (markState == MarkState.FLAGGED) {
                markState = MarkState.NONE;
            } else {
                markState = MarkState.FLAGGED;
            }
        }
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return markState == MarkState.FLAGGED;
    }

    public boolean isQuestioned() {
        return markState == MarkState.QUESTIONED;
    }

    public boolean isMarked() {
        return isFlagged() || isQuestioned();
    }

    public int getNeighborMineCount() {
        return neighborMineCount;
    }

    void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    void setNeighborMineCount(int neighborMineCount) {
        this.neighborMineCount = neighborMineCount;
    }

    public MarkState getMarkState() {
        return markState;
    }
}