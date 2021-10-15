 import java.util.List;
import java.util.ArrayList;

public class User extends Player{
  private int playerNum;
  
  public User(int position){
    super();
    playerNum = position;
    Player.incrementPlayerCount();
  }

  public void play(){
    System.out.println("\nIt's your turn\n");
    List<String> options = new ArrayList<String>();
      options.add("call");
      options.add("fold");
      options.add("raise");
      options.add("check");
    if(this.getBank() < Game.getCurrentbet()){
      System.out.println("\nYou don't have enough money to keep playing, you must fold.");
      fold();
    } else if(Game.getCurrentbet() < 0){
      options.remove("call");
      options.remove("raise");
      options.add("bet");
      String answer = Game.getResponse("\nDo you want to check, fold or bet?", options);
      if(answer.equals("check")){
        check();
      } else if(answer.equals("fold")){
        fold();
      } else {
        int answer2 = Game.getResponseBetween("\nHow much do you want to bet?", 1, this.getBank());
        betFirst(answer2);
      }
    }else if(Game.getCurrentbet() == 0){
      options.remove("call");
      String answer = Game.getResponse("\nDo you want to check, fold or raise?", options);
      if(answer.equals("check")){
        check();
      } else if(answer.equals("fold")){
        fold();
      } else {
        raise();
      }
    }else{
      if(this.getBank() < Game.getCurrentbet()*2){
        options.remove("check");
        options.remove("raise");
        String answer = Game.getResponse("\nDo you want to call or fold?", options);
        if(answer.equals("call")){
          call();
        } else if(answer.equals("fold")){
          fold();
        }
      } else {
        options.remove("check");
        String answer = Game.getResponse("\nDo you want to call, fold or raise?", options);
        if(answer.equals("call")){
          call();
        } else if(answer.equals("fold")){
          fold();
        } else {
          raise();
        }
      }  
    }
  }

  public void raise(){
    if(Game.getCurrentbet() == 0){
      int answer = Game.getResponseBetween("\nHow much do you want to bet?", 1, this.getBank());
      super.raise(answer);
      System.out.println("You have added $"+ answer + " to the pot\n");
    } else{
      int answer = Game.getResponseBetween("\nHow much do you want to raise?", Game.getCurrentbet()*2, this.getBank());
      super.raise(answer);
      System.out.println("You have added $"+ answer + " to the pot\n");
    }  
  }

  public void matchOrFold(){
    if(this.getBank() < Game.getCurrentbet()){
      System.out.println("\nYou don't have enough money match, you must fold.");
      fold();
    } else {
      List<String> options = new ArrayList<String>();
      options.add("match");
      options.add("fold");
      String answer = Game.getResponse("\nDo you want to match the current bet or fold?", options);
      if (answer.equals("match")){
        match();
      } else{
        fold();
      }
    }
  }

  public void fold(){
    super.fold();
    System.out.println("You have folded. Game Over.\n");
    Game.setGameOver(true);
  }

  public void betFirst(int amount){
    super.betFirst(amount);
    System.out.println("You have bet $"+ Game.getCurrentbet()+ "\n");
  }

  public void call(){
    super.call();
    System.out.println("You have added $"+ Game.getCurrentbet() + " to the pot\n");
  }

  public void check(){
    super.check();
    System.out.println("You have checked\n");
  }

  public void match(){
    super.match();
    System.out.println("You have matched the bet of $"+ Game.getCurrentbet());
  }

  public int getPlayerNum(){
    return playerNum;
  }


  



}