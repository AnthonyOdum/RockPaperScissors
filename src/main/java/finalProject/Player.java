package finalProject;

class Player {
  String name;
  int iD;
   
  public Player (String n,int i) {
    name = n;
    iD = i;
  }
   
  public int getID(){
    return iD;
  }

  public String getName(){
    return name;
  }
}