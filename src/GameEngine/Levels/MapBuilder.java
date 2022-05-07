package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.*;
import GameEngine.GameObjects.*;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;


public class MapBuilder extends Level{
   // Attributes
   public static String[] BACKGROUND_MUSIC = new String[] {"map_editor_1", "map_editor_2"};
   public static String name;

   private int map_width;
   private int map_height;

   public Level advance;

   // Constructor
   public MapBuilder(GameEngine sys, int map_width, int map_height, String name) {
      super(sys);

      // Init attributes
      this.map_width = map_width;
      this.map_height = map_height;
      this.advance = null;
      MapBuilder.name = name;
   }

   // Methods
   public void drawBackground(){
      sys.background(127, 159, 159);
   }

   public void start() {
      // Init world
      sys.audio_manager.start_background_music(BACKGROUND_MUSIC);
      sys.initWorld(map_width, map_height);

      // Create terrian
      int seed = new Random().nextInt();
      System.out.println("Terrain seed: " + seed);

      Terrain terrain = new Terrain(sys, seed, MapEditorTerrainGenerator::new);

      TerrainGenerator generator = terrain.getComponent(TerrainGenerator.class);
      generator.setConfig(map_width, map_height);
      generator.createWorld();

      sys.terrain = terrain;
      sys.spawn(terrain, 0);

      // Spawn the map editor object and set the view mode to chase
      MapEditor controller = new MapEditor(sys, new PVector(map_width / 2f, map_height / 2f));

      sys.chase_position = controller.pos;
      sys.spawn(controller, 3);
   }

   public boolean updateAndCanAdvance() {
      sys.audio_manager.update_background_music();
      return advance != null;
   }

   public Level advance() {
      sys.audio_manager.cancel_background_music();
      return advance;
   }
}
