package GameEngine.Components.MapEditorComponents.Tools;

import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PVector;

public enum Item {
   // Values
   WALL(Item::place_wall, "Wall"),
   AIR(Item::place_air, "Air"),
   NON_GRAPPLE_WALL(Item::place_non_grapple, "Non grapple wall"),
   MONSTER(Item::place_monster, "Monster"),
   PLAYER(Item::place_player, "Player"),
   BASIC_DOOR(Item::place_basic_door, "Basic Door"),
   KILL_DOOR(Item::place_kill_door, "Kill Door"),
   GOAL(Item::place_goal, "Goal");

   // Static attributes
   public static TerrainGenerator generator;
   public static GameEngine sys;
   public static int[] world;
   public static int[] tile_attributes;

   // Attributes
   public final Placer placer;
   public final String item_name;

   // Constructor
   Item(Placer placer, String item_name) {
      this.placer = placer;
      this.item_name = item_name;
   }


   // Class Methods


   // Value Methods
   public interface Placer {
      public void place(int world_index, float prev_x, float prev_y);
   }

   public static void place_wall(int world_index, float prev_x, float prev_y) {
      // Remove any entities from this position (e.g. player)
      check_entities(world_index);

      // Remove door if there is one
      check_door(world_index, (int) prev_x, (int) prev_y);

      world[world_index] = Terrain.WALL;
      tile_attributes[world_index] = Terrain.EMPTY;
   }


   public static void place_air(int world_index, float prev_x, float prev_y) {
      // Remove door if there is one
      check_door(world_index, (int) prev_x, (int) prev_y);

      world[world_index] = Terrain.AIR;
      tile_attributes[world_index] = Terrain.EMPTY;
   }


   public static void place_non_grapple(int world_index, float prev_x, float prev_y) {
      // Remove any entities from this position (e.g. player)
      check_entities(world_index);

      // Remove door if there is one
      check_door(world_index, (int) prev_x, (int) prev_y);

      world[world_index] = Terrain.WALL;
      tile_attributes[world_index] = Terrain.NON_GRAPPLE;
   }


   public static void place_monster(int world_index, float prev_x, float prev_y) {
      // Check if the position is valid
      if(position_not_air(prev_x, prev_y, world_index, MONSTER.item_name))
         return;

      PVector loc = new PVector(
              prev_x + Terrain.CELL_SIZE / 2f - Monster.COLLISION_WIDTH / 2f,
              prev_y + Monster.COLLISION_HEIGHT + 0.01f
      );

      generator.monster_spawn_locs.add(loc);
   }


   public static void place_player(int world_index, float prev_x, float prev_y) {
      // Check if the position is valid
      if(position_not_air(prev_x, prev_y, world_index, PLAYER.item_name))
         return;

      if(generator.player_spawn_loc == null)
         generator.player_spawn_loc = new PVector();

      generator.player_spawn_loc.x = prev_x + Terrain.CELL_SIZE / 2f - Player.COLLISION_WIDTH / 2f;
      generator.player_spawn_loc.y = prev_y + Player.COLLISION_HEIGHT + 0.01f;
   }


   public static void place_basic_door(int world_index, float prev_x, float prev_y) {
      place_door(world_index, prev_x, prev_y, Terrain.BASIC_DOOR_START);
   }


   public static void place_kill_door(int world_index, float prev_x, float prev_y) {
      place_door(world_index, prev_x, prev_y, Terrain.KILL_DOOR_START);
   }


   private static void place_door(int world_index, float prev_x, float prev_y, int type) {
      // Check space above
      if(prev_x <= 0 || prev_x >= Terrain.WIDTH - 1 || prev_y <= 0 || prev_y >= Terrain.WIDTH - 4){
         sys.warning_display.display_warning("Door must be placed inside world");
         return;
      }

      // Is space place door
      for(int i = 0; i < 3; i++){
         int index = generator.getIndex((int)prev_x, (int)prev_y + i);
         world[index] = Terrain.WALL;
         tile_attributes[index] = Terrain.DOOR_BODY;
      }
      tile_attributes[world_index] = type;
   }


   public static void place_goal(int world_index, float prev_x, float prev_y) {
      // Check if the position is valid
      if(position_not_air(prev_x, prev_y, world_index, GOAL.item_name))
         return;

      if(generator.goal_spawn_loc == null)
         generator.goal_spawn_loc = new PVector();

      generator.goal_spawn_loc.x = prev_x;
      generator.goal_spawn_loc.y = prev_y + 1;
   }


   // Value helper methods
   public static void check_entities(int world_index){
      // Check player and door
      PVector player_loc = generator.player_spawn_loc;
      PVector goal_loc = generator.goal_spawn_loc;

      if(player_loc != null && generator.getIndexFromWorldPos(player_loc.x, player_loc.y) == world_index){
         generator.player_spawn_loc = null;
      }
      if(goal_loc != null && generator.getIndexFromWorldPos(goal_loc.x + 0.1f, goal_loc.y - 0.1f) == world_index){
         generator.goal_spawn_loc = null;
      }
   }


   public static void check_door(int world_index, int prev_x, int prev_y){
      if(tile_attributes[world_index] != Terrain.DOOR_BODY && tile_attributes[world_index] != Terrain.BASIC_DOOR_START && tile_attributes[world_index] != Terrain.KILL_DOOR_START)
         return;

      // Does contain door remove it
      world[world_index] = Terrain.AIR;
      tile_attributes[world_index] = Terrain.AIR;

      check_door(generator.getIndex(prev_x, prev_y + 1), prev_x, prev_y);
      check_door(generator.getIndex(prev_x, prev_y - 1), prev_x, prev_y);
   }


   public static boolean position_not_air(float prev_x, float prev_y, int world_index, String item_name){
      // Check if the position is valid
      if(!position_valid(prev_x, prev_y, item_name))
         return true;

      // check that the position is free
      if(world[world_index] != Terrain.AIR){
         sys.warning_display.display_warning("Must place " + item_name + " in air");
         return true;
      }

      return false;
   }

   public static boolean position_valid(float prev_x, float prev_y, String item_name){
      if(prev_x <= 0  || prev_x >= Terrain.WIDTH - 1 || prev_y <= 0 || prev_y >= Terrain.HEIGHT - 1){
         sys.warning_display.display_warning("Cannot place " + item_name + " outside of the world");
         return false;
      }
      return true;
   }
}
