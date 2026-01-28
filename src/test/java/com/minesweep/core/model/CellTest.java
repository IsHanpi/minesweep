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
    void testToggleFlagOnUnrevealedCell() {
        Cell cell = new Cell();
        assertFalse(cell.isFlagged());
        
        cell.toggleFlag();
        assertTrue(cell.isFlagged());
        
        cell.toggleFlag();
        assertFalse(cell.isFlagged());
    }

    @Test
    void testToggleFlagOnRevealedCellThrowsException() {
        Cell cell = new Cell();
        cell.reveal();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cell.toggleFlag();
        });
        assertEquals("Cannot toggle flag on revealed cell", exception.getMessage());
    }

    @Test
    void testStatusTransitions() {
        Cell cell = new Cell();
        
        // Initial state
        assertFalse(cell.isRevealed());
        assertFalse(cell.isFlagged());
        
        // Toggle flag
        cell.toggleFlag();
        assertFalse(cell.isRevealed());
        assertTrue(cell.isFlagged());
        
        // Reveal should work even if flagged
        boolean revealResult = cell.reveal();
        assertTrue(revealResult);
        assertTrue(cell.isRevealed());
        assertTrue(cell.isFlagged()); // Flag status remains
        
        // Cannot toggle flag after reveal
        assertThrows(IllegalStateException.class, () -> {
            cell.toggleFlag();
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
