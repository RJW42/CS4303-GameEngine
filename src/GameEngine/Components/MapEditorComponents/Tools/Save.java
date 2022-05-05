package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.Levels.MapBuilder;


public class Save extends Tool {
   // Attributes
   private TerrainGenerator generator;
   private TerrainRenderer renderer;

   // Constructor
   public Save(GameObject parent) {
      super(parent);

      // Init Attributes
      this.icon_text = "Save";
   }


   // Methods 
   public void start() {
       generator = sys.terrain.getComponent(TerrainGenerator.class);
       renderer = sys.terrain.getComponent(TerrainRenderer.class);
   }
   public void draw() {}

   public void update() {
      if(!active)
         return;

      // Save world, and deactivate
      active = false;

      // Check player has been placed
      if(generator.player_spawn_loc == null){
         sys.warning_display.display_warning("Must place player before saving");
         return;
      }

      if(generator.goal_spawn_loc == null){
         sys.warning_display.display_warning("Must place goal before saving");
         return;
      }


      // Save world
      TerrainLoader.saveTerrain(generator, renderer, MapBuilder.name + ".json");
   }
}
