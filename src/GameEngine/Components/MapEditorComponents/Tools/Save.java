package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.GameObjects.GameObject;
import GameEngine.Levels.MapBuilder;


public class Save extends Tool {
   // Attributes
   private TerrainGenerator generator;

   // Constructor
   public Save(GameObject parent) {
      super(parent);

      // Init Attributes
      this.icon_text = "Save";
   }


   // Methods 
   public void start() {
       generator = sys.terrain.getComponent(TerrainGenerator.class);
   }

   public void update() {
      if(!active)
         return;

      // Save world, and deactivate
      // Todo: maybe add some cool down
      active = false;
      TerrainLoader.saveTerrain(generator, MapBuilder.name + ".json");
   }

   public void draw() {

   }
}
