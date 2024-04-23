import java.util.Scanner;

/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/23 12:17
 */
public class Interface {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime = System.nanoTime();

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

        Converter converter = new Converter(formatName);
        System.out.println("it took "+(System.nanoTime()-startTime)/1000_000_000+" sec.");
    }
}
