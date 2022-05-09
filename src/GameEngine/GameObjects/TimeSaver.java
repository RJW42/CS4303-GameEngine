package GameEngine.GameObjects;


import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.Components.UIComponents.UIInput;
import GameEngine.GameEngine;
import GameEngine.Levels.GameOver;
import processing.core.PVector;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static GameEngine.Components.TerrianComponents.TerrainLoader.SAVE_LOC;
import static GameEngine.GameObjects.MainMenu.MenuSelector.*;


public class TimeSaver extends GameObject {
   // Attributes
   public static final int MAX_USERNAME_LENGTH = 10;

   public boolean is_dead = false;

   private final LoadedTerrainGenerator generator;
   private final String file_name;
   private final float time;


   // Constructor
   public TimeSaver(GameEngine sys, LoadedTerrainGenerator generator, String file_name, String user_name, float time) {
      super(sys);

      // Init attributes
      this.generator = generator;
      this.time = time;
      this.file_name = file_name;

      // Create input
      if(user_name == null) {
         UIInput username_input = new UIInput(this,
                 this::username_callback,"Enter Username: ", "",
                 new PVector(GameEngine.SCREEN_WIDTH /2f, GameEngine.SCREEN_HEIGHT /2f),
                 TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, PADDING, BORDER_WIDTH,
                 WIDTH * 2, HEIGHT * 2, MAX_USERNAME_LENGTH, BUTTON_ALPHA
         );

         this.components.add(username_input);
      } else {
         username_callback(user_name);
      }
   }


   // Methods
   private void username_callback(String value){
      // Add time and username to generator
      generator.times.stream().filter(
         entry -> entry.username.equals(value)
      ).findFirst().ifPresentOrElse(
         // User already completed this map, update time
         entry -> entry.time = Math.min(entry.time, this.time),

         // User not completed this map, create new entry
         () -> generator.times.add(new TerrainGenerator.Time(this.time, value))
      );

      // Update the save
      TerrainLoader.saveTerrain(generator, generator.parent.getComponent(TerrainRenderer.class), file_name);

      // Continue to leaderboard
      is_dead = true;
      ((GameOver)sys.level_manager.getCurrentLevel()).user_name = value;
      sys.spawn(new LeaderBoard(sys, file_name, generator), 2);
   }


   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
