import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Game{

  private static int currentBet;
  private static int pot;
  private static int bigBet;
  private int lastRaisedPosition;
  private List<Player> allPlayers = new ArrayList<Player>();
  private int userPosition;
  private List<Card> communityCards = new ArrayList<Card>();
  private static boolean gameOver;
  private String[] handRanks = {"Royal Flush", "Straight Flush", "Four Of A Kind", "Full House", "Flush", "Straight", "Three Of A Kind", "Two Pair", "Pair", "High Card"};
  static Scanner in = new Scanner(System.in);

  public Game(int people, int bet){
    Deck.initializeDeck();
    bigBet = bet;
    lastRaisedPosition =-1;

    int rand = (int) (Math.random()*(people+1));
    //players before user
    for (int i=0; i<rand; i++){
      Player newGuy = new Npc();
      allPlayers.add(newGuy);
    }
    //inserting user into random position in list
    userPosition = rand;
    Player newUser = new User(rand+1);
    allPlayers.add(rand, newUser);
    //players after user
    for (int i=rand+1; i<people+1; i++){
      Player newGuy = new Npc();
      allPlayers.add(newGuy);
    }
    
  }

  //input verification
  public static String getResponse(String question, List<String> options){
    System.out.println(question);
    String answer = in.nextLine().toLowerCase();
    while(!options.contains(answer)){
      System.out.println("Invalid input. Please only respond with: " + options);
      answer = in.nextLine().toLowerCase();
    }
    return answer;
  }

  public static int getResponseBetween(String question, int one, int two){
    System.out.println(question);
    int answer = in.nextInt();
    while(answer < one || answer > two){
      System.out.println("Invalid input. Please only respond with an integer between " + one + " and " + two);
      answer = in.nextInt();
    }
    in.nextLine();
    return answer;
  }


  //checking who wins
  public Player findWinner(){

    //assign everyone handRanks
    for (int i=0; i<allPlayers.size(); i++){
      if(!allPlayers.get(i).getHasFolded()){
        List<Card> availableCards = new ArrayList<Card>();
        availableCards.addAll(allPlayers.get(i).getHand());
        availableCards.addAll(communityCards);
        List<Card> flush = checkFlush(availableCards);
        List<Card> straight = checkStriaght(availableCards);
        int[] pairs = checkForMatches(availableCards);
        if(!flush.isEmpty()){
          straight = checkStriaght(flush);
          if(!straight.isEmpty()){
            if(checkRoyalFlush(straight)){
              //Royal Flush
              allPlayers.get(i).setHandRank(0);
            } else{
              //Straight Flush
              allPlayers.get(i).setHandRank(1);
            }
          } else{
            //flush 
            allPlayers.get(i).setHandRank(4);
          } 
        }else if(!straight.isEmpty()){
          //straight
          allPlayers.get(i).setHandRank(5);   
        }else if(pairs[0] == 4){
          //four of a Kind
          allPlayers.get(i).setHandRank(2);  
        }else if(pairs[0] == 5){
          //Full House
          allPlayers.get(i).setHandRank(3);  
        }else if(pairs[0] == 3){
          //three of a kind
          allPlayers.get(i).setHandRank(6);  
        }else if(pairs[0] == 2){
          //two pair
          allPlayers.get(i).setHandRank(7);  
        }else if(pairs[0] == 1){
          //pair
          allPlayers.get(i).setHandRank(8);  
        }else{
          //high card
          allPlayers.get(i).setHandRank(9);  
        }   
      }
      
    }

    //order by rank
    allPlayers = sortHandRank(allPlayers);

    //check for ties
    int winningRank = allPlayers.get(0).getHandRank();
    int count = 1;
    for(int i=1; i<allPlayers.size(); i++){
      if(allPlayers.get(i).getHandRank() == winningRank){
        count++;
      } else{
        break;
      }
    }
    if(count>1){
      int position=0;
      for(int i=0; i<count; i++){
        if(winningRank == 2 || winningRank == 3 || winningRank >5){
          List<Card> cards1 = new ArrayList<Card>();
          cards1.addAll(communityCards);
          cards1.addAll(allPlayers.get(position).getHand());
          List<Card> cards2 = new ArrayList<Card>();
          cards2.addAll(communityCards);
          cards2.addAll(allPlayers.get(i).getHand());
          int[] pairs1 = checkForMatches(cards1);
          int[] pairs2 = checkForMatches(cards2);
          if(pairs1[1] < pairs2[1]){
            position = i;
          }
        } else{
          List<Card> cards1 = new ArrayList<Card>();
          cards1.addAll(communityCards);
          cards1.addAll(allPlayers.get(position).getHand());
          List<Card> cards2 = new ArrayList<Card>();
          cards2.addAll(communityCards);
          cards2.addAll(allPlayers.get(i).getHand());
          if(getHighCard(cards1).getRank() < getHighCard(cards2).getRank()){
            position = i;
          }
        }
      }
      return allPlayers.get(position);
    } else{
      return allPlayers.get(0);
    }
  }

  //flush
  public List<Card> checkFlush(List<Card> cards){
    cards = sortSuit(cards);
    List<Card> flush = new ArrayList<Card>();
    String suit = cards.get(0).getSuit();
    int count=0;
    for(int i=0; i<cards.size(); i++){
      if(cards.get(i).getSuit().equals(suit)){
        flush.add(cards.get(i));
        count++;
      } else{
        flush.removeAll(flush);
        flush.add(cards.get(i));
        suit = cards.get(i).getSuit();
        count = 1;
      }
    }
    if(count==5){
      return flush;
    } else if(count>5){
      
      flush = sortRank(flush);
      if(count == 6){
        flush.remove(0);
      } else{
        flush.remove(0);
        flush.remove(0);
      }
      return flush;
    } else{
      flush.removeAll(flush);
      return flush;
    }
 
  }

  //straight

  public List<Card> checkStriaght(List<Card> cards){
    cards = sortRank(cards);
    List<Card> straight = new ArrayList<Card>();
    int count=0;
    for(int i=1; i<cards.size(); i++){
      if(straight.size()==0){
        straight.add(cards.get(i-1));
        count=1;
      }
      if(cards.get(i).getRank() == 1+ cards.get(i-1).getRank()){
        straight.add(cards.get(i));
        count++;
      } else{
        straight.removeAll(straight);
      }
    }
    // in the ace 2 3 4 5 situation
    if((count==4 && straight.get(0).getRank() ==2) && cards.get(cards.size()-1).getRank() == 14){
      //if last two are aces, check for flush possibility
      if(cards.get(cards.size()-2).getRank() == 14 && cards.get(cards.size()-2).getSuit().equals(straight.get(0).getSuit())){
        straight.add(cards.get(cards.size()-2));
      }else{
        straight.add(cards.get(cards.size()-1));
      }
    }
    if(count==5){
      return straight;
    } else if(count>5){
      if(count == 6){
        straight.remove(0);
      } else{
        straight.remove(0);
        straight.remove(0);
      }
      return straight; 
    } else{
      straight.removeAll(straight);
      return straight;
    }

  }

  //high card
  public Card getHighCard(List<Card> cards){
    cards = sortRank(cards);
    return cards.get(cards.size()-1);
  }

  // royal flush
    // precondition: already straight flush
  public boolean checkRoyalFlush(List<Card> cards){
    boolean hasAce = false;
    boolean hasKing = false;
    for (Card card: cards){
      if(card.getRank() == 14){
        hasAce = true;
      }
      if(card.getRank() == 13){
        hasKing = true;
      }
    }
    if (!hasAce || !hasKing){
      return false;
    } else{
      return true;
    }
  }

  // finding pairs, trios and quads
    // 0 = none, 1 = pair, 2 = two pairs, 3 = trio, 4 = quad, 5 = full house
  public int[] checkForMatches(List<Card> cards){
    List<Card> toBurn = new ArrayList<Card>();
    toBurn.addAll(cards);
    List<Card> temp = new ArrayList<Card>();
    List<Card> twos = new ArrayList<Card>();
    List<Card> trio = new ArrayList<Card>();
    // holds type of pair and value of high card
    int[] result = new int[2];

    while(toBurn.size()>0){
      int count = 1;
      temp.add(toBurn.get(0));
      for(int i = 1; i<toBurn.size(); i++){
        if(toBurn.get(0).getRank() == toBurn.get(i).getRank()){
          count++;
          temp.add(toBurn.get(i));
        }
      }
      if(temp.size()==1){
        toBurn.removeAll(temp);
        temp.removeAll(temp);
      } else if(temp.size()==2){
        toBurn.removeAll(temp);
        twos.addAll(temp);
        temp.removeAll(temp);
      } else if(temp.size()==3){
        toBurn.removeAll(temp);
        trio.addAll(temp);
        temp.removeAll(temp);
      } else{
        result[0] = 4;
        result[1] = temp.get(0).getRank();
        return result;
      }
    }
    if(twos.size()==4){
      result[0] = 2;
      result[1] = twos.get(0).getRank();
      return result;
    } else if(twos.size()==2 && trio.size()==3){
      result[0] = 5;
      if(trio.get(0).getRank() > twos.get(0).getRank()){
        result[1] = trio.get(0).getRank();
      } else{
        result[1] = twos.get(0).getRank();
      }
      return result;
    } else if(!twos.isEmpty()){
      result[0] = 1;
      result[1] = twos.get(0).getRank();
      return result;
    } else if(!trio.isEmpty()){
      result[0] = 3;
      result[1] = trio.get(0).getRank();
      return result;
    } else{
      result[0] = 0;
      result[1] = getHighCard(cards).getRank();
      return result;
    } 
  }


  


  //bubble sorting

  public List<Card> sortSuit(List<Card> cards){
    boolean swapped; 
    for (int i = 0; i < cards.size()-1; i++) {
      swapped = false;
      for (int j = 0; j < cards.size() - i-1; j++) {
          if (cards.get(j).getSuit().compareTo(cards.get(j+1).getSuit()) <0) {
              cards.set(j, cards.set(j+1, cards.get(j)));
              swapped = true;
          }
      }
      if (swapped == false) {
          return cards;
      }
    }
    return cards;
  }

  public List<Card> sortRank(List<Card> cards){
    boolean swapped;
    for (int i = 0; i < cards.size()-1; i++) {
      swapped = false;
      for (int j = 0; j < cards.size() - i-1; j++) {
          if (cards.get(j).getRank() > cards.get(j+1).getRank() ) {
              cards.set(j, cards.set(j+1, cards.get(j)));
              swapped = true;
          }
      }
      if (swapped == false) {
        return cards;
      }
    }
    return cards;
  }

  public List<Player> sortHandRank(List<Player> people){
    boolean swapped;
    int counter = 0;  
    for (int i = 0; i < people.size()-1; i++) {
      counter = i + 1;
      swapped = false;
      for (int j = 0; j < people.size() - i-1; j++) {
          //  counter++;
          if (people.get(j).getHandRank() > people.get(j+1).getHandRank() ) {
              people.set(j, people.set(j+1, people.get(j)));
              swapped = true;
          }
      }
      if (swapped == false) {
          return people;
      }
    }
    return people;
  }

//displaying cards
public void showCards(){
  for(Player person: allPlayers){
    if(person.getPlayerNum() != userPosition+1 && !person.getHasFolded()){
      System.out.println("Player " + person.getPlayerNum() + " has " + person.getHand().get(0) +" and "+ person.getHand().get(1));
      wait(2);
    }
  } 
}

//display community cards
public void showCommunity(){
  System.out.println("\nThe community cards this game were: ");
  for(Card card : communityCards){
    System.out.println(card);
  }
}

//delay in printing 
public void wait(int sec){
  try {
    TimeUnit.SECONDS.sleep(sec);
  } catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
  }
}

//clearing console
public void clear(){
  System.out.print("\033[H\033[2J");
  System.out.flush();
}

//press enter to continue
public void pressEnter(){
  System.out.println("Press the Enter key to continue...");
  try{
   System.in.read();
  }  
  catch(Exception e){}
}

//display directions
public void getDirections(){
  clear();
  System.out.println("Here are the directions:\n");
  wait(2);
  System.out.println("You are playing poker against your chosen number of computer generated players.");
  wait(2);
  System.out.println("Every player is dealt two cards, and will have access to the five community cards.");
  wait(2);
  System.out.println("Your goal is to create the highest ranking hand of 5 cards from the seven available to you.");
  wait(2);
  System.out.println("The hand ranks from highest to lowest are: Royal Flush, Straight Flush, Four of a kind, Full House, Flush, Straight, Three of a kind, Two pair, pair, and High Card.");
  wait(3);
  System.out.println("All players are given a random amount between $1000 and $5000 to bet with, and one player will win the pot of money at the end of 4 rounds.");
  wait(2);
  System.out.println("The first round is called the pre-flop in which players must call, bet fold, raise, or match based on their two card hand, until all players have contributed the same amount to the pot or have folded.");
  wait(3);
  System.out.println("In the next round, called the flop, the first three community cards are revealed, after one has been burned. Players continue playing until everyone has contributed the same amount to the pot or folded.");
  wait(3);
  System.out.println("The third round, called the turn, proceeds in the same way with another addition to the community cards.");
  wait(2);
  System.out.println("The final community card is added in the river round, and the last round of betting occurs before the remaining players reveal their cards");
  wait(2);
  System.out.println("When all cards are revealed, the player with the highest ranked hand wins all the money in the pot.");
  wait(2);
  System.out.println("\nGood luck!!\n");
  pressEnter();
}

//display User cards
public void showUserCards(){
  System.out.print("YOUR CARDS: " + allPlayers.get(userPosition).getHand().get(0) + " and " + allPlayers.get(userPosition).getHand().get(1));
}

//display User bank
public void showUserBank(){
  System.out.print("YOUR MONEY: $" + allPlayers.get(userPosition).getBank());
}

//check if no one is left

public boolean hasAllFolded(){
  for(Player person: allPlayers){
    if(person.getPlayerNum() != userPosition+1 && !person.getHasFolded()){
      return false;
    }
  }
  gameOver = true;
  System.out.println("\nEveryone has folded, so you are left with the pot! Congratulations, you won $" +pot);
  return true;
}

//new round messages
public void displayNewRound(int roundNum){
  if(roundNum == 0){
    currentBet = bigBet;
    System.out.println("---------------------------\n|    ROUND 1: PREFLOP    |\n---------------------------\n");
    wait(1);
    showUserCards();
    wait(1);
    System.out.print("              ");
    showUserBank();
    System.out.print("              ");
    wait(1);
    System.out.println("MONEY IN POT: $" + pot);
    System.out.println("----------------------------------------------------------------------------------------------------------------------\n");
    wait(2);
    System.out.println("Player 1 is the Dealer, Player 2 the Small Blind, and Player 3 the Big Blind");
    wait(1);
    System.out.println("You are Player " +(userPosition+1));
    wait(1);
    System.out.println("Player 4, to the left of the Big Blind, will start.\n\n");
    wait(1);
    return;
  }else if(roundNum==1){
    System.out.println("---------------------------\n|      ROUND 2: FLOP      |\n---------------------------\n");
    System.out.println("A card has been burned");
    Deck.burn();
    Card community1 = Deck.draw();
    Card community2 = Deck.draw();
    Card community3 = Deck.draw();
    communityCards.add(community1);
    communityCards.add(community2);
    communityCards.add(community3);
    System.out.println("THE FIRST THREE COMMUNITY CARDS ARE:");
    System.out.println(community1);
    System.out.println(community2);
    System.out.println(community3);
    System.out.println("\n\n");
  }else if(roundNum ==2){
    System.out.println("---------------------------\n|      ROUND 3: TURN      |\n---------------------------\n");
    System.out.println("A card has been burned");
    Deck.burn();
    Card community4 = Deck.draw();
    communityCards.add(community4);
    System.out.println("ANOTHER COMMUNITY CARD HAS BEEN ADDED:");
    System.out.println(communityCards.get(0));
    System.out.println(communityCards.get(1));
    System.out.println(communityCards.get(2));
    System.out.println(community4);
    System.out.println("\n\n");
  }else{
    System.out.println("----------------------------\n|      ROUND 4: RIVER      |\n----------------------------\n");
    System.out.println("A card has been burned");
    Deck.burn();
    Card community5 = Deck.draw();
    communityCards.add(community5);
    System.out.println("THE LAST COMMUNITY CARD HAS BEEN ADDED:");
    System.out.println(communityCards.get(0));
    System.out.println(communityCards.get(1));
    System.out.println(communityCards.get(2));
    System.out.println(communityCards.get(3));
    System.out.println(community5);
    System.out.println("\n\n");
  }
  wait(1);
  showUserCards();
  wait(1);
  System.out.print("              ");
  showUserBank();
  System.out.print("              ");
  wait(1);
  System.out.println("MONEY IN POT: $" + pot);
  System.out.println("----------------------------------------------------------------------------------------------------------------------\n");
  wait(2);
  System.out.println("\nYou are Player " +(userPosition+1)); 
  wait(2);
  System.out.println("The first person to the left of the dealer, who has not folded will begin.\n\n");
  wait(1);
  return;
}

// accessors and mutators
  public static void addToPot(int amt){
    pot += amt;
  }

  public static void setCurrentbet(int amt){
    currentBet = amt;
  }

  public static int getCurrentbet(){
    return currentBet;
  }

  public static int getBigBet(){
    return bigBet;
  }

  public static boolean getGameOver(){
    return gameOver;
  }

  public static void setGameOver(boolean bool){
    gameOver = bool;
  }
  


  public void start(){
    clear();
    List<String> options = new ArrayList<String>();
    options.add("yes");
    options.add("no");
    String answer = getResponse("Do you want to see the directions?", options);
    if(answer.equals("yes")){
      getDirections();
    }
    for(int i=0; i<4; i++){
      clear();
      currentBet = -1;
      //make everyone's bet in round 0, and has checked/raised false
      for(int j=0; j<allPlayers.size(); j++){
        allPlayers.get(j).resetBetInRound();
        allPlayers.get(j).setHasChecked(false);
        allPlayers.get(j).setHasRaised(false);
      }
      displayNewRound(i);
      int position;
      if(i==0){position=3; /*if preflop, player 4 starts*/}
      else{position=1; /* otherwise, player 2 starts*/}
      for(int j=0; j<allPlayers.size(); j++){

        if(!allPlayers.get(position).getHasFolded()){
          allPlayers.get(position).play();
          wait(2);
          if(allPlayers.get(position).getHasRasied()){
            lastRaisedPosition = position;
          } else if(allPlayers.get(position).getHasChecked()){
            lastRaisedPosition = position+1;
          } else if(getGameOver() || hasAllFolded()){
            return;
          }
        }
        position++;
        if(position == allPlayers.size()){
          position = 0;
        }
      }
      if(lastRaisedPosition>-1){
        int startPos;
        if(i==0){startPos=3;}
        else{startPos=1;}
        int k;
        if(lastRaisedPosition>0){k=lastRaisedPosition;}
        else{k=allPlayers.size();}
        if(startPos!=k){
          System.out.println("\nTime to see if previous players want to match the raised bet or fold\n");
          wait(1);
        }
        while(startPos!= k){
          if(!allPlayers.get(startPos).getHasFolded()){
            allPlayers.get(startPos).matchOrFold();
            wait(2);
            if(getGameOver() || hasAllFolded()){
            return;
            }
          }

          startPos++;
          if(k!=allPlayers.size()){
            if(startPos==allPlayers.size()){
              startPos=0;
            }
          }
          
        }
        lastRaisedPosition = -1;
      }
      System.out.println("\n\nThe Round is Over.");
      pressEnter();


    }// big for 

    clear();
    System.out.println("");
    System.out.println("Time to reveal all the cards:\n-------------------------------------\n");
    showUserCards();
    showCommunity();
    System.out.println("\n");
    Player winner = findWinner();
    showCards();
    if(winner.getPlayerNum() == userPosition+1){
      System.out.println("\nYou have won with a "+handRanks[winner.getHandRank()]+ " Congratulations!");
      System.out.println("You have just earned $" + pot + " from the pot!");
    } else {
      System.out.println("\nPlayer "+ winner.getPlayerNum() + " has a "+handRanks[winner.getHandRank()]+ " and has won the $" +pot+" from the pot.\nBetter luck next time!");
    }
    return;


  }



}