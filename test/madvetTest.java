import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class madvetTest {

    private final madvet.Machine machineA = new madvet.Machine(new int[]{1, 0, 0}, new int[]{0, 1, 0});
    private final madvet.Machine machineB = new madvet.Machine(new int[]{0, 1, 0}, new int[]{1, 1, 1});
    private final madvet.Machine machineC = new madvet.Machine(new int[]{0, 0, 1}, new int[]{1, 1, 0});

    @Test
    void testMachineCanRunFowardReturnsTrueWhenInputIsSufficient() {
        int[] input = new int[]{1, 1, 1};
        assertTrue(machineA.canRunForward(input));
        assertTrue(machineB.canRunForward(input));
        assertTrue(machineC.canRunForward(input));
    }

    @Test
    void testMachineCanRunReverseReturnsTrueWhenInputIsSufficient() {
        int[] input = new int[]{1, 1, 1};
        assertTrue(machineA.canRunReverse(input));
        assertTrue(machineB.canRunReverse(input));
        assertTrue(machineC.canRunReverse(input));
    }

    @Test
    void testMachineCanRunForwardReturnsFalseWhenInputIsNotSufficient() {
        int[] input = new int[]{0, 0, 0};
        assertFalse(machineA.canRunForward(input));
        assertFalse(machineB.canRunForward(input));
        assertFalse(machineC.canRunForward(input));
    }

    @Test
    void testMachineCanRunReverseReturnsFalseWhenInputIsNotSufficient() {
        int[] input = new int[]{0, 0, 0};
        assertFalse(machineA.canRunReverse(input));
        assertFalse(machineB.canRunReverse(input));
        assertFalse(machineC.canRunReverse(input));
    }

    @Test
    void testMachineRunsForward() {
        int[] input = new int[]{1, 1, 1};

        int[] output = machineA.runForward(input);
        int[] expectedOutput = new int[]{0, 2, 1};
        assertArrayEquals(output, expectedOutput);

        output = machineB.runForward(input);
        expectedOutput = new int[]{2, 1, 2};
        assertArrayEquals(output, expectedOutput);

        output = machineC.runForward(input);
        expectedOutput = new int[]{2, 2, 0};
        assertArrayEquals(output, expectedOutput);
    }

    @Test
    void testMachineRunsBackward() {
        int[] input = new int[]{1, 1, 1};

        int[] output = machineA.runReverse(input);
        int[] expectedOutput = new int[]{2, 0, 1};
        assertArrayEquals(output, expectedOutput);

        output = machineB.runReverse(input);
        expectedOutput = new int[]{0, 1, 0};
        assertArrayEquals(output, expectedOutput);

        output = machineC.runReverse(input);
        expectedOutput = new int[]{0, 0, 2};
        assertArrayEquals(output, expectedOutput);
    }
}
