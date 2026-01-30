package com.minesweep.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void testDefaultConstructor() {
        Cell cell = new Cell();
        assertFalse(cell.isMine());
        assertFalse(cell.isRevealed());
        assertFalse(cell.isFlagged());
        assertEquals(0, cell.getNeighborMineCount());
    }

    @Test
    void testConstructorWithMineParameter() {
        Cell mineCell = new Cell(true);
        assertTrue(mineCell.isMine());
        assertFalse(mineCell.isRevealed());
        assertFalse(mineCell.isFlagged());
        assertEquals(0, mineCell.getNeighborMineCount());

        Cell safeCell = new Cell(false);
        assertFalse(safeCell.isMine());
        assertFalse(safeCell.isRevealed());
        assertFalse(safeCell.isFlagged());
        assertEquals(0, safeCell.getNeighborMineCount());
    }

    @Test
    void testRevealFirstTimeReturnsTrue() {
        Cell cell = new Cell();
        boolean result = cell.reveal();
        assertTrue(result);
        assertTrue(cell.isRevealed());
    }

    @Test
    void testRevealAlreadyRevealedReturnsFalse() {
        Cell cell = new Cell();
        cell.reveal(); // First reveal
        boolean result = cell.reveal(); // Second reveal
        assertFalse(result);
        assertTrue(cell.isRevealed());
    }

    @Test
    void testCycleMarkOnUnrevealedCell() {
        Cell cell = new Cell();
        assertFalse(cell.isFlagged());
        
        cell.cycleMark(false);
        assertTrue(cell.isFlagged());
        
        cell.cycleMark(false);
        assertFalse(cell.isFlagged());
    }

    @Test
    void testCycleMarkOnRevealedCellThrowsException() {
        Cell cell = new Cell();
        cell.reveal();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cell.cycleMark(false);
        });
        assertEquals("Cannot cycle mark on revealed cell", exception.getMessage());
    }

    @Test
    void testStatusTransitions() {
        Cell cell = new Cell();
        
        // Initial state
        assertFalse(cell.isRevealed());
        assertFalse(cell.isFlagged());
        
        // Cycle mark
        cell.cycleMark(false);
        assertFalse(cell.isRevealed());
        assertTrue(cell.isFlagged());
        
        // Reveal should not work if flagged (Flag absolute protection)
        boolean revealResult = cell.reveal();
        assertFalse(revealResult);
        assertFalse(cell.isRevealed());
        assertTrue(cell.isFlagged()); // Flag status remains
        
        // Can still cycle mark on flagged cell (not revealed yet)
        cell.cycleMark(false);
        assertFalse(cell.isRevealed());
        assertFalse(cell.isFlagged()); // Flag should be toggled off
        
        // Now reveal the cell
        revealResult = cell.reveal();
        assertTrue(revealResult);
        assertTrue(cell.isRevealed());
        
        // Cannot cycle mark after reveal
        assertThrows(IllegalStateException.class, () -> {
            cell.cycleMark(false);
        });
    }

    @Test
    void testGetters() {
        Cell cell = new Cell(true);
        assertTrue(cell.isMine());
        assertFalse(cell.isRevealed());
        assertFalse(cell.isFlagged());
        assertEquals(0, cell.getNeighborMineCount());
        
        // Test package-private setters (using reflection if needed, but for simplicity we'll test the logic)
        // Note: In a real scenario, you might use reflection to test package-private methods
        // For this test, we'll focus on the public API
    }
}
