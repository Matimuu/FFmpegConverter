import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/23 14:24
 */
public class Converter {
    private String IpathFolder = "/Users/omarenrique/Desktop/tests/SAMPLE VIDEO";
    private String OpathFolder = "/Users/omarenrique/Library/CloudStorage/GoogleDrive-alekseich18@gmail.com/Shared drives/Vamos.Show (videos)";
    private String codec = "libx265";
    private Pattern pattern = Pattern.compile("(.mov)|(.mp4)$");

    private Matcher matcher;
    private Date currentDate = new Date();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
    private File outFolder;
    //Constructor

    public Converter(String outputFormatFolder) {
        //Using method
        SurfingThroughDirectory(outputFormatFolder);
    }

    private void SurfingThroughDirectory(String outputFormatFolder) {
        // Create a File object for the directory
        File directory = new File(IpathFolder);

        // Verify if the provided path is a directory
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            for (File file : files) {

                matcher = pattern.matcher(file.getName());

                //Check if its ends with .mov/.mp4
                if (!matcher.find()) {
                    System.out.print("not a video");
                    continue;
                }

                outFolder = new File(OpathFolder+"/"+outputFormatFolder+"/"+dateFormat.format(currentDate)+"/C"+file.getName().substring(0,1));
                outFolder.mkdirs();

                Converting(file.getName(), file.getAbsolutePath());
            }
        } else {
            System.out.println("Provided path is not a directory.");
        }
    }
    private void Converting(String fileName, String IpathFile) {
        try {
            FFmpeg ffmpeg = new FFmpeg("/opt/homebrew/Cellar/ffmpeg/6.1.1_7/bin/ffmpeg");
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(IpathFile)
                    .overrideOutputFiles(false)
                    .addOutput(outFolder+"/"+fileName.substring(0,fileName.length()-4)+"_"+codec+".mov")
                    .setVideoCodec(codec)
                    .done();
            executor.createJob(builder).run();

            System.out.println("done!");

        } catch (IOException e) {
            System.out.println("ffmpeg doesnt found");
            System.out.println("failed");
            e.printStackTrace();
        }
    }
}
