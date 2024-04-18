import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TestPersonality {
    private static final InputStream SYSTEM_IN = System.in;
    private static final String MESSAGE_PREFIX = String.format("This program processes a file of answers to " +
            "the%1$sKeirsey Temperament Sorter.  It converts the%1$svarious A and B answers for each person into%1$sa" +
            " sequence of B-percentages and then into a%1$sfour-letter personality type.%1$s", System.lineSeparator());
    private static final String PROMPT_MESSAGE = "input file name? output file name?";
    private static final int SECOND = 1000;
    private static final String EXPECTED_BATCH_FILE_NAME = "tst/test_resources/batch_personality.txt";
    private static final String EXPECTED_OUTPUT_BATCH_FILE_NAME = "tst/test_resources/expected_batch_output.txt";
    private static final String ACTUAL_OUTPUT_BATCH_FILE_NAME = "actual_batch_output.txt";

    private static final String EXPECTED_GIVEN_FILE_NAME = "tst/test_resources/given_personality.txt";
    private static final String EXPECTED_OUTPUT_GIVEN_FILE_NAME = "tst/test_resources/expected_given_output.txt";
    private static final String ACTUAL_OUTPUT_SINGLE_FILE_NAME = "actual_given_output.txt";

    @AfterAll
    public static void cleanUp() throws IOException {
        Files.deleteIfExists((new File(ACTUAL_OUTPUT_BATCH_FILE_NAME)).toPath());
        Files.deleteIfExists((new File(ACTUAL_OUTPUT_SINGLE_FILE_NAME)).toPath());
    }

    @Test
    @Timeout(value = 10, unit = SECONDS)
    public void testPromptMessage() {
        try {
            String correctPrompt = MESSAGE_PREFIX + System.lineSeparator() + PROMPT_MESSAGE;
            passInputToSystemIn(EXPECTED_BATCH_FILE_NAME, ACTUAL_OUTPUT_BATCH_FILE_NAME);
            String mainOutput = captureMainOutput();
            mainOutput = mainOutput.replaceAll("\\s|\\p{Punct}", "");
            correctPrompt = correctPrompt.replaceAll("\\s|\\p{Punct}", "");
            String message = String.format("\"%s\" should contain \"%s\"", mainOutput, correctPrompt);
            Assertions.assertTrue(mainOutput.equalsIgnoreCase(correctPrompt), message);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        } finally {
            System.setIn(SYSTEM_IN);
        }
    }

    @Test
    @Timeout(value = 10, unit = SECONDS)
    public void testGivenFile() throws IOException {
        passInputToSystemIn(EXPECTED_GIVEN_FILE_NAME, ACTUAL_OUTPUT_SINGLE_FILE_NAME);
        Personality.main(null);
        testAreFilesIdentical(EXPECTED_OUTPUT_GIVEN_FILE_NAME, ACTUAL_OUTPUT_SINGLE_FILE_NAME);
    }

    @Test
    @Timeout(value = 10, unit = SECONDS)
    public void testBatchFile() throws IOException {
        passInputToSystemIn(EXPECTED_BATCH_FILE_NAME, ACTUAL_OUTPUT_BATCH_FILE_NAME);
        Personality.main(null);
        testAreFilesIdentical(EXPECTED_OUTPUT_BATCH_FILE_NAME, ACTUAL_OUTPUT_BATCH_FILE_NAME);
    }

    private void passInputToSystemIn(String inputFileName, String outputFileName) {
        String data = String.format("%2$s%1$s%3$s%1$s", System.lineSeparator(), inputFileName, outputFileName);
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    private void testAreFilesIdentical(String expectedFilePath, String actualFilePath) throws IOException {
        Scanner expectedFile = new Scanner(new File(expectedFilePath));
        Scanner actualFile = new Scanner(new File(actualFilePath));
        while (expectedFile.hasNextLine()) {
            String expectedLine = expectedFile.nextLine();
            Assertions.assertTrue(actualFile.hasNextLine(), "Actual File should not have less content than expected");
            String actualLine = actualFile.nextLine();
            String message = String.format("\"%s\" is not equal to \"%s\"%sExpected Line: %1$s%3$sActual Line: %2$s",
                    expectedLine, actualLine, System.lineSeparator());
            Assertions.assertEquals(expectedLine, actualLine, message);
        }
        Assertions.assertFalse(actualFile.hasNextLine(), "Actual File should not have more content than Expected File");
    }

    private static String captureMainOutput() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputStream);
        PrintStream oldOut = System.out;
        System.setOut(ps);

        Personality.main(null);

        System.out.flush();
        System.setOut(oldOut);
        return outputStream.toString();
    }

}
