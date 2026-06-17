import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// runs existing tests through Maven so they don't need manual javac
class LegacyTestSuiteTest {

    @Test
    void selfTestPasses() {
        Main.selfTest();
    }

    @Test
    void characterizationTestsPass() {
        assertEquals(0, GameTest.runTests(), "one or more characterization tests failed");
    }
}
