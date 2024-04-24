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

            Thread thread1 = new Thread(new Converter(formatName));
            thread1.start();
//            thread1.sleep(5000);

            Thread thread2 = new Thread(new Converter(formatName));
            thread2.start();


            thread1.join();
            thread2.join();
        System.out.println("it took "+(System.nanoTime()-startTime)/1000_000_000+" sec.");
    }
}
