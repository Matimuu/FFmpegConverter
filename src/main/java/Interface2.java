import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/28 15:01
 */
public class Interface2 {
    private static final Logger logger = LogManager.getLogger("mainLogger");
    private static final Scanner scanner = new Scanner(System.in);
    private static String formatName;

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        logger.info("Process is started...");
        logger.info("Destination format and folder:");
        logger.info("1.LIVE 2.PREVIA 3.CHAMPIONS 4.SHOW");

        int index = scanner.nextInt();
        formatName = switch (index) {
            case 1 -> Format.LIVE.getName();
            case 2 -> Format.PREVIA.getName();
            case 3 -> Format.CHAMPIONS.getName();
            case 4 -> Format.SHOW.getName();
            default -> {
                logger.error("Unexpected value: " + index);
                throw new IllegalStateException();
            }
        };
        logger.info("You choose: " + formatName);

        List<File> inputFolders = new ArrayList<>();
        inputFolders.add(new File("/Users/omarenrique/Desktop/tests/SAMPLE VIDEO"));
        inputFolders.add(new File("/Users/omarenrique/Desktop/tests/Sample 2"));
//        inputFolders.add(new File("/Users/omarenrique/Desktop/tests/Sample 2"));
//        inputFolders.add(new File("/Users/omarenrique/Desktop/tests/Sample 2"));
//        inputFolders.add(new File("/Users/omarenrique/Desktop/tests/Sample 2"));
//        inputFolders.add(new File("/Users/omarenrique/Desktop/tests/Sample 2"));

        List<Thread> threads = new ArrayList<>();
        for (File inputFolder : inputFolders) {
            Converter2 converter = new Converter2(inputFolder, formatName);
            Thread thread = new Thread(converter);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("Error joining thread: " + e.getMessage());
            }
        }
        logger.info(String.format("Process ended! it took: %.2f min", ((System.nanoTime() - startTime) / 1000_000_000 / 60.0)));
        Copy();
    }

    private static void Copy() {
        File outputFolderToCopy = new File("/Users/omarenrique/Desktop/tests/Converted/" + formatName + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        File destinationFilePath = new File("/Users/omarenrique/Desktop/tests/CopyTests");

        List<String> cmd = new ArrayList<>(Arrays.asList(
                "cp", "-r",
                outputFolderToCopy.getAbsolutePath(), destinationFilePath.getAbsolutePath()
        ));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            process.waitFor();
            logger.info("Folder copied!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
