package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.TerrianComponents.DoorRenderer;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PConstants;
import processing.core.PVector;


public class ItemPlace extends Tool {
   // Attributes
   private ItemSelect item_select;
   private TileSelector tile_selector;
   private InputManager.Key click;

   private TerrainGenerator generator;
   private TerrainRenderer renderer;

   private int prev_x;
   private int prev_y;

   // Constructor
   public ItemPlace(GameObject parent) {
      super(parent);

      // Init attributes
      this.icon_text = "PL";
   }


   // Methods 
   public void start() {
      this.item_select = parent.getComponent(ItemSelect.class);
      this.tile_selector = parent.getComponent(TileSelector.class);
      this.click = sys.input_manager.mouse_click;

      this.generator = sys.terrain.getComponent(TerrainGenerator.class);
      this.renderer = sys.terrain.getComponent(TerrainRenderer.class);

      generator.player_spawn_loc = null;
   }

   public void update() {
      // Check if should place tile
      if(!active){
         prev_x = -1;
         prev_y = -1;
         return;
      }

      if(menu.mouse_hovering || !click.pressed || (prev_x == tile_selector.current_x && prev_y == tile_selector.current_y))
         return;

      // Can place tile
      place_tile();
   }


   public void draw() {
      display_player();
      display_monsters();
      display_doors();
   }


   private void display_player(){
      // Check if spawn location set
      if(generator.player_spawn_loc == null)
         return;

      // Renderer player
      // Todo: change this when proper player model drawn
      sys.noStroke();
      sys.fill(Player.COLOUR.x, Player.COLOUR.y, Player.COLOUR.z);
      sys.rectMode(PConstants.CORNER);
      sys.rect(generator.player_spawn_loc.x, generator.player_spawn_loc.y, Player.COLLISION_WIDTH, -Player.COLLISION_HEIGHT);
   }


   private void display_monsters(){
      // Draw each monster
      generator.monster_spawn_locs.forEach(monster -> {
         // Render monster
         // Todo: chane this when proper monster model drawn
         sys.noStroke();
         sys.fill(Monster.COLOUR.x, Monster.COLOUR.y, Monster.COLOUR.z);
         sys.rectMode(PConstants.CORNER);
         sys.rect(monster.x, monster.y, Monster.COLLISION_WIDTH, -Monster.COLLISION_HEIGHT);
      });
   }

   private void display_doors(){
      // Draw each door
      int[] world = generator.getSpecialTiles();

      // Todo: change this when proper door model drawn
      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Check if door
            if(world[generator.getIndex(x, y)] != Terrain.DOOR_START)
               continue;

            // Render door
            sys.stroke(DoorRenderer.COLOUR.x, DoorRenderer.COLOUR.y, DoorRenderer.COLOUR.z);
            sys.strokeWeight(0.1f);
            sys.noFill();
            sys.rect(x, y, Terrain.CELL_SIZE, Terrain.CELL_SIZE * 3);
         }
      }
   }


   private void place_tile(){
      // Get location to place
      prev_x = tile_selector.current_x;
      prev_y = tile_selector.current_y;
      int world_index = generator.getIndex(prev_x, prev_y);

      int[] world = generator.getWorld();
      int[] tile_attributes = generator.getSpecialTiles();

      switch (item_select.current_item){
         case WALL:
            place_wall(world, tile_attributes, world_index);
            break;
         case AIR:
            place_air(world, tile_attributes, world_index);
            break;
         case NON_GRAPPLE_WALL:
            place_non_grapple(world, tile_attributes, world_index);
            break;
         case PLAYER:
            place_player(world, world_index);
            break;
         case MONSTER:
            place_monster(world, world_index);
            break;
         case DOOR:
            place_door(world, tile_attributes, world_index);
            break;
         default:
            System.err.println("Unreachable point reached error in ItemPlace.java");
            System.exit(0);
      }

      renderer.reset();
   }


   private void place_player(int[] world, int world_index){
      // Check if the position is valid
      if(prev_x <= 0  || prev_x >= Terrain.WIDTH - 1 || prev_y <= 0 || prev_y >= Terrain.HEIGHT - 1){
         sys.warning_display.display_warning("Cannot place player outside of the world");
         return;
      }

      if(world[world_index] != Terrain.AIR){
         sys.warning_display.display_warning("Must place player in air");
         return;
      }

      if(generator.player_spawn_loc == null)
         generator.player_spawn_loc = new PVector();

      generator.player_spawn_loc.x = prev_x + Terrain.CELL_SIZE / 2f - Player.COLLISION_WIDTH / 2f;
      generator.player_spawn_loc.y = prev_y + Player.COLLISION_HEIGHT + 0.01f;
   }


   private void place_monster(int[] world, int world_index){
      // Check if the position is valid
      if(prev_x <= 0  || prev_x >= Terrain.WIDTH - 1 || prev_y <= 0 || prev_y >= Terrain.HEIGHT - 1){
         sys.warning_display.display_warning("Cannot place monster outside of the world");
         return;
      }

      if(world[world_index] != Terrain.AIR){
         sys.warning_display.display_warning("Must place monster in air");
         return;
      }


      PVector loc = new PVector(
              prev_x + Terrain.CELL_SIZE / 2f - Monster.COLLISION_WIDTH / 2f,
              prev_y + Monster.COLLISION_HEIGHT + 0.01f
      );

      generator.monster_spawn_locs.add(loc);
   }


   private void place_wall(int[] world, int[] tile_attributes, int world_index){
      check_door(world, tile_attributes, world_index);

      world[world_index] = Terrain.WALL;
      tile_attributes[world_index] = Terrain.EMPTY;
   }


   private void place_air(int[] world, int[] tile_attributes, int world_index){
      check_door(world, tile_attributes, world_index);

      world[world_index] = Terrain.AIR;
      tile_attributes[world_index] = Terrain.EMPTY;
   }


   private void place_non_grapple(int[] world, int[] tile_attributes, int world_index){
      check_door(world, tile_attributes, world_index);

      world[world_index] = Terrain.WALL;
      tile_attributes[world_index] = Terrain.NON_GRAPPLE;
   }


   private void place_door(int[] world, int[] tile_attributes, int world_index) {
      // Check space above
      if(prev_x <= 0 || prev_x >= Terrain.WIDTH - 1 || prev_y <= 0 || prev_y >= Terrain.WIDTH - 4){
         sys.warning_display.display_warning("Door must be placed inside world");
         return;
      }

      // Is space place door
      for(int i = 0; i < 3; i++){
         int index = generator.getIndex(prev_x, prev_y + i);
         world[index] = Terrain.WALL;
         tile_attributes[index] = Terrain.DOOR_BODY;
      }
      tile_attributes[world_index] = Terrain.DOOR_START;
   }


   private void check_door(int[] world, int[] tile_attributes, int world_index){
      if(tile_attributes[world_index] != Terrain.DOOR_BODY && tile_attributes[world_index] != Terrain.DOOR_START)
         return;

      // Does contain door remove it
      world[world_index] = Terrain.AIR;
      tile_attributes[world_index] = Terrain.AIR;

      check_door(world, tile_attributes, generator.getIndex(prev_x, prev_y + 1));
      check_door(world, tile_attributes, generator.getIndex(prev_x, prev_y - 1));
   }
}
