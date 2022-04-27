package GameEngine.Components.TerrianComponents;

import org.json.JSONArray;
import org.json.JSONObject;
import processing.core.PVector;

import java.io.*;

public class TerrainLoader {
   private TerrainLoader(){} // Util class for writing and loading maps from file

   public static final String SAVE_LOC      = "./GameEngine/Resources/Levels/";

   // Core Properties
   private static final String WORLD_WIDTH   = "world-width";
   private static final String WORLD_HEIGHT  = "world-height";
   private static final String SPAWN_X       = "spawn-x";
   private static final String SPAWN_Y       = "spawn-y";
   private static final String WORLD_ARR     = "world";
   private static final String MONSTERS_SPAWN= "monsters";

   // Monster spawn properties
   private static final String MONSTER_X     = "x";
   private static final String MONSTER_Y     = "y";

   // World array element properties
   private static final String WORLD_VAL     = "value";
   private static final String VAL_ATTRIBUTE = "modifier";


   /* ***** JSON Layout *****
    * world-width: int
    * world-height: int
    * spawn-x: int
    * spawn-y: int
    * goal-x: int
    * goal-y: int
    * world: [
    *    {
    *       value: int either: 0 Air, 1 Wall
    *       modifier: int either: 0 none, 1 non grapple,
    *    }
    * ]
    * monsters: [
    *    {
    *       x: int,
    *       y: int
    *    }
    * ]
    */

   public static void loadTerrain(LoadedTerrainGenerator generator, String file_name){
      // Load file
      String terrain_string = read_file(file_name);
      // Todo: check if string null then throw error or something

      JSONObject terrain_data = new JSONObject(terrain_string);

      // Populate the generator data
      generator.setConfig(terrain_data.getInt(WORLD_WIDTH), terrain_data.getInt(WORLD_HEIGHT));
      generator.player_spawn_loc.x = terrain_data.getFloat(SPAWN_X);
      generator.player_spawn_loc.y = terrain_data.getFloat(SPAWN_Y);
      generator.width = terrain_data.getInt(WORLD_WIDTH);
      generator.height = terrain_data.getInt(WORLD_HEIGHT);

      // Add complex elements
      world_from_json(generator, terrain_data.getJSONArray(WORLD_ARR));
      monsters_from_JSON(generator, terrain_data.getJSONArray(MONSTERS_SPAWN));
   }


   public static void saveTerrain(TerrainGenerator generator, String file_name){
      // Todo: Items to save
      //        - goal loc
      //        - enemy spawn locations
      //        - lava / some other bad liquid
      //        - doors
      //        - nests/vents maybe

      // Create json object to represent terrain
      JSONObject core = new JSONObject();

      // Add Basic attributes
      core.put(WORLD_WIDTH, generator.width);
      core.put(WORLD_HEIGHT, generator.height);
      core.put(SPAWN_X, generator.player_spawn_loc.x);
      core.put(SPAWN_Y, generator.player_spawn_loc.y);
      // Todo: finish this

      // Add complex elements
      core.put(WORLD_ARR, world_to_JSON(generator));
      core.put(MONSTERS_SPAWN, monsters_to_JSON(generator));

      // Write to disk
      write_file(core, file_name);
   }


   // ********* World ********* //
   private static JSONArray world_to_JSON(TerrainGenerator generator){
      // Parse the world array into a json array
      JSONArray world_json = new JSONArray();

      int[] world = generator.getWorld();
      int[] tile_attribute = generator.getSpecialTiles();

      for(int i = 0; i < world.length; i++){
         JSONObject world_element = new JSONObject();

         world_element.put(WORLD_VAL, world[i]);
         world_element.put(VAL_ATTRIBUTE, tile_attribute[i]);

         world_json.put(world_element);
      }

      return world_json;
   }


   private static void world_from_json(LoadedTerrainGenerator generator, JSONArray world_json){
      // Init world and populate
      int[] world = new int[generator.width * generator.height];
      int[] tile_attribute = new int[generator.width * generator.height];

      for(int i = 0; i < world_json.length(); i++){
         JSONObject world_element = world_json.getJSONObject(i);

         world[i] = world_element.getInt(WORLD_VAL);
         tile_attribute[i] = world_element.getInt(VAL_ATTRIBUTE);
      }

      generator.world = world;
      generator.special_tiles = tile_attribute;
   }


   // ********* Monsters ********* //
   private static JSONArray monsters_to_JSON(TerrainGenerator generator) {
      JSONArray monsters = new JSONArray();

      for(PVector monster : generator.monster_spawn_locs) {
         JSONObject monster_json = new JSONObject();

         monster_json.put(MONSTER_X, monster.x);
         monster_json.put(MONSTER_Y, monster.y);

         monsters.put(monster_json);
      }

      return monsters;
   }


   private static void monsters_from_JSON(LoadedTerrainGenerator generator, JSONArray monsters_json){
      for(int i = 0; i < monsters_json.length(); i++) {
         JSONObject monster_json = monsters_json.getJSONObject(i);

         PVector pos = new PVector(
            monster_json.getFloat(MONSTER_X),
            monster_json.getFloat(MONSTER_Y)
         );

         generator.monster_spawn_locs.add(pos);
      }
   }


   // ********* Util ********* //
   private static String read_file(String file_name){
      // Load file contents into string builder
      StringBuilder res = new StringBuilder();

      try(BufferedReader br = new BufferedReader(new FileReader(SAVE_LOC + file_name))){
         String line;
         while((line = br.readLine()) != null)
            res.append(line);
      }catch (IOException e){
         System.err.println(" - Failed to read terrain file: " + e.getMessage());
         System.exit(0);
         return null;
      }

      return res.toString();
   }


   private static void write_file(JSONObject content, String file_name){
      // Write the json object to the given file
      try(BufferedWriter bw = new BufferedWriter(new FileWriter(SAVE_LOC + file_name))){
         bw.write(content.toString());
      }catch (IOException e){
         System.err.println(" - Failed to write terrain file: " + e.getMessage());
         System.exit(0);
      }
   }
}
