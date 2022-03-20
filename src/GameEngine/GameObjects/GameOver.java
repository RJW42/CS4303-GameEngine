package GameEngine.GameObjects;


import GameEngine.Components.ScoreBoard;
import GameEngine.GameEngine;
import processing.core.PVector;


public class GameOver extends GameObject {
   // Attributes
   public boolean is_dead = false;
   public ScoreBoard score_board;


   // Constructor
   public GameOver(GameEngine sys, int score) {
      super(sys);

      // Init pos
      this.pos = new PVector(0, 0);

      // Add regular components
      this.score_board = new ScoreBoard(this, score);
      this.components.add(score_board);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
