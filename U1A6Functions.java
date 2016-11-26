/**
 * Created by Shane Bishop
 * sometime in January 2015
 * originally as an assignment (11 culminating) for Mr. Grasley
 * and now as an assignment (12 U1A6) for Mr. Koivisto
 * to produce a simple playable card game.
 * 
 * The user always plays first.
 */
package u1a6functions;

import java.awt.event.*;
import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class U1A6Functions extends JFrame {
    Random r; // Declare a Random object called r
    
    // Create new JLabels
    JLabel objectOfGameLabel = new JLabel(); // Create new JLabel objectOfGameLabel
    JLabel playingACardLabel = new JLabel(); // Create new JLabel playingACardLabel
    JLabel heading1Label = new JLabel();     // And so on ...
    JLabel instruction1Label = new JLabel();
    JLabel instruction2Label = new JLabel();
    JLabel playPos1Label = new JLabel();
    JLabel playPos2Label = new JLabel();
    JLabel playPos3Label = new JLabel();
    JLabel playPos4Label = new JLabel();
    JLabel playPos5Label = new JLabel();
    JLabel deckLabel = new JLabel();
    
    JScrollPane messagePane = new JScrollPane(); // Creates new JScrollPane messagePane
    JTextArea messageArea = new JTextArea();     // Creates new JTextArea messageArea
    JPanel handPanel = new JPanel();             // Creates new JPanel handPanel
    
    private Card[][] allCards = new Card[4][9]; // Create a 2D Card array
    
    // Set up fields related to the deck
    private Card[] deck = new Card[36]; // Create a Card array for the deck
    private int deckSize;               // Hold the number of cards in the deck
    
    // Set up fields related to the player's hand
    private Card[] playerHand = new Card[12]; // Create a Card array for the player
    private int playerHandSize;               // Hold the number of cards in the player's hand
    
    // Set up fields related to the AI's hand
    private Card[] cpuHand = new Card[12];    // Create a Card array for the AI
    private int cpuHandSize;                  // Hold the number of cards in the AI's hand
    
    private Card activeCard; // Store the active Card
    private Card[] playPiles = new Card[5];
    private JLabel activeCardLabel = null;
    private JLabel playPositionLabel = null;
    private int deckLabelClickCounter = 0;     
    private JLabel[] handPosLabels; // Create a JLabel array for the player's hand
    private JLabel[] playPosLabels; // Create a JLabel array for the positions of play
    
    // I'm not sure if I need this, but I might need to be able to reference 
    // where a Card is played to properly maintain the playPiles array for the 
    // cpuTurn method
    private int activePosition;
    
    /**
     * The entry main() method
     */
    public static void main(String[] args) {
        // Run GUI codes on the Event-Dispatcher Thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                U1A6Functions U1A6Functions = new U1A6Functions(); // Let the constructor do the job
            }
        });
    }
    
    /**
     * Create new form U1A6Functions
     */
    public U1A6Functions () {
        handPosLabels = new JLabel[12]; // Initialize handPosLabels
        playPosLabels = new JLabel[5];  // Initialize playPosLabels
        
        playPosLabels[0] = playPos1Label; // Initialize playPosLabels[0] as playPos1Label
        playPosLabels[1] = playPos2Label; // Initialize playPosLabels[1] as playPos2Label
        playPosLabels[2] = playPos3Label; // Initialize playPosLabels[2] as playPos3Label
        playPosLabels[3] = playPos4Label; // And so on ...
        playPosLabels[4] = playPos5Label;
        
        // Set up a new MouseAdapter
        MouseAdapter m = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // This set of if statements sends the mouseClickedEvent evt to 
                // the correct MouseClickedEvent method according to its source
                // ** some or all of these throw NullPointerExceptions
                if (evt.getSource().equals(playPosLabels[0])) 
                {playPos1LabelMouseClicked(evt);
              
                }
                else if (evt.getSource().equals(playPosLabels[1])){ playPos2LabelMouseClicked(evt);}
                else if (evt.getSource().equals(playPosLabels[2])) {playPos3LabelMouseClicked(evt);}
                else if (evt.getSource().equals(playPosLabels[3])) {playPos4LabelMouseClicked(evt);}
                else if (evt.getSource().equals(playPosLabels[4])){ playPos5LabelMouseClicked(evt);}
                else if (evt.getSource().equals(deckLabel)) {deckLabelMouseClicked(evt);;}
                else {handPosMouseClicked(evt);System.out.println("player card clicked");
                        messageArea.append("\nplayer card clicked");;}
            }
        };
        
        // Adds a mouse listener to each element of playPosLabels
        for (int int_i = 0; int_i < playPosLabels.length; int_i++) {
            playPosLabels[int_i].addMouseListener(m);
        }
        
        deckLabel.addMouseListener(m); // Adds a mouse listener event to deckLabel
        
        // Set up JLabels for the player's hand
        for (int i = 0; i < 12; i++) {
            handPosLabels[i] = new JLabel();      // Create a JLabel for each Card in the player's hand
            handPosLabels[i].addMouseListener(m); // Give the JLabel a MouseListener
            handPanel.add(handPosLabels[i]);      // Add the JLabel to handPanel
        }
        
        r = new Random(); // Initialize the Random r
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle the CLOSE button
        setTitle("Making 10"); // Set the title of the JFrame to "Making 10"

        // Set the text of the JLabels
        objectOfGameLabel.setText("Object of Game: Be the first to play all the "
                + "cards in your hand.");
        playingACardLabel.setText("To play a card, it and the card it is to be "
                + "played on should add to 10. To draw more cards, click on the image of a facedown card.");
        heading1Label.setText("During a player's turn:");
        instruction1Label.setText("-Player plays a single card by clicking on "
                + "the card they want to play and then clicking on the card they want to play it on");
        instruction2Label.setText("-If they cannot play, they draw two cards and "
                + "DO NOT play (the maximum number of cards that can be held at a time is 12)");

        // Set up messageArea
        messageArea.setEditable(false);
        messageArea.setColumns(20);
        messageArea.setRows(5);
        messageArea.setText("Click on the card you want to play.");
        messagePane.setViewportView(messageArea);
        
        // Set up handPanel
        handPanel.setMaximumSize(new Dimension(1000, 96));    // Set maximum size to 1000 by 96
        handPanel.setMinimumSize(new Dimension(1000, 96));    // Set minimum size to 1000 by 96
        handPanel.setPreferredSize(new Dimension(1000, 96));  // Set preferred size to 1000 by 96
        handPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout to FlowLayout.LEFT

        // Perform magic
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playPos1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playPos2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playPos3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playPos4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playPos5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deckLabel))
                            .addComponent(messagePane, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(handPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(instruction2Label)
                                    .addComponent(objectOfGameLabel)
                                    .addComponent(playingACardLabel)
                                    .addComponent(heading1Label)
                                    .addComponent(instruction1Label))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {deckLabel, playPos1Label});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(objectOfGameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playingACardLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(heading1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instruction1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instruction2Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playPos1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playPos2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playPos4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playPos5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playPos3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deckLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(handPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(messagePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deckLabel, playPos1Label});

        pack();
        
        setVisible(true); // Show the JFrame
        requestFocus();   // Set the focus to the JFrame to receive user input
        
        // ---------------------------------------------------------------------
        // The following bunch of code used to belong to the method private void
        // setupGame()
        
        // Set up deckLabel
        java.net.URL backImageURL = U1A6Functions.class.getResource("back/back.png"); // Gets the resource back.png
        deckLabel.setIcon(new ImageIcon(backImageURL)); // Sets deckLabel to icon back.png
        
        // Fill allCards array and deck array
        for (int suit = 0; suit < 4; suit++) {
            for (int value = 0; value < 9; value++) {
                allCards[suit][value] = new u1a6functions.Card(suit + 1, value + 1);
                deck[suit * 9 + value] = allCards[suit][value];
               // System.out.println(deck[suit * 9 + value]);
            }
        }
        deckSize = 36; // Since deck array has been filled, deckSize is 36
        
        // Display ace through 5 of random suit (remove from deck)
        int suitInt1 = r.nextInt(4);
        u1a6functions.Card startingAce = null;
        switch (suitInt1) {
            case 0: startingAce = deck[0];  //ace of clubs
                break;
            case 1: startingAce = deck[9];  //ace of diamonds
                break;
            case 2: startingAce = deck[18]; //ace of hearts
                break;
            case 3: startingAce = deck[27]; //ace of spades
                break;
        }
        playPiles[0] = startingAce;
        playPos1Label.setIcon(startingAce);
        
        
        int suitInt2 = r.nextInt(4);
        u1a6functions.Card starting2 = null;
        switch (suitInt2) {
            case 0: starting2 = deck[1];  //2 of clubs
                break;
            case 1: starting2 = deck[10]; //2 of diamonds
                break;
            case 2: starting2 = deck[19]; //2 of hearts
                break;
            case 3: starting2 = deck[28]; //2 of spades
                break;
        }
        playPiles[1] = starting2;
        playPos2Label.setIcon(starting2);
        
        
        int suitInt3 = r.nextInt(4);
        u1a6functions.Card starting3 = null;
        switch (suitInt3) {
            case 0: starting3 = deck[2];  //3 of clubs
                break;
            case 1: starting3 = deck[11]; //3 of diamonds
                break;
            case 2: starting3 = deck[20]; //3 of hearts
                break;
            case 3: starting3 = deck[29]; //3 of spades
                break;
        }
        playPiles[2] = starting3;
        playPos3Label.setIcon(starting3);
        
        
        int suitInt4 = r.nextInt(4);
        u1a6functions.Card starting4 = null;
        switch (suitInt4) {
            case 0: starting4 = deck[3];  //4 of clubs
                break;
            case 1: starting4 = deck[12]; //4 of diamonds
                break;
            case 2: starting4 = deck[21]; //4 of hearts
                break;
            case 3: starting4 = deck[30]; //4 of spades
                break;
        }
        playPiles[3] = starting4;
        playPos4Label.setIcon(starting4);
        
        
        int suitInt5 = r.nextInt(4);
        u1a6functions.Card starting5 = null;
        switch (suitInt5) {
            case 0: starting5 = deck[3];  //5 of clubs
                break;
            case 1: starting5 = deck[13]; //5 of diamonds
                break;
            case 2: starting5 = deck[22]; //5 of hearts
                break;
            case 3: starting5 = deck[31]; //5 of spades
                break;
        }
        playPiles[4] = starting5;
        playPos5Label.setIcon(starting5);
        
        //once all starting cards have been selected, they are removed
        removeCardFromDeck(startingAce);
        removeCardFromDeck(starting2);
        removeCardFromDeck(starting3);
        removeCardFromDeck(starting4);
        removeCardFromDeck(starting5);
        deckSize = 31; // With 5 cards removed, deckSize is now 31
        
        // ---------------------------------------------------------------------
        // The following 7 lines of code used to belong to the method private 
        // void shuffleDeck()
        int swapIndex;
        u1a6functions.Card temp;
        for (int startIndex = 0; startIndex < deckSize; startIndex++) {
            swapIndex = r.nextInt(deckSize);
            temp = deck[swapIndex];
            deck[swapIndex] = deck[startIndex];
            deck[startIndex] = temp;
        }
        // End of old shuffleDeck() --------------------------------------------
        
        // Deal 7 cards to player and cpu in alternating order (remove cards)
        playerHand[0] = deck[30];                // Deal the 31st card to the player
        handPosLabels[0].setIcon(playerHand[0]); // Display the player's first card at handPosLabels[0]
        removeCardFromDeck(deck[30]);            // Remove the 31st card from the deck
        cpuHand[0] = deck[29];                   // Deal the 30th card to the AI
        removeCardFromDeck(deck[29]);            // Remove the 30th card from the deck
        playerHand[1] = deck[28];                // Proceeds in the same manner
        handPosLabels[1].setIcon(playerHand[1]);
        removeCardFromDeck(deck[28]);
        cpuHand[1] = deck[27];
        removeCardFromDeck(deck[27]);
        playerHand[2] = deck[26];
        handPosLabels[2].setIcon(playerHand[2]);
        removeCardFromDeck(deck[26]);
        cpuHand[2] = deck[25];
        removeCardFromDeck(deck[25]);
        playerHand[3] = deck[24];
        handPosLabels[3].setIcon(playerHand[3]);
        removeCardFromDeck(deck[24]);
        cpuHand[3] = deck[23];
        removeCardFromDeck(deck[23]);
        playerHand[4] = deck[22];
        handPosLabels[4].setIcon(playerHand[4]);
        removeCardFromDeck(deck[22]);
        cpuHand[4] = deck[21];
        removeCardFromDeck(deck[21]);
        playerHand[5] = deck[20];
        handPosLabels[5].setIcon(playerHand[5]);
        removeCardFromDeck(deck[20]);
        cpuHand[5] = deck[19];
        removeCardFromDeck(deck[19]);
        playerHand[6] = deck[18];
        handPosLabels[6].setIcon(playerHand[6]);
        removeCardFromDeck(deck[18]);
        cpuHand[6] = deck[17];
        removeCardFromDeck(deck[17]);
        
        playerHandSize = 7; // The player now holds 7 cards
        cpuHandSize = 7;    // The AI now holds 7 cards
        
        deckSize = 17; // Since 7 more cards have been removed, deckSize is 17
         messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
        this.validate();
        this.repaint();
        
        // End of old setupGame ------------------------------------------------
    }
    
    /**
     * Handle the user clicking on cards in their hand
     * 
     * @param evt 
     */
    private void handPosMouseClicked(MouseEvent evt) {
        if (playerHandSize <= 0) return; // If the user has played all of their cards, the game is over
        if (cpuHandSize <= 0) return;    // If the AI has played all of their cards, the game is over
        
        JLabel handPosLabel = (JLabel) evt.getSource(); // Get reference to the handLabel
        activeCard = (Card) handPosLabel.getIcon();     // Determine which card has been clicked on
    }
    
    /**
     * Deal cards to the user
     * 
     * @param evt 
     */
    private void deckLabelMouseClicked(MouseEvent evt) {
        if (playerHandSize == 12) {
            deckLabelClickCounter++;
            if (deckLabelClickCounter == 2) {
                // If the player is holding the maximum number of cards, and has
                // clicked deckLabel twice, execute the AI's turn and set 
                // deckLabelClickCounter to 0
                deckLabelClickCounter = 0;
                cpuTurn();
            }
            else {
                // If the user has only clicked deckLabel once and is holding 12 cards
                messageArea.append("\nSorry, you are already holding the maximum "
                        + "number of cards.\nIf you would like to end your turn, "
                        + "click on the image of the facedown card again.");
            }
        }
        else if (playerHandSize == 11 && deckSize>1) {
            playerHand[playerHandSize] = deck[deckSize-1]; // Give the user a card from the top of the deck
            removeCardFromDeck(deck[deckSize-1]);          // Remove the top card from the deck
            playerHandSize++;                              // The user now has an additional card
            
            handPosLabels[0].setIcon(playerHand[0]); // Set handPosLabels[0] to diplay the icon for playerHand[0]
            handPosLabels[1].setIcon(playerHand[1]); // Set handPosLabels[1] to display the icon for playerHand[1]
            handPosLabels[2].setIcon(playerHand[2]); // Set handPosLabels[2] to display the icon for playerHand[2]
            handPosLabels[3].setIcon(playerHand[3]); // And so on ...
            handPosLabels[4].setIcon(playerHand[4]);
            handPosLabels[5].setIcon(playerHand[5]);
            handPosLabels[6].setIcon(playerHand[6]);
            handPosLabels[7].setIcon(playerHand[7]);
            handPosLabels[8].setIcon(playerHand[8]);
            handPosLabels[9].setIcon(playerHand[9]);
            handPosLabels[10].setIcon(playerHand[10]);
            handPosLabels[11].setIcon(playerHand[11]);
                
           messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
            cpuTurn();                                     // It is now the AI's turn
        }
        else if (playerHandSize <= 10 && deckSize>=2)  { // Should only execute if playerHandSize <= 10
            playerHand[playerHandSize] = deck[deckSize - 1]; // Give the user a card from the top of the deck
            removeCardFromDeck(deck[deckSize - 1]);          // Remove the top card from the deck
            playerHandSize++;                              // The user now has an additional card
            playerHand[playerHandSize] = deck[deckSize - 1]; // Repeate the previous three lines with the new values
            removeCardFromDeck(deck[deckSize - 1]);
            playerHandSize++;
            
            handPosLabels[0].setIcon(playerHand[0]); // Set handPosLabels[0] to diplay the icon for playerHand[0]
            handPosLabels[1].setIcon(playerHand[1]); // Set handPosLabels[1] to display the icon for playerHand[1]
            handPosLabels[2].setIcon(playerHand[2]); // Set handPosLabels[2] to display the icon for playerHand[2]
            handPosLabels[3].setIcon(playerHand[3]); // And so on ...
            handPosLabels[4].setIcon(playerHand[4]);
            handPosLabels[5].setIcon(playerHand[5]);
            handPosLabels[6].setIcon(playerHand[6]);
            handPosLabels[7].setIcon(playerHand[7]);
            handPosLabels[8].setIcon(playerHand[8]);
            handPosLabels[9].setIcon(playerHand[9]);
            handPosLabels[10].setIcon(playerHand[10]);
            handPosLabels[11].setIcon(playerHand[11]);
             
            messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
            cpuTurn(); // It is now the AI's turn
        }else if (playerHandSize <= 10 && deckSize==1)  { // Should only execute if playerHandSize <= 10
            playerHand[playerHandSize] = deck[deckSize - 1]; // Give the user a card from the top of the deck
            removeCardFromDeck(deck[deckSize - 1]);          // Remove the top card from the deck
            playerHandSize++;                              // The user now has an additional card
           
            handPosLabels[0].setIcon(playerHand[0]); // Set handPosLabels[0] to diplay the icon for playerHand[0]
            handPosLabels[1].setIcon(playerHand[1]); // Set handPosLabels[1] to display the icon for playerHand[1]
            handPosLabels[2].setIcon(playerHand[2]); // Set handPosLabels[2] to display the icon for playerHand[2]
            handPosLabels[3].setIcon(playerHand[3]); // And so on ...
            handPosLabels[4].setIcon(playerHand[4]);
            handPosLabels[5].setIcon(playerHand[5]);
            handPosLabels[6].setIcon(playerHand[6]);
            handPosLabels[7].setIcon(playerHand[7]);
            handPosLabels[8].setIcon(playerHand[8]);
            handPosLabels[9].setIcon(playerHand[9]);
            handPosLabels[10].setIcon(playerHand[10]);
            handPosLabels[11].setIcon(playerHand[11]);
            messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);  
            
            cpuTurn(); // It is now the AI's turn
        }
    }
    
    private void playPos1LabelMouseClicked(MouseEvent evt) {
        if (playerHandSize <= 0) return; // If the user has played all of their cards, the game is over
        if (cpuHandSize <= 0) return;    // If the AI has played all of their cards, the game is over
        
        // If player hasn't clicked a card in their hand, return
        if (activeCard == null) return;
        
        activePosition = 0; // Store activePosition for cpuTurn
        playActiveCardToPosition((JLabel)playPos1Label); // Play the active card to this label
    }
    
    private void playPos2LabelMouseClicked(MouseEvent evt) {
        if (playerHandSize <= 0) return; // If the user has played all of their cards, the game is over
        if (cpuHandSize <= 0) return;    // If the AI has played all of their cards, the game is over
        
        // If player hasn't clicked a card in their hand, return
        if (activeCard == null) return;
        
        activePosition = 1; // Store activePosition for cpuTurn
        playActiveCardToPosition((JLabel)playPos2Label); // Play the active card to this label
    }
    
    private void playPos3LabelMouseClicked(MouseEvent evt) {
        if (playerHandSize <= 0) return; // If the user has played all of their cards, the game is over
        if (cpuHandSize <= 0) return;    // If the AI has played all of their cards, the game is over
        
        // If player hasn't clicked a card in their hand, return
        if (activeCard == null) return;
        
        activePosition = 2; // Store activePosition for cpuTurn
        playActiveCardToPosition((JLabel)playPos3Label); // Play the active card to this label
    }
    
    private void playPos4LabelMouseClicked(MouseEvent evt) {
        if (playerHandSize <= 0) return; // If the user has played all of their cards, the game is over
        if (cpuHandSize <= 0) return;    // If the AI has played all of their cards, the game is over
        
        // If player hasn't clicked a card in their hand, return
        if (activeCard == null) return;
        
        activePosition = 3; // Store activePosition for cpuTurn
        playActiveCardToPosition((JLabel)playPos4Label); // Play the active card to this label
    }
    
    private void playPos5LabelMouseClicked(MouseEvent evt) {
        if (playerHandSize <= 0) return; // If the user has played all of their cards, the game is over
        if (cpuHandSize <= 0) return;    // If the AI has played all of their cards, the game is over
        
        // If player hasn't clicked a card in their hand, return
        if (activeCard == null) return;
        
        activePosition = 4; // Store activePosition for cpuTurn
        playActiveCardToPosition((JLabel)playPos5Label); // Play the active card to this label
    }
    
    /**
     * Play the Card activeCard to the JLabel specified in the playPosXLabelMouseClicked
     * method. Currently throws a NullPointerException whenever it is called - 
     * perhaps the playPosXLabel throws the NullPointerException
     * 
     * This is done by checking to see if the values of activeCard and the card 
     * on top of the pile that was clicked on add to 10. If they do, the icon of 
     * the pile is changed, activeCard is removed from the hand, and the 
     * computer's turn is executed. Else, the user is told that the cards do not 
     * add up to 10, and all necessary values are reset.
     * 
     * @param playPositionLabel The label displaying the card the user chose to play to
     */
    private void playActiveCardToPosition(JLabel playPositionLabel) {
        int handValue = ((Card)activeCard).getValue();                            // Determine the value of activeCard
        int playPositionValue = (((Card)playPositionLabel.getIcon()).getValue()); // Determine the value of the Card at playPositionLabel (currently doesn't work)
        int sum = handValue + playPositionValue;                                  // Determine sum of handValue and playPositionValue
        
        if (sum == 10) {
            playPiles[activePosition] = activeCard; // Play the card to activePosition
            playPositionLabel.setIcon(activeCard);  // Display the icon of the played card
            
            // Remove activeCardLabel from hand
            
            // -----------------------------------------------------------------
            // The following for loop was taken from private void 
            // removeCardFromHand(Card removeMe)
            for (int i = 0; i < playerHandSize; i++) {
                if (playerHand[i] == activeCard) {
                    for (int i2 = i; i2 < playerHandSize - 1; i2++) {
                        playerHand[i2] = playerHand[i2 + 1];
                    }
                    
                    playerHandSize--; // The player has now played a card                    
                    playerHand[playerHandSize] = null;
                    messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
                    break; // Quit for loop
                }
            }
            
            for (int i2 = 0; i2 < 12; i2++) {
                handPosLabels[i2].setIcon(playerHand[i2]);
            }
            // End of old removeCardFromHand -----------------------------------
            
            if (playerHandSize <= 0) { // Player has no remaining cards
                messageArea.append("\nYOU WIN!"); // Let's the player know they won
                return; // Prevents the following code from executing
            }
            
            cpuTurn(); // Execute AI's turn
        }
        else { // Make sure that all necessary values are reset
            messageArea.append("\nYour two cards do not add to 10. Please try again."); // Tells the user their move was invalid
            activeCardLabel = null;
            playPositionLabel = null; // Resets playPositionLabel
            activeCard = null;        // Resets activeCard
            handValue = 0;            // Resets handValue
            playPositionValue = 0;    // Resets playPositionValue
            sum = 0;                  // Resets sum
        }
    }
    
    /**
     * Looks through the deck for a Card, removes it, and shifts all cards after
     * forwards in the deck array, and decrements deckSize. It is important that
     * this method is only called to remove one card at a time, and that it does
     * not interfere with other methods if it should need to reshuffle the deck.
     * 
     * If deckSize is less than or equal to zero, take all of the cards from the
     * piles except for the ones on the very top, put them back in the deck, and
     * reshuffle the deck. This can be done by storing all the cards that are 
     * played in arrays for each playing pile, moving these cards back into the 
     * deck, and doing whatever else is necessary in that case; or the deck 
     * could be re-populated by every card except for the ones in the playPiles
     * array and then simply re-shuffled.
     * 
     * @param removeMe Card to be removed from the deck
     */
    
    /*
    
    two major errors in removeCardFromDeck method responsible for repeats of cards.
    One cause of this is the second for loop. When a card is taken from the 
    deck at any point, in this method represented by i, to properly remove it
    every card above i should be shifted down one. Currently, every card below i
    is being shifted down one, meaning that instead of removing the card at index
    i, the card at index 0 is being removed. Understanding what actions need to 
    take place in this method it helps to think of the deck set up in this class 
    not as a phisical deck of cards, but rather just a mathimatical structure
    you need to manipulate to achieve a goal. The second error is that in the
    second loop index i is used to relocate cards as opposed to i2.   
    Now that the error has been fixed, another error, simply fixed will arrise.
    Now that cards are correctly being removed from the deck, when cards are referenced
    to by hard-coded indexes these indexes will be offset. This means when cards
    are first drawn for the board at the begining of the game cards will no longer
    be in ascending order. A simple fix to this is to select all the cards for the
    board before removing a single once, and then after all cards have been selected 
    remove all selected cards. 
    
    */
    
    private void removeCardFromDeck(Card removeMe) {
        if (deckSize<= 0) { System.out.println("out of cards");}
            else {   
           
        for (int i = 0; i < deckSize; i++) {
            if (deck[i] == removeMe) {
                
                for (int i2 = i; i2 < deckSize-1; i2++) {
                    deck[i2] = deck[i2 + 1];
                }
                
                deckSize--; // A card has been removed from the deck
                
                break; // Quits for loop
            }
        }
        
        
            }
    }
    
    /**
     * Determines the AI of the player's opponent.
     * 
     * Starting with the first card in the hand, check to see if cards can be 
     * played. The first card that can be played will be played. Evidently, this 
     * will require a loop that will only run until it has looked at each card 
     * and up to when a card is played.
     */
    private void cpuTurn() 
    {   boolean cardPlayed = false; // Used to determine if the AI played a card
       
        for (int i = 0; i < cpuHandSize; i++) {
         
            int inHandValue = cpuHand[i].getValue(); // Get value of cpuHand[i]
            Card currentCard = cpuHand[i];           // Store cpuHand[i]
            
            for (int i2 = 0; i2 < playPiles.length; i2++) {
              
                int onPileValue = playPiles[i2].getValue(); // Get value of playPiles[i2]
                int sum = inHandValue + onPileValue;        // Calculate sum of inHandValue and onPileValue
              
                
               if (sum == 10) {
                   int  int_cardposition = i2 +1;
                   messageArea.append("\nThe card in the computer's hand " + inHandValue + "\n and the card on the pile " + onPileValue + "  add to " + sum);
                     messageArea.append("\nIt is played in card pile " + int_cardposition);
                    
                    playPiles[i2] = currentCard;            // Set playPiles[i2] to currentCard
                         playPosLabels[i2].setIcon(currentCard);
                 
                         
                    // Remove currentCard from hand
                   removeCardFromCPUHand(currentCard);
                  cardPlayed = true; // The AI did indeed play a card
                   messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
                    
                    // Inform the user the AI has played a card
                  // messageArea.append("The computer played " + currentCard + ".");
                    
                    if (cpuHandSize <= 0) {
                        messageArea.append("\nYOU LOSE!"); // Inform the user that the AI has won
                      return; // Prevents the rest of this method from executing
                    }
                    break; // The cards added to 10, and the AI has played a card
                  
               }
            }
            if (cardPlayed == true) break;
        }
        if (cardPlayed == false) {
            if (cpuHandSize == 12) {
                   System.out.println(cpuHandSize +"computer hand full   decksize" + deckSize);
                // Inform the user that the AI was unable to play
                messageArea.append("\nThe computer was unable to play.");
                return;
            }
            else if (cpuHandSize == 11&& deckSize>1) {
                cpuHand[cpuHandSize] = deck[deckSize - 1]; // Give the AI the top card on the deck
                removeCardFromDeck(deck[deckSize - 1]);    // Remove the card from the deck
                cpuHandSize++; // The AI now has an additional card
                 
                messageArea.append("\nThe computer drew one card."); // Let the user know the AI has drawn a card
                 messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
                return;
            }
             else if (cpuHandSize <= 10 && deckSize>=2) { // cpuHandSize <= 10
                cpuHand[cpuHandSize] = deck[deckSize - 1]; // Give the AI the top card on the deck
                removeCardFromDeck(deck[deckSize - 1]);    // Remove the card from the deck
                cpuHandSize++;   
                // The AI now has an additional card
                cpuHand[cpuHandSize] = deck[deckSize - 1]; // Repeat the previous three line with the altered values
                removeCardFromDeck(deck[deckSize - 1]);
                cpuHandSize++;
                
                messageArea.append("\nThe computer drew two cards."); // Let the user know the AI has drawn two cards
                 messageArea.append("\nDeckSize"+ deckSize+"\nplayerhand size"+ playerHandSize +"\ncomputer hand size"+ cpuHandSize);
                return;
            }
        }
        
        messageArea.append("\nChoose a card to play."); // Let the user know it is now their turn
  }
  
    private void removeCardFromCPUHand(Card removeMe) {
         System.out.println("computer handsize" + cpuHandSize);
        for (int i = 0; i < cpuHandSize; i++) {
            if (cpuHand[i] == removeMe) {
                for (int i2 = i; i2 < cpuHandSize - 1; i2++) {
                    cpuHand[i2] = cpuHand[i2 + 1];
                }
                
                cpuHandSize--; // A card has been removed from the AI's hand
                  System.out.println("computers new handsize" + cpuHandSize);
                cpuHand[cpuHandSize] = null;
                
                break; // Quits for loop
            }
        }
    }
}
