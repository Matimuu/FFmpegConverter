import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Long.sum;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/28 15:01
 */
public class Interface2 {
    static long startTime = System.nanoTime();

    private static final Scanner scanner = new Scanner(System.in);
    private static String formatName;
    private static File outputFolderToCopy;
    private static File destinationFilePath;
    private static Logger logger;
    private static File OBSFOLDER = new File("C:/Users/onAir/Videos/");

    public static void main(String[] args) {
        System.out.println("Destination format and folder:");
        System.out.println("1.LIVE 2.PREVIA 3.CHAMPIONS 4.SHOW");

        int index = scanner.nextInt();
        formatName = switch (index) {
            case 1 -> Format.LIVE.getName();
            case 2 -> Format.PREVIA.getName();
            case 3 -> Format.CHAMPIONS.getName();
            case 4 -> Format.SHOW.getName();
            case 9 -> Format.TEST.getName();
            default -> {
                throw new IllegalStateException();
            }
        };
        outputFolderToCopy = new File("D:/VAMOS/BACKUP/" + formatName + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        destinationFilePath = new File("L:/Shared drives/Vamos.Show (videos)/" + formatName + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        System.setProperty("log.path", outputFolderToCopy.getAbsolutePath());
        logger = LogManager.getLogger("mainLogger");
        logger.info("You choose: " + formatName);

        List<File> inputFolders = new ArrayList<>();
//        inputFolders.add(new File("C:/Users/onAir/Desktop/test/SAMPLE 1"));
         inputFolders.add(new File("N:/"));
         inputFolders.add(new File("M:/"));
         inputFolders.add(new File("O:/"));
         inputFolders.add(new File("P:/"));
         inputFolders.add(new File("G:/"));
        // inputFolders.add(new File("J:/"));
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
        Copy(outputFolderToCopy, destinationFilePath);

        logger.info(String.format("Process ended! it took: %.2f", ((System.nanoTime() - startTime) / 1000_000_000 / 60 / 60.)));
    }

    private static void Copy(File folderToCopy, File destinationFolder) {
        logger.info("Copying started.");

        try {
            // Create destination directory if it doesn't exist
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            // Iterate through files in the source directory and copy them to the destination directory
            Files.walk(folderToCopy.toPath())
                    .forEach(sourcePath -> {
                        Path destinationPath = Paths.get(destinationFolder.getAbsolutePath(), folderToCopy.toPath().relativize(sourcePath).toString());
                        try {
                            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                            logger.info("Folder copied successfully!");
                        } catch (IOException e) {
                            logger.error("Error copying file: " + e.getMessage());
                        }
                    });
            if (checkCopy(folderToCopy.toPath()) == checkCopy(destinationFolder.toPath())) {
                logger.info("Copied folders are the same.");
            } else {
                logger.info("Copied folders are not the same.");
                Copy(folderToCopy, destinationFolder);
            }
        } catch (IOException e) {
            logger.error("Error copying folders: " + e.getMessage());
        }
    }
    private static void CopyObsFile() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("");
    }

    private static long checkCopy(Path path) {
        long size = 0;
        try {
            size = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .mapToLong(f -> {
                        try{
                            return Files.size(f);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                        .sum();
        } catch (IOException e) {
            logger.error("Folder not exist");
            throw new RuntimeException(e);
        }
        return size/1_000_000;
    }
}