package com.minesweep.core.model;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int neighborMineCount;

    public Cell() {
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.neighborMineCount = 0;
    }

    public Cell(boolean isMine) {
        this.isMine = isMine;
        this.isRevealed = false;
        this.isFlagged = false;
        this.neighborMineCount = 0;
    }

    public boolean reveal() {
        if (isRevealed) {
            return false;
        }
        isRevealed = true;
        return true;
    }

    public void toggleFlag() {
        if (isRevealed) {
            throw new IllegalStateException("Cannot toggle flag on revealed cell");
        }
        isFlagged = !isFlagged;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return isFlagged;
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
}