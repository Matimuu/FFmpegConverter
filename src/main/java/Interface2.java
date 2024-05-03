import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            case 9 -> Format.TEST.getName();
            default -> {
                logger.error("Unexpected value: " + index);
                throw new IllegalStateException();
            }
        };
        logger.info("You choose: " + formatName);

        List<File> inputFolders = new ArrayList<>();
        inputFolders.add(new File("N:/"));
        inputFolders.add(new File("M:/"));
        inputFolders.add(new File("O:/"));
        inputFolders.add(new File("P:/"));
        inputFolders.add(new File("G:/"));
        inputFolders.add(new File("J:/"));
        inputFolders.add(new File("Q:/"));

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
        Copy();
        logger.info(String.format("Process ended! it took: %.2f min", ((System.nanoTime() - startTime) / 1000_000_000 / 60.0)));
    }

    private static void Copy() {
        logger.info("Copying started.");

        File outputFolderToCopy = new File("D:/VAMOS/BACKUP/" + formatName + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        File destinationFilePath = new File("L:/Shared drives/Vamos.Show (videos)/" + formatName + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        try {
            // Create destination directory if it doesn't exist
            if (!destinationFilePath.exists()) {
                destinationFilePath.mkdirs();
            }

            // Iterate through files in the source directory and copy them to the destination directory
            Files.walk(outputFolderToCopy.toPath())
                    .forEach(sourcePath -> {
                        Path destinationPath = Paths.get(destinationFilePath.getAbsolutePath(), outputFolderToCopy.toPath().relativize(sourcePath).toString());
                        try {
                            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            logger.error("Error copying file: " + e.getMessage());
                        }
                    });

            logger.info("Folder copied successfully!");
        } catch (IOException e) {
            logger.error("Error copying folder: " + e.getMessage());
        }
    }
}
