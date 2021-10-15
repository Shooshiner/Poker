public class Card{
  private String suit;
  private String value;
  private int rank;

  public Card(String suit, String value, int rank){
    this.suit = suit;
    this.value = value;
    this.rank = rank;
  }

  public String getSuit(){
    return suit;
  }

  public String getValue(){
    return value;
  }

  public int getRank(){
    return rank;
  }

  public String toString(){
    if(rank == 8 || rank == 14){
      return "an " + value + " of " + suit;
    }else{
      return "a " + value + " of " + suit;
    }
  }

}