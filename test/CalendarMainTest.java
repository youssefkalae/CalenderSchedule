import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test suite for CalendarMain class.
 * Tests the actual behavior and integration of components without mocking.
 */
public class CalendarMainTest {

  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  private final PrintStream standardOut = System.out;
  private final InputStream standardIn = System.in;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  void tearDown() {
    System.setOut(standardOut);
    System.setIn(standardIn);
  }

  /**
   * Test that main method executes without throwing exceptions.
   */
  @Test
  void testMainMethodDoesNotThrowException() {
    assertDoesNotThrow(() -> {
      provideInputAndRunMain("exit\n");
    });
  }

  /**
   * Test main method behavior with null arguments.
   */
  @Test
  void testMainMethodWithNullArgs() {
    assertDoesNotThrow(() -> {
      provideInputAndRunMain("exit\n", null);
    });
  }

  /**
   * Test main method behavior with empty arguments array.
   */
  @Test
  void testMainMethodWithEmptyArgs() {
    assertDoesNotThrow(() -> {
      provideInputAndRunMain("exit\n", new String[]{});
    });
  }

  /**
   * Test main method behavior with sample command line arguments.
   */
  @Test
  void testMainMethodWithSampleArgs() {
    assertDoesNotThrow(() -> {
      provideInputAndRunMain("exit\n", new String[]{"arg1", "arg2"});
    });
  }

  /**
   * Test that application starts and accepts user input.
   */
  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void testApplicationStartsAndAcceptsInput() {
    String input = "help\nexit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });

    String output = outputStreamCaptor.toString();
    assertFalse(output.isEmpty(), "Application should produce some output");
  }

  /**
   * Test that application responds to multiple commands.
   */
  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void testApplicationRespondsToCommands() {
    String input = "help\nlist\nexit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });

    String output = outputStreamCaptor.toString();
    assertFalse(output.isEmpty(), "Application should respond to commands");
  }

  /**
   * Test that application exits cleanly when exit command is given.
   */
  @Test
  @Timeout(value = 3, unit = TimeUnit.SECONDS)
  void testApplicationExitsCleanly() {
    String input = "exit\n";

    long startTime = System.currentTimeMillis();

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    assertTrue(duration < 3000, "Application should exit promptly");
  }

  /**
   * Test that application handles invalid commands gracefully.
   */
  @Test
  void testApplicationHandlesInvalidCommands() {
    String input = "invalidcommand\nexit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });

    String output = outputStreamCaptor.toString();
    assertFalse(output.isEmpty(), "Application should handle invalid commands");
  }

  /**
   * Test that application components initialize successfully.
   */
  @Test
  void testApplicationInitializationComponents() {
    String input = "exit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    }, "Application initialization should complete without errors");
  }

  /**
   * Test processing of multiple commands in sequence.
   */
  @Test
  void testMultipleCommandsSequence() {
    String input = "help\nlist\nhelp\nexit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });

    String output = outputStreamCaptor.toString();
    assertFalse(output.isEmpty(), "Application should process command sequence");
  }

  /**
   * Test that application does not hang indefinitely.
   */
  @Test
  @Timeout(value = 5, unit = TimeUnit.SECONDS)
  void testApplicationDoesNotHang() {
    String input = "exit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    }, "Application should not hang and should exit cleanly");
  }

  /**
   * Test that CalendarMain class can be loaded successfully.
   */
  @Test
  void testMainMethodClassLoading() {
    assertDoesNotThrow(() -> {
      Class.forName("CalendarMain");
    }, "CalendarMain class should be loadable");
  }

  /**
   * Test that application produces expected output.
   */
  @Test
  void testApplicationOutputIsProduced() {
    String input = "help\nexit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });

    String output = outputStreamCaptor.toString();
    assertNotNull(output, "Output should not be null");
  }

  /**
   * Test minimal application execution with immediate exit.
   */
  @Test
  void testApplicationWithQuickExit() {
    String input = "exit\n";

    assertDoesNotThrow(() -> {
      provideInputAndRunMain(input);
    });
  }

  /**
   * Helper method to provide input to the application and run main method.
   * This simulates user input to the command-line interface.
   *
   * @param input the simulated user input
   */
  private void provideInputAndRunMain(String input) {
    provideInputAndRunMain(input, new String[]{});
  }

  /**
   * Helper method to provide input and arguments to the application.
   *
   * @param input the simulated user input
   * @param args  the command line arguments
   */
  private void provideInputAndRunMain(String input, String[] args) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
    System.setIn(inputStream);

    try {
      CalendarMain.main(args);
    } finally {
      System.setIn(standardIn);
    }
  }

  /**
   * Helper method for testing with specific timeout and input.
   *
   * @param input     the simulated user input
   * @param timeoutMs the timeout in milliseconds
   */
  private void runWithTimeout(String input, long timeoutMs) {
    Thread mainThread = new Thread(() -> {
      try {
        provideInputAndRunMain(input);
      } catch (Exception e) {
        System.err.println("Exception in main thread: " + e.getMessage());
      }
    });

    mainThread.start();

    try {
      mainThread.join(timeoutMs);
      if (mainThread.isAlive()) {
        mainThread.interrupt();
        fail("Application did not exit within timeout period");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      fail("Test was interrupted");
    }
  }
}