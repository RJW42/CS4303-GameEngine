package GameEngine;

public interface ScoreManager {
   // Methods
   public void savedHuman(int score);
   public void killedNPC(Class type);
   public int getScore();
   public int getLevel();
}
