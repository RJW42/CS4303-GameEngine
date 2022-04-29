package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.TerrianComponents.DoorRenderer;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.Core.Goal;
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
      this.icon_text = "Place";
   }


   // Methods 
   public void start() {
      this.item_select = parent.getComponent(ItemSelect.class);
      this.tile_selector = parent.getComponent(TileSelector.class);
      this.click = sys.input_manager.mouse_click;

      this.generator = sys.terrain.getComponent(TerrainGenerator.class);
      this.renderer = sys.terrain.getComponent(TerrainRenderer.class);

      generator.player_spawn_loc = null;
      generator.goal_spawn_loc = null;
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
      display_goal();
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


   private void display_goal(){
      // Check if goal location set
      if(generator.goal_spawn_loc == null)
         return;

      // Renderer goal
      // Todo: change this when proper goal model drawn
      sys.noStroke();
      sys.fill(Goal.COLOUR.x, Goal.COLOUR.y, Goal.COLOUR.z);
      sys.rectMode(PConstants.CORNER);
      sys.rect(generator.goal_spawn_loc.x, generator.goal_spawn_loc.y, 1, -1);
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
            if(world[generator.getIndex(x, y)] != Terrain.BASIC_DOOR_START && world[generator.getIndex(x, y)] != Terrain.KILL_DOOR_START)
               continue;
            // Todo: render both types of doors
            // Render door
            sys.stroke(DoorRenderer.COLOUR.x, DoorRenderer.COLOUR.y, DoorRenderer.COLOUR.z);
            sys.strokeWeight(0.1f);
            sys.noFill();
            sys.rect(x, y, Terrain.CELL_SIZE, Terrain.CELL_SIZE * 3);
         }
      }
   }


   private void place_tile() {
      // Set statics
      Item.generator = generator;
      Item.sys = sys;
      Item.world = generator.getWorld();
      Item.tile_attributes = generator.getSpecialTiles();

      // Get location to place
      prev_x = tile_selector.current_x;
      prev_y = tile_selector.current_y;
      int world_index = generator.getIndex(prev_x, prev_y);

      // Place item
      item_select.current_item.placer.place(world_index, prev_x, prev_y);

      // Refresh world
      renderer.reset();
   }
}
