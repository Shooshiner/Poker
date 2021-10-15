class Main {
  public static void main(String[] args) {
    int numNPCs = Game.getResponseBetween("\n\nHello! How many people (excluding yourself) would you like to play Poker with? 3-20", 3, 20);
    int bigBet = Game.getResponseBetween("What do you want the big bet to be? 10-300", 10, 300);
    Game newGame = new Game(numNPCs, bigBet);
    newGame.start();
  }
}