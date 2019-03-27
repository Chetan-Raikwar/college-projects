import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

class KeyPad extends JPanel {
    
    boolean capsState = false, autoShuffle = false, coverState = false;
    String resultString = "";
    String[] KeyMap = {"1234567890",
                       "qwertyuiopasdfghjklzxcvbnm",
                       "`~!@#$%^&*()_+-=\\|[]{}:;'\"<>,./?"
                      };
    JCheckBox shuffle, textCover;
    JPanel[] panels;
    JTextField resultField;
    JButton[] buttons;
    JButton backSpace, clear, copy, space, capsLock;
    GridBagConstraints GBC;
    
    public KeyPad() {
        
        /* switch to desired UI */
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        
        try {
            UIManager.setLookAndFeel(infos[1].getClassName());
        } 
        catch (Exception x) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Recommended UI support not found.\n"
                    + "Switching to default UI.");
        }
        
        setBackground(Color.GRAY);
        setLayout( new GridBagLayout() );
        GBC = new GridBagConstraints();
        
        makePanels();
        
        GBC.weightx = 100;
        GBC.weighty = 100;
        
        GBC.gridx = 0;
        GBC.gridy = 1;
        add( new JLabel(new ImageIcon("F:\\green.png")),GBC);
        
        // result field 
        GBC.gridx = 1;
        GBC.gridy = 1;
        GBC.gridwidth = 2;
       
        GBC.fill = GridBagConstraints.HORIZONTAL;
        add(panels[0], GBC);
        
        // numeric pad
        GBC.gridx = 0;
        GBC.gridy = 2;
        GBC.gridwidth = 1;
        GBC.fill = GridBagConstraints.NONE;
        add(panels[1], GBC);
        
        // alphabetic pad
        GBC.gridx = 1;
        GBC.gridy = 2;
        
        add(panels[2], GBC);
        
        // symbol pad
        GBC.gridx = 2;
        GBC.gridy = 2;
        add(panels[3], GBC);
        
        // special key pad
        GBC.gridx = 3;
        GBC.gridy = 2;
        add(panels[4], GBC);
        
       prepareBoard();
        
    }
    
    private void makePanels() {
        
       panels = new JPanel[5];
       
       for(int i=0; i<panels.length; i++) {
           
           panels[i] = new JPanel();
           panels[i].setBackground(Color.GRAY);
       }
       
       Border panelBorder = BorderFactory.createLoweredBevelBorder();
       
       Border titleBorder = BorderFactory.createTitledBorder(panelBorder, "Confidential Text");
       panels[0].setLayout(new GridLayout(1, 1));
       panels[0].setBorder(titleBorder);
       
       titleBorder = BorderFactory.createTitledBorder(panelBorder, "Numbers");
       panels[1].setLayout(new GridLayout(5, 2));
       panels[1].setBorder(titleBorder);
       
       titleBorder = BorderFactory.createTitledBorder(panelBorder, "Alphabets");
       panels[2].setLayout(new GridLayout(5, 6));
       panels[2].setBorder(titleBorder);
       
       titleBorder = BorderFactory.createTitledBorder(panelBorder, "Symbols");
       panels[3].setLayout(new GridLayout(5, 8));
       panels[3].setBorder(titleBorder);
       
       panels[4].setLayout(new GridLayout(7, 1));
       panels[4].setBorder(panelBorder);
    }
   
    private void prepareBoard()  {
      
       // text result field
     resultField = new JTextField(1000);
     resultField.setEditable(false);
     Font font = new Font("Sanserif", Font.PLAIN, 24);
     resultField.setFont(font);
   
     resultField.setSize(30, 50);
        panels[0].add(resultField);
        
        // text coverer
        
        textCover = new JCheckBox("Cover Text");
        
        textCover.addActionListener(new ActionListener() {
      
         public void actionPerformed(ActionEvent e) {
             
            if(textCover.isSelected())
                coverState = true;
            
            else 
                coverState = false;
            
            coverText();
         }       
        });
        
        // shuffle option
        shuffle = new JCheckBox("Auto shuffle");
        
        shuffle.addActionListener(new ActionListener() {
      
         public void actionPerformed(ActionEvent e) {
             
             if(shuffle.isSelected()) 
                 autoShuffle = true;
             
             else 
                 autoShuffle = false;
         }       
        });
        
        // prepare buttons
        buttons = new JButton[ (KeyMap[0].length()+KeyMap[1].length()+KeyMap[2].length()) ];
        
        int keyNumber = 0, panelNumber = 1;
        
        for(String Key : KeyMap) {
            
            for(int i=0; i<Key.length(); i++) {
 
                buttons[keyNumber] = new JButton( ""+Key.charAt(i) );

                buttons[keyNumber].addActionListener(new ActionListener(){
                    
                    public void actionPerformed(ActionEvent e) {

                        if(capsState)
                            resultString += e.getActionCommand().trim().toUpperCase();
                        
                        
                        else 
                            resultString += e.getActionCommand().trim().toLowerCase();
                           
                        coverText();
                        
                        if(autoShuffle)
                            shuffle();
                    }
                });
                panels[panelNumber].add(buttons[keyNumber]);
                
                keyNumber++;
            }
            panelNumber++;
        }
        
        // backspace key 
        backSpace = new JButton("Backspace");
        backSpace.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
                    
                if(resultString.length() > 0) {
                    
                    resultString = resultString.substring(0, resultString.length()-1);
                    coverText();
                }
            }
        });
        
        // clear key
        clear = new JButton("Clear");
        clear.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
  
               resultString = "";
               resultField.setText(resultString);
            }
        });
        
        // copy key 
        copy = new JButton("Copy");
        copy.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
                
               /* copying data in another thread to avoid program freeze */  
               new Thread( new Runnable() {
                               
                   public void run() {
                   
               /* copy data to system clipboard */
                StringSelection selection = new StringSelection(resultString);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                
                try {
                    Thread.sleep(10000); // wait for 10 seconds
                }
                catch(InterruptedException ex) {
                
                }
                
                /* remove system clipboard contents */
                selection = new StringSelection(""); // no contents 
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                Toolkit.getDefaultToolkit().beep(); /* signal for clipboard cleanup */
                 
                   }
               } ).start();
  
            }
        });
        
        //  space key
        space = new JButton("Space");
        space.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
                    
                resultString += " ";
                coverText();
            }
        });
        
        // capslock key
        capsLock = new JButton("Capslock");
        capsLock.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
                    
                if(!capsState) {
                    for(JButton key : buttons) {

                            String title = key.getText();
                            
                            if(Character.isLowerCase(title.charAt(0)))
                                key.setText(title.toUpperCase());        
                     }                
                   }
                
                else {
                for(JButton key : buttons) {

                            String title = key.getText();
                            
                            if(Character.isUpperCase(title.charAt(0)))
                                key.setText(title.toLowerCase());        
                     }
                }
                    capsState = !capsState;
                
            }
        });
       
        panels[4].add(textCover); 
        panels[4].add(shuffle);
        panels[4].add(capsLock);
        panels[4].add(backSpace);
        panels[4].add(space);
        panels[4].add(clear);
        panels[4].add(copy);
       
        
    }
    
    /* hides the text based on cover state */
    private void coverText() {
        
        if(coverState) {

            String starText = "";
            for(int i=0; i<resultString.length(); i++)
                starText += "*";
            
            resultField.setText(starText);
        }
        
        else 
            resultField.setText(resultString);
    }
    
    /* shuffles the keys in groups */
    private void shuffle() {
        
        ArrayList<Character> List = new ArrayList<>();
       
        for(int mainCount = 0; mainCount < KeyMap.length; mainCount++ ) {
             
            for(int i=0; i< KeyMap[mainCount].length(); i++) 
                List.add(KeyMap[mainCount].charAt(i));
            
            Collections.shuffle(List);
            
           String newMap = "";
            
            for(Character c : List)
                newMap += c;
            
            KeyMap[mainCount] = newMap;
            List.clear();
        }
        
        int keyNumber = 0;
        
        for(String Key : KeyMap) {         
            for(int i=0; i<Key.length(); i++)
                buttons[keyNumber++].setText( ""+Key.charAt(i) );
        
        }
    }
}

class PassBoard extends JFrame {
    
    public PassBoard(String title) {
        
        addWindowListener(new WindowAdapter(){
            
            public void windowClosing(WindowEvent e) {
                
                JOptionPane.showMessageDialog(null, "Thank you for using Passboard.\n\nMajor Project by:\n"
                        + "Name: Chetan Raikwar (0203IT131001)\n  From: Hitkarini College of Engineering & Technology, Jabalpur");
            }
        });
        
        add(new KeyPad());
        setIconImage(new ImageIcon("F:\\green.png").getImage());
        setTitle(title);
        setSize(900, 300);
        setLocationByPlatform(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

class Solution {
     
    public static void main(String[] args) {
       
        EventQueue.invokeLater(new Runnable(){
            
            public void run() {
                
                EventQueue.invokeLater(new Runnable() {
                    
                    public void run() {
                         
                        new PassBoard("PassBoard");
                    }
                });
               
            }
        });
        
    }
}