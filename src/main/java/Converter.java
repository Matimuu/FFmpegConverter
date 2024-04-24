import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/23 14:24
 */
public class Converter implements Runnable{
    private static volatile int index = 0;
    private String[] volumes = {"N:\\", "Q:\\", "M:\\", "P:\\", "O:\\", "J:\\"};
    private String OpathFolder = "D:\\VAMOS";
    private String codec = "libx265";
    private File outFolder;
    private String outputFormatFolder;

    private Pattern pattern = Pattern.compile("(.mov)|(.mp4)$");
    private Matcher matcher;

    private Date currentDate = new Date();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");

//    private String volume;
    //Constructor

    public Converter(String outputFormatFolder) {
        this.outputFormatFolder = outputFormatFolder;
    }

    private void SurfingThroughDirectory(String outputFormatFolder) {
        // Create a File object for the directory
        for (; index < 6; index++) {
            File directory = new File(volumes[index]);

            // Verify if the provided path is a directory
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();

                for (File file : files) {

                    matcher = pattern.matcher(file.getName());

                    //Check if its ends with .mov/.mp4
                    if (!matcher.find()) {
                        System.out.println("not a video");
                        continue;
                    }

                    outFolder = new File(OpathFolder+"/"+outputFormatFolder+"/"+dateFormat.format(currentDate)+"/C"+file.getName().substring(0,1));
                    outFolder.mkdirs();

                    Converting(file, file.getAbsolutePath());
                }
            } else {
                System.out.println("Provided path is not a directory.");
            }
        }
        } {


    }
    private void Converting(File file, String IpathFile) {
        try {
            FFmpeg ffmpeg = new FFmpeg("C:\\ffmpeg\\bin\\ffmpeg.exe");
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            String CNVRTDFileName = outFolder + "/" + file.getName().substring(0, file.getName().length() - 4) + "_" + codec + ".mov";

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(IpathFile)
                    .overrideOutputFiles(true)
                    .addOutput(CNVRTDFileName)
                    .setVideoCodec(codec)
                    .setFormat("mov")
                    .done();
            executor.createJob(builder).run();

            System.out.println("done! Checking...");
            Checking(file, new File(CNVRTDFileName));

        } catch (IOException e) {
            System.out.println("ffmpeg doesnt found");
            System.out.println("failed");
            e.printStackTrace();
        }
    }
    private void Checking(File ORGFile, File CNVRTDFile) {
        try {
            FFprobe fFprobe = new FFprobe("C:\\ffmpeg\\bin\\ffprobe.exe");

            FFmpegProbeResult ORGprobeResult = fFprobe.probe(ORGFile.getPath());
            FFmpegFormat ORGFormat = ORGprobeResult.getFormat();

            FFmpegProbeResult CNVRTDResult = fFprobe.probe(CNVRTDFile.getPath());
            FFmpegFormat CNVRTDFormat = CNVRTDResult.getFormat();

            if (Math.round(ORGFormat.duration) != Math.round(CNVRTDFormat.duration)){
                System.out.println("Original time: "+ORGFormat.duration);
                System.out.println("Converted time: "+CNVRTDFormat.duration);
                System.out.println("Another converting for this file: "+ORGFile.getName());
                Converting(ORGFile, ORGFile.getPath());
            } else {
                System.out.println("Everything is alright!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        SurfingThroughDirectory(outputFormatFolder);
    }
}
