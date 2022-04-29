package GameEngine.GameObjects.Core;


import GameEngine.Components.AIComponents.AIMovement.AIPathManager;
import GameEngine.Components.AIComponents.KillDoorManager;
import GameEngine.Components.AIComponents.Timer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;


public class Director extends GameObject {
   // Attributes
   public boolean is_dead = false;
   public Player player;
   public Timer timer;


   // Constructor
   public Director(GameEngine sys, Player player) {
      super(sys);

      // Init attributes
      this.player = player;
      this.timer = new Timer(this, player);

      // Add regular components 
      this.components.add(new AIPathManager(this));
      this.components.add(new KillDoorManager(this));
      this.components.add(timer);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
