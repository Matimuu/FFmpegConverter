import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/27 16:53
 */
public class Converter2 implements Runnable {
    private final Logger logger = LogManager.getLogger("mainLogger");
    private Date currentDate = new Date();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private File inputFolder;
    private File inputFile;
    private String outputFile;
    private File outputFolder = new File("/D:/VAMOS/BACKUP/");
    private File outputFolderToCopy;
        private String convertingFormat = "hevc_nvenc";
//    private String convertingFormat = "libx265";

    public Converter2(File inputFolder, String showFormatName) {
        this.inputFolder = inputFolder;
        this.outputFolder = new File(outputFolder.getAbsolutePath() + "/" + showFormatName + "/" + simpleDateFormat.format(currentDate));
        outputFolderToCopy = outputFolder;
    }

    public void ScrollingThroughFiles() {
        try {
            assert inputFolder.isDirectory();
            assert inputFolder != null;
        } catch (AssertionError e) {
            logger.error("This is not a folder or folder is empty.");
        }
        logger.info("Opening folder " + inputFolder);
        File[] filesFromFolder = inputFolder.listFiles((dir, name) -> name.endsWith(".mov") || name.endsWith(".mp4"));
        try {
            assert filesFromFolder != null;
        } catch (AssertionError e) {
            logger.error("Folder is empty");
        }
        Arrays.stream(filesFromFolder).forEach(file -> {
            inputFile = file.getAbsoluteFile();
            File outputPath = new File(outputFolder.getAbsolutePath() + "/C" + inputFile.getName().substring(0, 1));

            outputPath.mkdirs();

            logger.info(outputPath.getAbsolutePath() + ": folder was added");

            outputFile = outputPath.getAbsolutePath() + File.separator + inputFile.getName().substring(0, inputFile.getName().length() - 4) + "_libx265.mov";

            ffmpegCmd(file);
        });
    }

    private void ffmpegCmd(File f) {
        List<String> cmd = new ArrayList<>(Arrays.asList(
                "ffmpeg",
                "-hwaccel", "auto",
                "-hide_banner",
                "-loglevel", "error",
                "-i", f.getAbsolutePath(),
                "-c:v", convertingFormat,
                "-b:v", "12500k",
                "-profile:v", "main",
                "-level", "6.1",
                "-map", "v:0",
                "-c:a", "aac",
                "-ar", "48k",
                "-b:a", "320k",
                "-map", "a:0",
                "-pix_fmt", "yuv420p",
                "-sws_flags", "bicubic",
                "-tag:v", "hvc1",
                "-metadata", "creation_time=\"" + simpleDateFormat.format(currentDate) + "\"",
                "-y", outputFile
        ));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true); // Redirect error stream to output stream
        try {
            long startTime = System.nanoTime();

            Process process = pb.start();
            logger.info("Converting started.");

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info(String.format("Done, it took: %.2f", (System.nanoTime() - startTime) / 1000_000_000 / 60 / 60.));
                Checking(f, new File(outputFile));
            }
        } catch (IOException e) {
            logger.error("Cannot run ffmpeg command: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void Checking(File ORGFile, File CNVRTDFile) {
        try {
            logger.info("Checking for errors...");
            FFprobe fFprobe = new FFprobe("C:\\ffmpeg\\bin\\ffprobe.exe");

            FFmpegProbeResult ORGprobeResult = fFprobe.probe(ORGFile.getPath());
            FFmpegFormat ORGFormat = ORGprobeResult.getFormat();

            FFmpegProbeResult CNVRTDResult = fFprobe.probe(CNVRTDFile.getPath());
            FFmpegFormat CNVRTDFormat = CNVRTDResult.getFormat();

            if (Math.round(ORGFormat.duration) != Math.round(CNVRTDFormat.duration)) {
                logger.info("Original time: " + ORGFormat.duration);
                logger.info("Converted time: " + CNVRTDFormat.duration);
                logger.info("Another converting for this file: " + ORGFile.getName());
                ffmpegCmd(ORGFile);
            } else {
                logger.info("Everything is alright!\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        ScrollingThroughFiles();
    }
}
