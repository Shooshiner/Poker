import java.util.List;
import java.util.ArrayList;

public abstract class Deck{

  public static List<Card> deckOfCards = new ArrayList<Card>();

  public static void initializeDeck(){
    for (int i=0; i<4; i++){
      String suit="";
      if(i ==0){
        suit = "spades";
      } else if(i == 1){
        suit = "diamonds";
      } else if(i == 2){
        suit = "hearts";
      } else {
        suit = "clubs";
      }
      for (int j=2; j<=14; j++){
        String value="";
        if (j<11){
          value = Integer.toString(j);
        } else if (j==11){
          value = "jack";
        } else if (j==12){
          value = "queen";
        } else if (j==13){
          value = "king";
        } else{
          value = "ace";
        }
        Card newCard = new Card(suit, value, j);
        deckOfCards.add(newCard);
      }
    }
  }

  public static Card draw(){
    int rand = (int) (Math.random()*deckOfCards.size()-1);
    Card toReturn = deckOfCards.get(rand);
    deckOfCards.remove(rand);
    return toReturn;
  }

  public static void burn(){
    int rand = (int) (Math.random()*deckOfCards.size()-1) +1;
    deckOfCards.remove(rand);
  }

}