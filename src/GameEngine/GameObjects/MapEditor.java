package GameEngine.GameObjects;


import GameEngine.Components.MapBuildingComponents.NoClipController;
import GameEngine.Components.MapBuildingComponents.TileSelector;
import GameEngine.Components.MapBuildingComponents.ToolSelector;
import GameEngine.Components.MapBuildingComponents.Tools.ChangeTileType;
import GameEngine.Components.MapBuildingComponents.Tools.PlaceTile;
import GameEngine.GameEngine;
import processing.core.PVector;


public class MapEditor extends GameObject {
   // Attributes
   public static final float SPEED = 20f;

   public boolean is_dead = false;


   // Constructor
   public MapEditor(GameEngine sys, PVector pos) {
      super(sys);

      this.pos = pos;

      // Add regular components 
      this.components.add(new NoClipController(this, SPEED));
      this.components.add(new TileSelector(this));
      this.components.add(new ToolSelector(this));

      // Add tools
      this.components.add(new PlaceTile(this));
      this.components.add(new ChangeTileType(this));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
