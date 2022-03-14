// Alexander Wolf
// 1/31/22

package awblackjack; // part of awblackjack package

import java.util.ArrayList; // import ArrayList class
import java.util.Scanner; // import Scanner class
import java.security.SecureRandom; // import SecureRandom class
import java.util.Stack; // import Stack class

// begin BlackjackMain class
public class BlackjackMain {
	// begin main method
	public static void main(String[] args) {
		// instantiate Blackjack object
		Blackjack game = new Blackjack();
		// play multiple games with user
		game.playMultipleGames();
	} // end main method
} // end BlackjackMain class

// begin Blackjack class
class Blackjack {
	// create new private deck of cards
	private DeckOfCards gameDeck = new DeckOfCards();
	// instantiate new user input Scanner
	private Scanner console = new Scanner(System.in);
	// initialize boolean for player card total > 21
	private boolean playerBust;
	// initialize boolean for dealer card total > 21
	private boolean dealerBust;
	// initialize boolean for player win
	private boolean win;
	// counter of number of games played
	private int gameCount;
	// counter of number of games won
	private int winningGameCount;
	// cumulative sum of card value total from winning games
	private int winningTotal;
	
	// begin playGame - play one game with the user
	public int playOneGame() {
		// dealer hand to be populated later
		ArrayList<Card> dealerHand = null;
		// player hand to be populated later
		ArrayList<Card> playerHand = new ArrayList<Card>();
		int choice = 0; // declare variable for hit or stand
		playerBust = false; // if card total > 21, = true
		dealerBust = false; // if card total > 21, = true
		
		gameDeck.shuffle(); // shuffle and populate deck of cards
		dealerHand = getTwoCardHand(); // get dealer's hand
		playerHand.add(gameDeck.dealCard()); // add one card to player's hand
		// Display game number
		System.out.printf("\nGame number %d\n", gameCount);
		do { // loop if player chooses to get additional card(s)
			playerHand.add(gameDeck.dealCard()); // add card to hand
			
			// Show one dealer card, second card stays hidden
			System.out.printf("\nDealer's visible card:\t%s\n\n", dealerHand.get(0));
			// give player card names and total value of their cards
			System.out.printf("Your hand:\t\t\t\t%s%n", getHandContents(playerHand));
			System.out.printf("Your total:\t\t\t\t%d%n", getTotal(playerHand));
			
			// notify player if their card value has exceeded 21
			if (getTotal(playerHand) > 21) {
				System.out.println("Your card value total exceeds 21.");
				playerBust = true; // card value has exceeded 21
				break; // break out of loop, end game
			} // end if condition
			
			do { // prompt user to select 1 (hit) or 2 (stand)
				try { // try to get user input of 1 or 2
					System.out.print("Would you like to hit(1) or stand(2)? ");
					choice = console.nextInt();
				// catch InputMistmatchException
				} catch (Exception e) {
					console.next(); // discard input
				} // end try/catch
			} while (choice != 1 && choice != 2); // loop while input not 1 or 2
		} while (choice == 1); // end do while
		
		// reveal dealer's full hand and total card value
		System.out.printf("\n%s", getDealerResults(dealerHand));
		// tell player if they have won or lost
		System.out.println(getResults(playerHand, dealerHand));
		
		// if player wins, return card total value
		if (win) {
			winningGameCount++;
			return getTotal(playerHand);
		} else {
			// if player loses, return 0
			return 0;
		} // end if/else
	} // end playOneGame method
	
	// begin playMultipleGames - call playOneGame and prompt user to
	// choose to play more games or end program
	public void playMultipleGames() {
		gameCount = 0; // initialize game counter
		winningGameCount = 0; // initialize winning game counter
		winningTotal = 0; // initialize card value sum from winning games
		char playAgain;
		System.out.println(getGreeting());
		do { // play multiple games
			gameCount++; // increment game counter
			// add total card value from winning games to cumulative sum
			winningTotal += playOneGame();
			do { // prompt user until y/n entered
				System.out.print("Would you like to play again(y/n)? " );
				playAgain = console.next().charAt(0); // first char of input
			} while (playAgain != 'y' && playAgain != 'Y'
					&& playAgain != 'n' && playAgain != 'N'); // end do while
		} while (playAgain == 'y' || playAgain == 'Y'); // end do while
		
		System.out.println(getGameInfo()); // display results of games played
	} // end playMultipleGames method
	
	// begin getResults - tell player whether they won, lost, or tied,
	// and if they have blackjack
	public String getResults(ArrayList<Card> playerHand,
		ArrayList<Card> dealerHand) {
			win = false; // set to false each time method is called
		
		// blackjack if player gets 21 in two cards
		if (getTotal(playerHand) > getTotal(dealerHand)
			&& getTotal(playerHand) == 21 && playerHand.size() == 2) {
				win = true;
				return "You got Blackjack!\nCongratulations, you won!";
		// player card value total greater than dealer total
		} else if ((getTotal(playerHand) > getTotal(dealerHand) && !playerBust)
			|| (!playerBust && dealerBust)) {
				win = true;
				return "Congratulations, you won!";
		// player card value total less than dealer total, or total exceeds 21
		} else if (getTotal(playerHand) < getTotal(dealerHand) || playerBust
			|| (playerBust && dealerBust)) {
				return "You lost.";
		// player card value total is same as dealer total
		} else {
			return "You tied with the dealer.";
		} // end if/else
	} // end getResults method
	
	// begin getGameInfo - return data/calculations about games played
	public String getGameInfo() {
		return "\nGames played:\t\t\t\t\t\t\t" + gameCount
			+ "\nWinning games:\t\t\t\t\t\t\t" + winningGameCount
			+ "\nAverage total per winning game:\t"
				// (Total card value from winning games)/(number of winning games)
				// rounded to 2 decimal places
			+ Math.round(
					(double) winningTotal / (double) winningGameCount
					* 10.0) / 10.0;
	} // end getGameInfo
	
	// begin getTotal - get total of ArrayList of Cards passed in
	// post: "Ace" card has been assigned value of 1 or 11
	public int getTotal(ArrayList<Card> hand) {
		int total = 0;
		// traverse hand and create cumulative value of card
		// value total - does not identify "Ace" cards
		for (int i = 0; i < hand.size(); i++) {
			if (hand.get(i).toString().contains("Two")) {
				total += 2;
			} else if (hand.get(i).toString().contains("Three")) {
				total += 3;
			} else if (hand.get(i).toString().contains("Four")) {
				total += 4;
			} else if (hand.get(i).toString().contains("Five")) {
				total += 5;
			} else if (hand.get(i).toString().contains("Six")) {
				total += 6;
			} else if (hand.get(i).toString().contains("Seven")) {
				total += 7;
			} else if (hand.get(i).toString().contains("Eight")) {
				total += 8;
			} else if (hand.get(i).toString().contains("Nine")) {
				total += 9;
			} else if (hand.get(i).toString().contains("Ten")
				|| (hand.get(i).toString().contains("Jack"))
				|| (hand.get(i).toString().contains("Queen"))
				|| (hand.get(i).toString().contains("King"))) {
					total += 10;
			} else {
				// do nothing
			} // end if/else
		} // end for loop
		
		// traverse hand and add value of "Ace" cards to
		// cumulative total
		// post: Ace card has been assigned value of 1 or 11
		for (int i = 0; i < hand.size(); i++) {
			if (hand.get(i).toString().contains("Ace")) {
				if (total > 10) { // if "Ace" = 11 would exceed 21
					total += 1;
				} else {
					total += 11;
				} // end inner if/else
			} // end outer if/else
		} // end for loop
		return total;
	} // end getTotal method
	
	// begin getHandContents - return String stating cards in current hand
	public String getHandContents(ArrayList<Card> hand) {
		String currentHand = "" + hand.get(0);
		for (int i = 1; i < hand.size(); i++) {
			currentHand += "\n\t\t\t\t\t\t\t" + hand.get(i);
		}
		return currentHand;
	} // end getHandContents method
	
	// begin getTwoCardHand - populate and return ArrayList<Card>
	// of size 2
	public ArrayList<Card> getTwoCardHand() {
		ArrayList<Card> twoCardHand = new ArrayList<Card>();
		
		// deal two cards from deck to hand
		for (int i = 0; i < 2; i++) {
			twoCardHand.add(gameDeck.dealCard());
		}
		return twoCardHand;
	} // end getTwoCardHand method
	
	// begin getDealerResults - if total value of Cards in 
	// dealerHand ArrayList < 17, continue adding Cards to
	// ArrayList until total value >= 17 - return String
	// concatenation of all cards
	public String getDealerResults(ArrayList<Card> dealerHand) {
		// get dealer card value total
		int dealerTotal = getTotal(dealerHand);
		
		// add cards automatically until dealer total >= 17
		while ( dealerTotal < 17) {
			dealerHand.add(gameDeck.dealCard());
			dealerTotal = getTotal(dealerHand);
		} // end while loop
		
		// if 17 <= dealer card total <= 21
		if (dealerTotal >= 17 && dealerTotal <= 21) {
			return "Dealer's full hand:\t"
				+ getHandContents(dealerHand) + "\n"
				+ "Dealer total:\t\t\t" + getTotal(dealerHand)
				+ "\n\n";
		} else { // if dealer value exceeds 21, dealerBust = true
			dealerBust = true;
			return "Dealer's full hand:\t\t"
				+ getHandContents(dealerHand) + "\n"
				+ "Dealer total:\t\t\t" + getTotal(dealerHand)
				+ "\nThe dealer's card value total exceeds 21.\n\n";
		} // end if/else
	} // end getDealerResults
	
	// begin getGreeting - return String introducing game
	public String getGreeting() {
		return"Welcome to the game of Blackjack! The objective "
			+ "of this game is\n"
			+ "to get a card count which beats the dealer’s by "
			+ "getting as close to\n"
			+ "21 as possible without exceeding 21. Numbered "
			+ "cards are worth\n"
			+ "their number value, face cards are worth ten, and "
			+ "aces are worth\n"
			+ "1 or 11. Choose to \"hit\" to get another card, or "
			+ "\"stand\" to end your\n"
			+ "turn with your current total. The dealer will make "
			+ "one card from\n"
			+ "their hand visible during your turn, while hiding "
			+ "the other. When\n"
			+ "you have finished your turn, the dealer will display "
			+ "their hidden card,\n"
			+ "and, if their total is less than 17, they will "
			+ "automatically draw cards\n"
			+ "until their value is greater than or equal to 17. If "
			+ "both you and the\n"
			+ "dealer go \"bust,\" with a total exceeding 21, you "
			+ "will lose the game. ";
	} // end getGreeting
} // end Blackjack class

// begin DeckOfCards class
class DeckOfCards {
	// random number generator
	private static final SecureRandom randomNumbers = new SecureRandom();
	private static final int NUMBER_OF_CARDS = 52;
		
	private Card[] deck = new Card[NUMBER_OF_CARDS]; // Card references
	// create Stack of Cards to be populated
	private Stack<Card> shuffledDeck = new Stack<>();
	private int currentCard = 0; // index of next Card to be dealt (0-51)
		
	// constructor fills deck of Cards
	public DeckOfCards() {
		String[] face = {Face.ACE.toString(), Face.TWO.toString(),
			Face.THREE.toString(), Face.FOUR.toString(),
			Face.FIVE.toString(), Face.SIX.toString(),
			Face.SEVEN.toString(), Face.EIGHT.toString(),
			Face.NINE.toString(), Face.TEN.toString(),
			Face.JACK.toString(), Face.QUEEN.toString(),
			Face.KING.toString()};
		String[] suit = {Suit.HEARTS.toString(), Suit.DIAMONDS.toString(),
			Suit.CLUBS.toString(), Suit.SPADES.toString()};
			
		// populate deck with Card objects
		for (int count = 0; count < deck.length; count++) {
			deck[count] = new Card(face[count % 13], suit[count / 13]);
		} // end for loop
	} // end DeckOfCards constructor
		
	// shuffle deck of Cards with one-pass algorithm
	public void shuffle() {
		// next call to method dealCard should start at deck[0] again
		currentCard = 0;
			
		// for each Card, pick another random Card (0-51_ and swap them
		for (int first = 0; first < deck.length; first++) {
			// select a random number between 0 and 51
			int second = randomNumbers.nextInt(NUMBER_OF_CARDS);
				
			// swap current Card with randomly selected Card
			Card temp = deck[first];
			deck[first] = deck[second];
			deck[second] = temp;
		} // end for loop
			
		// push each card of shuffled deck to stack
		for (Card c : deck) {
			shuffledDeck.push(c);
		} // end for loop
	} // end shuffle method
		
	// deal one card
	public Card dealCard() {
		Card oneCard = null; // initialize Card object to null
		try {
			oneCard = shuffledDeck.pop(); // return current Card in array
		} catch (Exception e) { // if deck is empty, catch Exception
			System.out.println("The deck is empty.");
		} // end try/catch
		return oneCard;
	} // end dealCard method
		
	// begin Face enum
	public enum Face {
		ACE("Ace"), TWO("Two"), THREE("Three"), FOUR("Four"),
		FIVE("Five"), SIX("Six"), SEVEN("Seven"), EIGHT("Eight"),
		NINE("Nine"), TEN("Ten"), JACK("Jack"), QUEEN("Queen"),
		KING("King");
			
		String faceName; // initialize face name String
		// constructor stores faceName String
		Face(String s) {
			this.faceName = s;
		} // end constructor
				
		// begin toString method
		public String toString() {
			return faceName;
		} // end toString method
	} // end Face enum
		
	// begin Suit enum
	public enum Suit {
		HEARTS("Hearts"), DIAMONDS("Diamonds"), CLUBS("Clubs"),
		SPADES("Spades");
			
		String suitName; // initialize suit name String
		// constructor stores suitName String
		Suit(String s) {
			this.suitName = s;
		} // end constructor
			
		// begin toString method
		public String toString() {
			return suitName;
		} // end toString method
	} // end Suit enum
} // end DeckOfCards class

// begin Card class
class Card {
	private final String face; // face of card ("Ace", "Jack", etc)
	private final String suit; // suit of card ("Hearts" "Diamonds", etc)
	
	// two argument constructor initializes card's face and suit
	public Card(String cardFace, String cardSuit) {
		this.face = cardFace; // initialize face of card
		this.suit = cardSuit; // initialize suit of card
	} // end Card constructor
	
	// return String representation of Card
	public String toString() {
		return face + " of " + suit;
	} // end toString
} // end Card class
