public class Npc extends Player{
  private int playerNum;
  
  public Npc(){
    super();
    playerNum = Player.getPlayerCount();
    Player.incrementPlayerCount();
  }

  public void play(){
    int rand = (int) (Math.random()*100) +1;
    if(this.getBank() < Game.getCurrentbet()){
      fold();
      return;
    }
    if(Game.getCurrentbet()>0){
      if (rand<=15){
        fold();
      }else if(rand<=25 && this.getBank() >= Game.getCurrentbet()*2){
        raise(Game.getCurrentbet()*2);
      }else {
        call();
      }
    } else if(Game.getCurrentbet()==0){
      if (rand<=15){
        fold();
      } else if(rand<=25 || this.getBank() < Game.getBigBet()){
        check();
      } else {
        raise(Game.getBigBet());
      }
    } else{
       if (rand<=15){
        fold();
      } else if(rand<=25 || this.getBank() < Game.getBigBet()){
        check();
      } else {
        betFirst(Game.getBigBet());
      }
    }
  }

  public void matchOrFold(){
    int rand = (int) (Math.random()*100) +1;
    if(rand <= 30 || this.getBank() < Game.getCurrentbet()){
      fold();
    } else {
      match();
    }
  }

  public void fold(){
    super.fold();
    System.out.println("Player "+playerNum+" has folded.");
  }

  public void call(){
    super.call();
    System.out.println("Player "+playerNum+" has called and added $"+ Game.getCurrentbet() + " to the pot");
  }

  public void check(){
    super.check();
    System.out.println("Player "+playerNum+" has checked");
  }

  public void match(){
    super.match();
    System.out.println("Player "+playerNum+" has matched the bet of $"+ (Game.getCurrentbet()));
  }

  public void raise(int amount){
    super.raise(amount);
    System.out.println("Player "+playerNum+" has raised the bet to $"+ Game.getCurrentbet());
  }

  public void betFirst(int amount){
    super.betFirst(amount);
    System.out.println("Player "+playerNum+" has bet $"+ amount);
  }

  public int getPlayerNum(){
    return playerNum;
  }


}