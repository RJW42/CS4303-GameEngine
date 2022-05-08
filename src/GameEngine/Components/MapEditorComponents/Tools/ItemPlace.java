package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.Renderers.GifRenderer;
import GameEngine.Components.TerrianComponents.DoorRenderer;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.*;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.rmi.MarshalException;


public class ItemPlace extends Tool {
   // Attributes
   private ItemSelect item_select;
   private TileSelector tile_selector;
   private InputManager.Key click;

   private TerrainGenerator generator;
   private TerrainRenderer renderer;

   private GifRenderer goal_renderer;

   private PImage kill_door_closed;
   private PImage basic_door_closed;
   private PImage player;
   private PImage monster_img;

   private int prev_x;
   private int prev_y;

   // Constructor
   public ItemPlace(GameObject parent) {
      super(parent);

      // Init attributes
      this.icon_text = "Place";
      this.player = sys.sprite_manager.get_sprite("player_right",
              Math.round(GameEngine.PIXEL_TO_METER * Player.RENDER_WIDTH),
              Math.round(GameEngine.PIXEL_TO_METER * Player.RENDER_HEIGHT)
      );

      this.monster_img = sys.sprite_manager.get_sprite("monster_left",
              Math.round(GameEngine.PIXEL_TO_METER * Monster.RENDER_WIDTH),
              Math.round(GameEngine.PIXEL_TO_METER * Monster.RENDER_HEIGHT)
      );
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

      // Load sprites
      goal_renderer = new GifRenderer(parent, Goal.GIF_SPRITE, Goal.GIF_FPS, Terrain.CELL_SIZE, Terrain.CELL_SIZE, new PVector(Terrain.CELL_SIZE / 2f, Terrain.CELL_SIZE / -2f), PConstants.PI * 2);
      goal_renderer.start();

      basic_door_closed = sys.sprite_manager.get_sprite(Door.BASIC_CLOSE_SPRITE, (int)(Terrain.CELL_SIZE * GameEngine.PIXEL_TO_METER), (int)(Terrain.CELL_SIZE * 3 * GameEngine.PIXEL_TO_METER));
      kill_door_closed = sys.sprite_manager.get_sprite(Door.KILL_CLOSE_SPRITE, (int)(Terrain.CELL_SIZE * GameEngine.PIXEL_TO_METER), (int)(Terrain.CELL_SIZE * 3 * GameEngine.PIXEL_TO_METER));
   }

   public void update() {
      // Update any renderers
      goal_renderer.update();

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
      float x = generator.player_spawn_loc.x + Player.COLLISION_WIDTH / 2f;
      float y = generator.player_spawn_loc.y + Player.COLLISION_HEIGHT / -2f;

      sys.image(player, x, y);
   }


   private void display_goal(){
      // Check if goal location set
      if(generator.goal_spawn_loc == null)
         return;

      // Renderer goal
      // Terrain.CELL_SIZE / 2f, Terrain.CELL_SIZE / -2f
      goal_renderer.offset.x = Terrain.CELL_SIZE / 2f - parent.pos.x + generator.goal_spawn_loc.x;
      goal_renderer.offset.y = Terrain.CELL_SIZE / -2f - parent.pos.y + generator.goal_spawn_loc.y;
      goal_renderer.draw();
   }


   private void display_monsters(){
      // Draw each monster
      generator.monster_spawn_locs.forEach(monster -> {
         // Render monster
         float x = monster.x + Monster.COLLISION_WIDTH / 2f;
         float y = monster.y + Monster.COLLISION_HEIGHT / -2f;

         sys.image(monster_img, x, y);
      });
   }


   private void display_doors(){
      // Draw each door
      int[] world = generator.getSpecialTiles();
      //sys.image(basic_door_closed, 1.5f, 1 + 1.5f);

      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Check if door
            if(world[generator.getIndex(x, y)] == Terrain.BASIC_DOOR_START)
               sys.image(basic_door_closed, x + 0.5f, y + 1.5f);
            else if(world[generator.getIndex(x, y)] == Terrain.KILL_DOOR_START)
               sys.image(kill_door_closed, x + 0.5f, y + 1.5f);
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
