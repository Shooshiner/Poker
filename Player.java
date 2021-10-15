 import java.util.List;
import java.util.ArrayList;

public class Player{
  private int betInRound;
  private int bank;
  private boolean hasFolded;
  private boolean hasChecked;
  private boolean hasRaised;
  private int handRank;
  private static int playerCount = 1;
  private List<Card> hand = new ArrayList<Card>();

  public Player(){
    betInRound = 0;
    bank = (int) (Math.random()*4001) +1000;
    hasFolded = false;
    hasRaised = false;
    hand.add(Deck.draw());
    hand.add(Deck.draw());
    // will change if not folded
    handRank =100;
  }


  public void play(){}

  public void fold(){
    hasFolded = true;
  }

  public void raise(int amount){
    hasRaised = true;
    bank -= amount;
    Game.addToPot(amount);
    betInRound = amount;
    Game.setCurrentbet(amount);
  }

  public void check(){
    hasChecked = true;
    betInRound = 0;
  }

  public void call(){
    bank -= Game.getCurrentbet();
    Game.addToPot(Game.getCurrentbet());
    betInRound = Game.getCurrentbet();
  }

  public void matchOrFold(){}

  public void match(){
    bank -= (Game.getCurrentbet() - betInRound);
    Game.addToPot(Game.getCurrentbet() - betInRound);
    betInRound = Game.getCurrentbet();
  }

  public void betFirst(int amount){
    bank -= amount;
    Game.addToPot(amount);
    betInRound = amount;
    Game.setCurrentbet(amount);
  }


  //accessors + mutators
  public int getBank(){
    return bank;
  }

  public int getBetInRound(){
    return betInRound;
  }
  

  public void resetBetInRound(){
    betInRound = 0;
  }

  public boolean getHasFolded(){
    return hasFolded;
  }

  public boolean getHasRasied(){
    return hasRaised;
  }

  public void setHasRaised(boolean bool){
    hasRaised = bool;
  }

  public boolean getHasChecked(){
    return hasChecked;
  }

  public void setHasChecked(boolean bool){
    hasChecked = bool;
  }

  public List<Card> getHand(){
    return hand;
  }

  public int getHandRank(){
    return handRank;
  }

  public void setHandRank(int rank){
    handRank = rank;
  }

  public int getPlayerNum(){return 0;}

  public static int getPlayerCount(){
    return playerCount;
  }

  public static void incrementPlayerCount(){
    playerCount++;
  }





}