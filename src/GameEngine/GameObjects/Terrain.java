package GameEngine.GameObjects;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.TerrianComponents.*;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class Terrain extends GameObject implements Collideable {
   // Attributes
   public static final int WALL = 1;
   public static final int AIR = 0;
   public static int WIDTH = GameEngine.WORLD_WIDTH;
   public static int HEIGHT = GameEngine.WORLD_HEIGHT;
   public static int CELL_SIZE = 1;

   public int[] world;
   public ArrayList<BaseCollisionComponent> collision_components;

   public final int seed;


   // Constructor
   public Terrain(GameEngine sys){
      this(sys, new Random().nextInt(), new TerrainGenerator.TerrainSupplier() {
         @Override
         public TerrainGenerator get(GameObject parent, int seed) {
            return new BasicTerrainGenerator(parent, seed);
         }
      });
   }

   public Terrain(GameEngine sys, int seed, TerrainGenerator.TerrainSupplier terrain_generator) {
      super(sys);
      this.seed = seed;

      // Debugging information
      System.out.println("Terrain Seed: " + seed);

      // Init variables
      this.collision_components = new ArrayList<>();
      this.pos = new PVector(0, 0);

      // Add components
      this.components.add(new TerrainRenderer(this));
      this.components.add(new TerrainCollider(this));
      this.components.add(terrain_generator.get(this, seed));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return false;
   }

   @Override
   public List<BaseCollisionComponent> getCollisionComponents() {
      return collision_components;
   }

   @Override
   public BaseCollisionComponent getCollisionComponent() {
      return collision_components.get(0);
   }
}
