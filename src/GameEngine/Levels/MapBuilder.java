package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.*;
import GameEngine.GameObjects.*;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.Random;


public class MapBuilder extends Level{
   // Attributes
   private int map_width;
   private int map_height;
   private String name;

   // Constructor
   public MapBuilder(GameEngine sys, int map_width, int map_height, String name) {
      super(sys);

      // Init attributes
      this.map_width = map_width;
      this.map_height = map_height;
      this.name = name;
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }

   public void start() {
      // Init world
      sys.initWorld(map_width, map_height);

      // Create terrian
      int seed = new Random().nextInt();
      System.out.println("Terrain seed: " + seed);

      Terrain terrain = new Terrain(sys, seed, MapBuilderTerrainGenerator::new);

      TerrainGenerator generator = terrain.getComponent(TerrainGenerator.class);
      generator.setConfig(map_width, map_height);
      generator.createWorld();

      sys.terrain = terrain;
      sys.spawn(terrain, 0);

      // Spawn the map editor object and set the view mode to chase
      MapEditor controller = new MapEditor(sys, new PVector(map_height / 2f, map_height / 2f));

      sys.chase_object = controller;
      sys.spawn(controller, 3);
   }

   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }
}
