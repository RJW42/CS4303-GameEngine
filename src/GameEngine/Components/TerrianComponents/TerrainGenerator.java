package GameEngine.Components.TerrianComponents;

import GameEngine.Components.Component;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.Core.Door;
import GameEngine.GameObjects.Core.Goal;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public abstract class TerrainGenerator extends Component {
   // Attributes
   protected int width, height, seed;

   public PVector player_spawn_loc;
   public PVector goal_spawn_loc;
   public ArrayList<PVector> monster_spawn_locs;
   public ArrayList<Room> rooms;
   public ArrayList<Time> times;
   public Optional<ArrayList<String>> tips;

   // Constructor
   public TerrainGenerator(GameObject parent, int seed){
      super(parent);
      this.seed = seed;

      this.player_spawn_loc = new PVector(0,0);
      this.goal_spawn_loc = new PVector(0, 0);
      this.monster_spawn_locs = new ArrayList<>();
   }

   public int getWidth(){
      return width;
   }

   public int getHeight(){
      return height;
   }

   // Methods
   public abstract int[] createWorld();
   public abstract int[] getWorld();
   public abstract int[] getSpecialTiles();

   /* ***** Util Functions ***** */
   public void setConfig(int width, int height){
      this.height = height;
      this.width = width;
   }


   public int getIndex(int x, int y){
      return (y * width) + x;
   }

   public int getIndexFromWorldPos(float x, float y){
      x = x / Terrain.CELL_SIZE;
      y = y / Terrain.CELL_SIZE;
      return getIndex((int)x, (int)y);
   }

   public boolean validWalkCord(int x, int y){
      return x >= 0 && y >= 0 && x < width && y < height;
   }

   public PVector getPosFromIndex(int index) {
      return new PVector(index % width + (float)Terrain.CELL_SIZE/2f, index / width + (float)Terrain.CELL_SIZE/2f);
   }

   /* ***** Leaderboard ***** */
   public void add_time(String username, float time){
      times.add(new Time(time, username));
   }

   public ArrayList<Time> get_times(){
      return times;
   }


   /* ***** Goal and other entities ***** */
   public void spawn_goal(){
      sys.spawn(new Goal(sys, goal_spawn_loc), 1);
   }


   /* ***** Spawning / Monster functions ***** */
   public void spawn_monsters(){
      // Spawn all monsters whilst keeping track of what room they belong to
      if(rooms == null) init_rooms();

      for(PVector loc : monster_spawn_locs) {
         Monster monster = new Monster(sys, loc.copy());

         // Get the room of this monster
         int index = getIndexFromWorldPos(loc.x, loc.y);
         Room room = rooms.stream().filter(r -> r.points.contains(index)).findFirst().get();

         room.monsters.add(monster);
         sys.spawn(monster, 2);
      }
   }


   public void init_rooms(){
      // Get all rooms
      HashSet<Integer> visited = new HashSet<>();
      rooms = new ArrayList<>();

      for(int x = 0; x < width; x++){
         for(int y = 0; y < height; y++){
            int index = getIndex(x, y);

            if(getWorld()[index] != Terrain.AIR || visited.contains(index))
               continue;

            // Found new room
            Room room = new Room();
            add_to_rooms(x, y, visited, room);
            rooms.add(room);
         }
      }
   }


   /* ******** Room and door functions ********* */
   public void add_kill_door(Door door) {
      if(rooms == null)
         init_rooms();

      // Get the room for this door
      int index = getIndex(door.lower_x - 1, door.lower_y);
      Room room = get_room(index);

      if(room == null){
         index = getIndex(door.lower_x - 1, door.lower_y + 1);
         room = get_room(index);

         if(room == null){
            System.err.println("Failed to find room for door");
            return;
         }
      }

      // Found room add room to door
      room.doors.add(door);
   }


   public Room get_room(int index){
      return rooms.stream().filter(r -> r.points.contains(index)).findFirst().orElseGet(null);
   }


   private void add_to_rooms(int x, int y, HashSet<Integer> visited, Room room){
      // Check if valid
      if(!validWalkCord(x, y))
         return;

      int index = getIndex(x, y);

      if(getWorld()[index] != Terrain.AIR || visited.contains(index))
         return;

      visited.add(index);
      room.points.add(index);

      add_to_rooms(x - 1, y, visited, room);
      add_to_rooms(x + 1, y, visited, room);
      add_to_rooms(x, y - 1, visited, room);
      add_to_rooms(x, y + 1, visited, room);
   }


   public interface TerrainSupplier {
      public TerrainGenerator get(GameObject parent, int seed);
   }

   public static class Room {
      public final HashSet<Integer> points;
      public final ArrayList<Monster> monsters;
      public final ArrayList<Door> doors;

      public Room() {
         points = new HashSet<>();
         monsters = new ArrayList<>();
         doors = new ArrayList<>();
      }
   }


   public static class Time implements Comparable<Time> {
      public final String username;
      public float time;

      public Time(float time, String username) {
         this.time = time;
         this.username = username;
      }

      @Override
      public int compareTo(Time o) {
         return Float.compare(time, o.time);
      }
   }
}
