import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.net.*;

import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game{
    private final Board board;
    private final List<Piece> pieces = new ArrayList<>();
    private static final int PIECE_NUM = 16;
    private boolean Quarto = false;
    private final String ADDRESS;
    private final int PORT;
    public String P1output;
    public String P2output;
    public String PlayerName;
    public String currentPiece;
    public String button_input;
    public boolean gamestart = false;
    public boolean gameend = false;

    Game(String address_name, int port_num) {
        board = new Board();
        for (int i = 0; i < PIECE_NUM; i++) {
            pieces.add(new Piece(i));
        }
        ADDRESS = address_name;
        PORT = port_num;
    }

    Game(int port_num) {
        this("localhost", port_num);
    }

    Game() {
        this("localhost", 8080);
    }

    public void start() throws IOException {
        int turn = 0;
        String selecting_player, putting_player;
        Socket socket;
        PlayerName = "";
        String input = "";

        try {
            InetAddress addr = InetAddress.getByName(ADDRESS);
            System.out.println("Searching server... (addr=" + addr + ", port=" + PORT + ")");
            socket = new Socket(addr, PORT);
            System.out.println("Server found.");
        } catch (IOException e) {
            System.out.println("Server not found. Launching server...");
            ServerSocket s = new ServerSocket(PORT);
            System.out.println("Started: " + s);
            socket = s.accept();

            Random random = new Random();
            PlayerName = "Player " + (random.nextInt(2) + 1);
        }

        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("socket=" + socket);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()
                            )
                    ), true
            );

            print();

            if (PlayerName.equals("")) {
                PlayerName = in.readLine();
            } else {
                String OpponentName = PlayerName.equals("Player 2") ? "Player 1" : "Player 2";
                out.println(OpponentName);
            }
            System.out.println("You are " + PlayerName);
            P1output=P2output="Quarto";
            printStartGUI();
            while(gamestart==false){
                try{Thread.sleep(200);}
                catch(InterruptedException e){}
            }
            System.out.println();

            while (!Quarto) {
                turn++;

                // 交代する
                // Player 1 は先にコマを置く人、Player 2 は先にコマを選ぶ人
                if (turn % 2 == 0) {
                    selecting_player = "Player 1";
                    putting_player = "Player 2";
                } else {
                    selecting_player = "Player 2";
                    putting_player = "Player 1";
                }

                // 次のコマを選択
                Piece next_piece = null;
                while (next_piece == null) {
                    // 入力を得る
                    setOutput(selecting_player, "Select Piece");
                    setOutput(putting_player, "Waiting...");
                    printGUI();
                    input = getInputWithConnection(PlayerName, selecting_player, scanner, in, "Next piece");
                    button_input=null;
                    // チェック
                    next_piece = checkSelectPieceInput(input);
                }
                // 相手にデータ送信
                if (PlayerName.equals(selecting_player)) out.println(input);
                currentPiece="./piece_"+Integer.parseInt(input,2)+".png";

                System.out.println();
                setOutput(selecting_player, "Waiting...");
                setOutput(putting_player, "Select Board Location");
                printGUI();
                // コマを置く
                boolean isPut = false;
                while (!isPut) {
                    // 入力を得る
                    input = getInputWithConnection(PlayerName, putting_player, scanner, in, "Put on (row column)");
                    button_input=null;
                    // チェック
                    isPut = checkPutPieceInput(input, next_piece);
                }
                // 相手にデータ送信
                if (PlayerName.equals(putting_player)) out.println(input);

                System.out.println();

                // 現在の盤面を表示
                print();
                setOutput(putting_player, "Push Button if Quarto");
                currentPiece = null;
                printGUI();
                System.out.println();


                // クアルト処理
                boolean isOK = false;
                while (!isOK) {
                    // 入力を得る
                    input = getInputWithConnection(PlayerName, putting_player, scanner, in, "Quarto or not (q/n)");
                    button_input=null;
                    // チェック
                    isOK = checkQuartoInput(input);
                }
                // 相手にデータ送信
                if (PlayerName.equals(putting_player)) out.println(input);
                if(Quarto){
                    printwinlossGUI(putting_player);
                    break;
                }
                System.out.println();

                // ピースを全て使い切った場合
                if (pieces.isEmpty()) {
                    System.out.println("All pieces are used! GAME OVER");
                    break;
                }
            }
            scanner.close();
        } finally {
            System.out.println("Closing connection...");
            socket.close();
        }
    }

    /**
     * 標準入力あるいは通信により入力を得る
     *
     * @param PlayerName      自分のプレイヤー名
     * @param InputtingPlayer 入力を行うプレイヤー名
     * @param scanner         標準入力バッファ
     * @param in              通信の受信バッファ
     * @param text            表示する文章
     * @return 得られた入力
     * @throws IOException 通信エラー
     */
    private String getInputWithConnection(String PlayerName,
                                          String InputtingPlayer,
                                          Scanner scanner,
                                          BufferedReader in,
                                          String text) throws IOException {
        String input;
        if (PlayerName.equals(InputtingPlayer)) {
            // 標準入力から入力を得る
            System.out.print("[" + InputtingPlayer + "] ");
            System.out.print(text + ": ");
            while(button_input==null){
                try{Thread.sleep(100);}
                catch(InterruptedException e){}
            }
            input = button_input;
        } else {
            // 通信により入力を得る
            //setOutput(PlayerName, "Waiting for opponent...");
            System.out.println("Waiting for opponent...");
            input = in.readLine();
            System.out.print("[" + InputtingPlayer + "] ");
            System.out.print(text + ": ");
            System.out.println(input);
        }
        return input;
    }

    /**
     * コマを選択する入力に関するチェック
     *
     * @param input 入力された文字列
     * @return 該当するコマ（存在しなかったらnull）
     */
    private Piece checkSelectPieceInput(String input) {
        if (!input.matches("[01][01][01][01]")) {
            System.out.println("Input 4 digit binary");
            return null;
        }

        // 変換
        byte next_byte = 0;
        for (int i = 0; i < input.length(); i++) {
            next_byte <<= 1;
            next_byte += Integer.parseInt(String.valueOf(input.charAt(i)));
        }

        // チェック
        Piece next_piece = new Piece(next_byte);
        if (pieces.remove(next_piece)) {
            System.out.println("Selected piece: " + next_piece.feature());
            return next_piece;
        } else if (next_byte >= 0b0000 && next_byte <= 0b1111) {
            System.out.println("This piece is already used");
            return null;
        } else {
            System.out.println("Please type any piece in remaining pieces");
            return null;
        }
    }

    /**
     * コマを設置する入力に関するチェック
     *
     * @param input      入力された文字列
     * @param next_piece 相手に選択されたコマ
     * @return コマを盤上に設置できたらtrue
     */
    private boolean checkPutPieceInput(String input, Piece next_piece) {
        if (!input.matches("[0-3] [0-3]")) {
            System.out.println("Illegal input. Make sure that 0<=row<4, 0<=column<4, and they are divided by space");
            return false;
        }
        int row = Integer.parseInt(String.valueOf(input.charAt(0)));
        int column = Integer.parseInt(String.valueOf(input.charAt(2)));
        if (board.isEmpty(row, column)) {
            board.put(next_piece, row, column);
            return true;
        } else {
            System.out.println("This square is occupied");
            return false;
        }
    }

    /**
     * クアルトを宣言する入力に関するチェック
     *
     * @param input 入力された文字列
     * @return 入力が"Quarto"か"not"のいずれかの場合true
     */
    private boolean checkQuartoInput(String input) {
        if (input.matches("^[qQ]")) {
            if (board.isAligned()) {
                Quarto = true;
                System.out.println("QUARTO!\nYOU WIN! GG :)");
            } else {
                System.out.println("Not QUARTO...");
            }
            return true;
        } else if (input.matches("^[nN]")) {
            return true;
        } else {
            System.out.println("Type q or n");
            return false;
        }
    }

    /**
     * 盤面を表示する関数
     */
    public void print() {
        Piece[][] spaces = board.getSpaces();

        System.out.println("--- Current Board ---");
        for (int i = 0; i < spaces.length; i++) {
            if (i != 0) System.out.println("----+----+----+----");
            for (int j = 0; j < spaces[i].length; j++) {
                if (j != 0) System.out.print("|");
                if (spaces[i][j] != null) {
                    System.out.print(spaces[i][j]);
                } else {
                    System.out.print("    ");
                }
            }
            System.out.println();
        }
        System.out.println("---------------------");

        System.out.println("--- Remaining Pieces ---");
        for (Piece piece : pieces) {
            System.out.print(piece);
            System.out.print(", ");
        }
        System.out.println();
        System.out.println("---------------------");
    }

    public void printStartGUI(){
        JFrame frame = new JFrame();
        frame.setTitle(PlayerName);
        if(PlayerName.equals("Player 1"))frame.setBounds(900, 0, 1000, 500);
        else frame.setBounds(900, 520, 1000, 500);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        JPanel panel = new JPanel();
        JButton startButton = new JButton(new ImageIcon("./Start.png"));
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setPreferredSize(new Dimension(800, 400));
        startButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                gamestart=true;
              }
        });
        panel.add(startButton);
        Container contentPane = frame.getContentPane();
        contentPane.add(panel);
        frame.setVisible(true);
    }

    public void printwinlossGUI(String p){
        JFrame frame = new JFrame();
        frame.setTitle(PlayerName);
        if(PlayerName.equals("Player 1"))frame.setBounds(900, 0, 1000, 500);
        else frame.setBounds(900, 520, 1000, 500);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        JPanel panel = new JPanel();
        ImageIcon icon;
        if(PlayerName.equals(p))icon = new ImageIcon("./WIN.png");
        else icon = new ImageIcon("./LOSE.png");
        JLabel label = new JLabel(icon);
        panel.add(label);
        Container contentPane = frame.getContentPane();
        contentPane.add(panel);
        frame.setVisible(true);
    }

    public void printGUI(){
        deleteframes();
        JFrame frame = new JFrame();
        frame.setTitle(PlayerName);
        if(PlayerName.equals("Player 1"))frame.setBounds(900, 0, 1000, 500);
        else frame.setBounds(900, 520, 1000, 500);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Piece[][] spaces = board.getSpaces();
        JPanel boardPanel = new JPanel();
        JButton[] board_buttons = new JButton[16];
        for(int i = 0; i<16; i++){
            if(board.isEmpty(i/4, i%4)){
              ImageIcon defaultboard = new ImageIcon("./boardcircle.png");
              ImageIcon board1_over = new ImageIcon("./boardcircle_over.png");
              board_buttons[i] = new JButton(defaultboard);
              board_buttons[i].setRolloverIcon(board1_over);
            }
            else{
                ImageIcon boardimage = new ImageIcon("./"+spaces[i/4][i%4].toString()+"_placed.png");
                board_buttons[i] = new JButton(boardimage);
            }
            board_buttons[i].setContentAreaFilled(false);
            board_buttons[i].setBorderPainted(false);
            board_buttons[i].setPreferredSize(new Dimension(100, 100));
            boardPanel.add(board_buttons[i]);
            int temp1=i/4,temp2=i%4;
            board_buttons[i].addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    System.out.println(temp1+" "+temp2);
                    button_input=temp1+" "+temp2;
                  }
            });
          }
          boardPanel.setBackground(Color.black);
          GridLayout BoardLayout = new GridLayout(4,4);
          boardPanel.setLayout(BoardLayout);

          JPanel master = new JPanel();
          JButton QuartoButton_q = new JButton("QUARTO!");
          QuartoButton_q.setPreferredSize(new Dimension(100, 40));
          QuartoButton_q.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.out.println("q");
                button_input="q";
              }
        });
        JButton QuartoButton_n = new JButton("NOT QUARTO");
          QuartoButton_n.setPreferredSize(new Dimension(120, 40));
          QuartoButton_n.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.out.println("n");
                button_input="n";
              }
        });
          JLabel mastertext = new JLabel();
          mastertext.setFont(new Font("Arial", Font.PLAIN, 16));
          if(PlayerName.equals("Player 1"))mastertext.setText(P1output);
          else mastertext.setText(P2output);
          if(currentPiece!=null)mastertext.setIcon(new ImageIcon(currentPiece));
          master.add(mastertext);
          master.add(QuartoButton_q);
          master.add(QuartoButton_n);

          JPanel pieceTable = new JPanel();
          JButton[] piece_buttons = new JButton[16];
          //--------------------------------------------------------------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!
          for(int i = 0; i<16; i++){
            ImageIcon pieceimage = new ImageIcon("./piece_"+i+".png");
            if(pieces.contains(new Piece(i))){
                piece_buttons[i] = new JButton(pieceimage);
            }
            else{
                piece_buttons[i] = new JButton();
            }
            //piece_buttons[i].setContentAreaFilled(false);
            piece_buttons[i].setPreferredSize(new Dimension(80, 80));
            pieceTable.add(piece_buttons[i]);
            int temp = i;
            piece_buttons[i].addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    System.out.println(new Piece(temp).toString());
                    button_input = new Piece(temp).toString();
                    piece_buttons[temp].setBackground(Color.GREEN);
                    //currentPiece="./piece_"+temp+".png";
                  }
            });
          }
          GridLayout ButtonLayout = new GridLayout(4,4);
          pieceTable.setLayout(ButtonLayout);

          Container contentPane = frame.getContentPane();
          contentPane.add(boardPanel);
          contentPane.add(master);
          contentPane.add(pieceTable);

          //game layout
          GridBagLayout GameLayout = new GridBagLayout();
          contentPane.setLayout(GameLayout);
          GridBagConstraints gbc = new GridBagConstraints();
          gbc.gridx = 0;
          gbc.gridy = 0;
          gbc.gridheight = 2;
          gbc.weightx = 1.0d;
          gbc.weighty = 1.0d;
          GameLayout.setConstraints(boardPanel, gbc);
          gbc.gridx = 1;
          gbc.gridy = 0;
          gbc.gridheight = 1;
          gbc.weightx = 1.0d;
          gbc.weighty = 1.0d;
          GameLayout.setConstraints(master, gbc);
          gbc.gridx = 1;
          gbc.gridy = 1;
          gbc.weightx = 1.0d;
          gbc.weighty = 1.0d;
          GameLayout.setConstraints(pieceTable, gbc);
          frame.setVisible(true);
    }

    public void deleteframes(){
        Frame[] frames = JFrame.getFrames();
        for(Frame frame : frames){
            //System.out.println(frame.getName() + ": " + frame.getClass());
            frame.dispose();
        }
    }

    public void setOutput(String player,String text){
        if(player.equals("Player 1"))P1output=text;
        else P2output=text;
    }
}
