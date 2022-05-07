package GameEngine.Components.TerrianComponents;

import org.json.JSONArray;
import org.json.JSONObject;
import processing.core.PVector;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class TerrainLoader {
   private TerrainLoader(){} // Util class for writing and loading maps from file

   public static final String SAVE_LOC      = "./GameEngine/Resources/Levels/";

   // Core Properties
   private static final String WORLD_WIDTH   = "world_width";
   private static final String WORLD_HEIGHT  = "world_height";
   private static final String SPAWN_X       = "spawn_x";
   private static final String SPAWN_Y       = "spawn_y";
   private static final String WORLD_ARR     = "world";
   private static final String MONSTERS_SPAWN= "monsters";
   private static final String AIR_CLR       = "air_colour";
   private static final String BORDER_CLR    = "border_colour";
   private static final String WALL_CLR      = "wall_colour";
   private static final String GOAL_X        = "goal_x";
   private static final String GOAL_Y        = "goal_y";
   private static final String LEADERBOARD   = "leaderboard";
   private static final String TIPS          = "tips";

   // Monster spawn properties
   private static final String MONSTER_X     = "x";
   private static final String MONSTER_Y     = "y";

   // World array element properties
   private static final String WORLD_VAL     = "value";
   private static final String VAL_ATTRIBUTE = "modifier";

   // RGB properties
   private static final String COLOUR_R      = "r";
   private static final String COLOUR_G      = "g";
   private static final String COLOUR_B      = "b";

   // leaderboard properties
   private static final String USERNAME      = "username";
   private static final String TIME          = "time";


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
    * wall_colour: {r: int, g: int, b: int}
    * air_colour: {r: int, g: int, b: int}
    * border_colour: {r: int, g: int, b: int}
    * scoreboard: [
    *    {
    *       username: str,
    *       time: float
    *    }
    * ],
    * tips: [str]
    */

   public static void loadTerrain(LoadedTerrainGenerator generator, TerrainRenderer renderer, String file_name){
      // Load file
      String terrain_string = read_file(file_name);
      JSONObject terrain_data = new JSONObject(terrain_string);

      // Populate the generator data
      generator.setConfig(terrain_data.getInt(WORLD_WIDTH), terrain_data.getInt(WORLD_HEIGHT));
      generator.player_spawn_loc.x = terrain_data.getFloat(SPAWN_X);
      generator.player_spawn_loc.y = terrain_data.getFloat(SPAWN_Y);
      generator.goal_spawn_loc.x = terrain_data.getFloat(GOAL_X);
      generator.goal_spawn_loc.y = terrain_data.getFloat(GOAL_Y);
      generator.width = terrain_data.getInt(WORLD_WIDTH);
      generator.height = terrain_data.getInt(WORLD_HEIGHT);

      renderer.wall_colour = rgb_from_JSON(terrain_data.getJSONObject(WALL_CLR));
      renderer.air_colour = rgb_from_JSON(terrain_data.getJSONObject(AIR_CLR));
      renderer.border_colour = rgb_from_JSON(terrain_data.getJSONObject(BORDER_CLR));

      // Add complex elements
      world_from_json(generator, terrain_data.getJSONArray(WORLD_ARR));
      monsters_from_JSON(generator, terrain_data.getJSONArray(MONSTERS_SPAWN));
      leaderboard_from_JSON(generator, terrain_data.getJSONArray(LEADERBOARD));

      if(terrain_data.has(TIPS))
         tips_from_JSON(generator, terrain_data.getJSONArray(TIPS));
      else
         generator.tips = Optional.empty();
   }


   public static void saveTerrain(TerrainGenerator generator, TerrainRenderer renderer, String file_name){
      // Create json object to represent terrain
      JSONObject core = new JSONObject();

      // Add Basic attributes
      core.put(WORLD_WIDTH, generator.width);
      core.put(WORLD_HEIGHT, generator.height);
      core.put(SPAWN_X, generator.player_spawn_loc.x);
      core.put(SPAWN_Y, generator.player_spawn_loc.y);
      core.put(GOAL_X, generator.goal_spawn_loc.x);
      core.put(GOAL_Y, generator.goal_spawn_loc.y);

      // Add complex elements
      core.put(WORLD_ARR, world_to_JSON(generator));
      core.put(MONSTERS_SPAWN, monsters_to_JSON(generator));
      core.put(WALL_CLR, rgb_to_JSON(renderer.wall_colour));
      core.put(AIR_CLR, rgb_to_JSON(renderer.air_colour));
      core.put(BORDER_CLR, rgb_to_JSON(renderer.border_colour));
      core.put(LEADERBOARD, leaderboard_to_json(generator));
      core.put(TIPS, tips_to_json(generator));

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

   // ******** Leaderboard ********* //
   private static JSONArray leaderboard_to_json(TerrainGenerator generator){
      JSONArray leaderboard = new JSONArray();

      for(TerrainGenerator.Time time : generator.get_times()){
         JSONObject object = new JSONObject();

         object.put(USERNAME, time.username);
         object.put(TIME, time.time);

         leaderboard.put(object);
      }

      return leaderboard;
   }

   private static void leaderboard_from_JSON(TerrainGenerator generator, JSONArray times_json){
      ArrayList<TerrainGenerator.Time> times = new ArrayList<>();

      for(int i = 0; i < times_json.length(); i++){
         JSONObject time = times_json.getJSONObject(i);

         times.add(new TerrainGenerator.Time(
                 time.getFloat(TIME),
                 time.getString(USERNAME)
         ));
      }

      generator.times = times;
   }

   // ******** Tips ******** //
   private static JSONArray tips_to_json(TerrainGenerator generator){
      // Check if generator is has tips
      if(generator.tips.isPresent())
         return new JSONArray();

      // Contains tips
      JSONArray out = new JSONArray();
      for(var tip : generator.tips.get())
         out.put(tip);
      return out;
   }

   private static void tips_from_JSON(TerrainGenerator generator, JSONArray tips){
      // Init output array
      ArrayList<String> out = new ArrayList<>();

      // Create tips array
      for(int i = 0; i < tips.length(); i++){
         out.add(tips.getString(i));
      }

      // Output tips
      if(out.size() == 0) generator.tips = Optional.empty();
      else generator.tips = Optional.of(out);
   }

   // ******** RGB ********* //
   private static JSONObject rgb_to_JSON(PVector colour){
      JSONObject out = new JSONObject();

      out.put(COLOUR_R, (int)colour.x);
      out.put(COLOUR_G, (int)colour.y);
      out.put(COLOUR_B, (int)colour.z);

      return out;
   }


   private static PVector rgb_from_JSON(JSONObject colour_json){
      PVector out = new PVector();

      out.x = colour_json.getInt(COLOUR_R);
      out.y = colour_json.getInt(COLOUR_G);
      out.z = colour_json.getInt(COLOUR_B);

      return out;
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
