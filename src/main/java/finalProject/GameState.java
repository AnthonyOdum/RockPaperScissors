package finalProject;

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
        System.out.println("Game is full!");
      }
   }
   
   public void setP1Guess(int num) {
      p1guess = num;
   }
   
   public void setP2Guess(int num) {
      p2guess = num;
   }
}
   