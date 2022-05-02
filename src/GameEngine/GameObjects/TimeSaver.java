package GameEngine.GameObjects;


import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameEngine;


public class TimeSaver extends GameObject {
   // Attributes
   public boolean is_dead = false;

   private final LoadedTerrainGenerator generator;
   private final float time;


   // Constructor
   public TimeSaver(GameEngine sys, LoadedTerrainGenerator generator, float time) {
      super(sys);

      // Init attributes
      this.generator = generator;
      this.time = time;

      // Create text box

   }


   // Methods
   private void username_callback(String value){
      System.out.println(value);
   }


   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
