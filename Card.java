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

/**
 *
 * @author Shane Bishop
 */
public class Card extends javax.swing.ImageIcon {
    private int suit;
    private int value;
    
    //this is a collection of 4 arrays, one written per line, which are 
    //referened deck[suit][value]
    private static final String[][] deck = {
        {"AC.png", "2C.png", "3C.png", "4C.png", "5C.png", "6C.png", "7C.png", "8C.png", "9C.png"},
        {"AD.png", "2D.png", "3D.png", "4D.png", "5D.png", "6D.png", "7D.png", "8D.png", "9D.png"},
        {"AH.png", "2H.png", "3H.png", "4H.png", "5H.png", "6H.png", "7H.png", "8H.png", "9H.png"},
        {"AS.png", "2S.png", "3S.png", "4S.png", "5S.png", "6S.png", "7S.png", "8S.png", "9S.png"},
    };
    
    public Card (int suit, int value) {
        super(Card.class.getResource("/u1a6functions/images/" + deck[suit - 1][value - 1]));
        this.suit = suit;
        this.value = value;
    }
    
    /*
    * Returns an int for the suit of a Card
    */    
    public int getSuit() {
        return suit;
    }
    
    /*
    * Returns the numerical value of a Card as an int
    */
    public int getValue() {
        return value;
    }
}
