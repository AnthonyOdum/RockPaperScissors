package finalProject;

import java.util.HashMap;

class GameState {
  String name;
  Player p1;
  Player p2;
  int p1guess;
  int p2guess;
   
  public GameState(String n) {
    name = n;
  }
   
  public String getName(){
    return name;
  }
   
  public void addPlayer(Player p) {
    if (p1 == null) {
      p1 = p;
    } else if (p2 == null) {
      p2 = p;
    } else {
      throw new IllegalArgumentException("Game is full");
    }
  }
   
  public void setP1Guess(int num) {
    if (p1guess != 0) {
      throw new IllegalArgumentException("Player 1 already guessed");
    }
    p1guess = num;
  }
   
  public void setP2Guess(int num) {
    if (p2guess != 0) {
      throw new IllegalArgumentException("Player 2 already guessed");
    }
    p2guess = num;
  }

  public String getWinner() {
    if (p1guess == 0 || p2guess == 0) {
      return null;
    }

    if (p1guess == 1 && p2guess == 2) {
      return p2.getName();
    } else if (p1guess == 1 && p2guess == 3) {
      return p1.getName();
    } else if (p1guess == 2 && p2guess == 1) {
      return p1.getName();
    } else if (p1guess == 2 && p2guess == 3) {
      return p2.getName();
    } else if (p1guess == 3 && p2guess == 1) {
      return p2.getName();
    } else if (p1guess == 3 && p2guess == 2) {
      return p1.getName();
    } else {
      return "Draw";
    }
  }
}
   