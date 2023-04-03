
// import java.util.ArrayList;
// import java.util.List;

// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JButton;
// import javax.swing.JPanel;
// import javax.swing.BoxLayout;
// import javax.swing.ImageIcon;
// import javax.swing.JTextField;
// import java.awt.Container;
// import java.awt.GridLayout;
// import java.awt.Color;
// import java.awt.Dimension;
// import java.awt.GridBagLayout;
// import java.awt.GridBagConstraints;
// import java.awt.Insets;

// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;

// class GUI extends JFrame implements ActionListener{
//   Board board;
//   String text;
//   List<Piece> pieceList;
//   Game game;

//   public GUI(Game thegame){
//     game = thegame;
//     Draw();
//   }
  
  // public void actionPerformed(ActionEvent e) {
  //   String cmd = e.getActionCommand();
  //   for(int i = 0; i<16; i++){
  //     if(cmd.equals("boardButton"+i)){
  //       System.out.println("boardbutton"+i+"pushed!");
      
  //     }
  //   }
  // }
  
//   JPanel boardPanel = new JPanel();
//   JButton[] board_buttons = new JButton[16];
//   public void UpdateBoard(Board board){
//     //盤面の状態を受け取ってボードを更新
//     for(int i = 0; i<16; i++){
//       if(board.isEmpty(i/4, i%4)){
//         ImageIcon defaultboard = new ImageIcon("./boardcircle.png");
//         ImageIcon board1_over = new ImageIcon("./boardcircle_over.png");
//         board_buttons[i] = new JButton(defaultboard);
//         board_buttons[i].setContentAreaFilled(false);
//         board_buttons[i].setBorderPainted(false);
//         board_buttons[i].setPreferredSize(new Dimension(100, 100));
//         board_buttons[i].setRolloverIcon(board1_over);
//         board_buttons[i].addActionListener(this);
//         board_buttons[i].setActionCommand("boardButton"+i);
//       }
//       else{
//         ImageIcon boardimage = new ImageIcon("./"+board.getSpaces()[i/4][i%4].feature()+"_placed.png");
//         board_buttons[i] = new JButton(boardimage);
//         board_buttons[i].setContentAreaFilled(false);
//         board_buttons[i].setBorderPainted(false);
//         board_buttons[i].setPreferredSize(new Dimension(100, 100));
//         board_buttons[i].addActionListener(this);
//         board_buttons[i].setActionCommand("boardButton"+i);
//       }
//       boardPanel.add(board_buttons[i]);
//     }
//     boardPanel.setBackground(Color.black);
//     GridLayout BoardLayout = new GridLayout(4,4);
//     boardPanel.setLayout(BoardLayout);
//   }
  
//   JPanel pieceTable = new JPanel();
//   JButton[] piece_buttons = new JButton[16];
//   public void UpdatePieces(List<Piece> pieceList){
//     //残りコマのリストを受け取ってコマのテーブルを更新
//     for(int i = 0; i<16; i++){
//       ImageIcon pieceimage = new ImageIcon("./piece_"+i+".png");
//       piece_buttons[i] = new JButton(pieceimage);
//       piece_buttons[i].setContentAreaFilled(false);
//       piece_buttons[i].setPreferredSize(new Dimension(80, 80));
//       pieceTable.add(piece_buttons[i]);
//     }
//     GridLayout ButtonLayout = new GridLayout(4,4);
//     pieceTable.setLayout(ButtonLayout);
//   }

//   JPanel master = new JPanel();
//   public void UpdateMaster(String text){
//     JLabel mastertext = new JLabel();
//     mastertext.setText(text);
//     master.add(mastertext);
//   }
  
//   public void Draw(){
//     board = game.board;
//     //text = game.output;
//     pieceList = game.pieces;
    
//     setTitle("Quarto");
//     setBounds(0, 0, 1000, 500);
//     setResizable(false);
//     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
//     UpdateBoard(board);
//     UpdateMaster(text);
//     UpdatePieces(pieceList);
    
//     Container contentPane = getContentPane();
//     contentPane.add(boardPanel);
//     contentPane.add(master);
//     contentPane.add(pieceTable);
    
//     //game layout
//     GridBagLayout GameLayout = new GridBagLayout();
//     contentPane.setLayout(GameLayout);
//     GridBagConstraints gbc = new GridBagConstraints();
//     gbc.gridx = 0;
//     gbc.gridy = 0;
//     gbc.gridheight = 2;
//     gbc.weightx = 1.0d;
//     gbc.weighty = 1.0d;
//     GameLayout.setConstraints(boardPanel, gbc);
//     gbc.gridx = 1;
//     gbc.gridy = 0;
//     gbc.gridheight = 1;
//     gbc.weightx = 1.0d;
//     gbc.weighty = 1.0d;
//     GameLayout.setConstraints(master, gbc);
//     gbc.gridx = 1;
//     gbc.gridy = 1;
//     gbc.weightx = 1.0d;
//     gbc.weighty = 1.0d;
//     GameLayout.setConstraints(pieceTable, gbc);
//     System.out.println("repainted!");
//   }
  
// }