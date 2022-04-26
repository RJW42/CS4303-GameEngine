package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.Utils.Managers.InputManager;


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
      // Todo: draw player properly in spawn location

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
            world[world_index] = Terrain.WALL;
            break;
         case AIR:
            world[world_index] = Terrain.AIR;
            break;
         case NON_GRAPPLE_WALL:
            world[world_index] = Terrain.WALL;
            tile_attributes[world_index] = Terrain.NON_GRAPPLE;
            break;
         case SPAWN_LOCATION:
            generator.player_spawn_loc.x = prev_x + Terrain.CELL_SIZE / 2f;
            generator.player_spawn_loc.y = prev_y + Terrain.CELL_SIZE / 2f;
            break;
         default:
            System.err.println("Unreachable point reached error in ItemPlace.java");
            System.exit(0);
      }

      renderer.resetMasks();
   }
}
