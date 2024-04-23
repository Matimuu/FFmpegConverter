import java.util.Scanner;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/23 12:17
 */
public class Interface {

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Process is started...");
        System.out.println("Destination format and folder:");
        System.out.println("1.LIVE 2.PREVIA 3.CHAMPIONS 4.SHOW");

        int index = scanner.nextInt();
        String formatName = switch(index) {
            case 1 -> Format.LIVE.getName();
            case 2 -> Format.PREVIA.getName();
            case 3 -> Format.CHAMPIONS.getName();
            case 4 -> Format.SHOW.getName();
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };


            System.out.println("enter name of volume:");
            String volume1 = "/Users/omarenrique/Desktop/tests/SAMPLE VIDEO";
            String volume2 = "/Users/omarenrique/Desktop/tests/Sample 2";

            Thread thread1 = new Thread(new Converter(formatName, volume1));
            thread1.start();

            Thread thread2 = new Thread(new Converter(formatName, volume2));
            thread2.start();

            thread1.join();
            thread2.join();
        System.out.println("it took "+(System.nanoTime()-startTime)/1000_000_000+" sec.");
    }
}
