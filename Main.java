import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Game game;

        if (args.length == 2) {
            String address_name = args[0];
            int port_num = Integer.parseInt(args[1]);
            game = new Game(address_name, port_num);
        } else if (args.length == 1) {
            int port_num = Integer.parseInt(args[0]);
            game = new Game(port_num);
        } else if (args.length == 0) {
            game = new Game();
        } else {
            System.out.println("Error: Too many arguments");
            return;
        }

        // ゲーム開始
        try {
            game.start();
        } catch (IOException e) {
            System.out.println("Error: Lost connection");
        }
    }
}
