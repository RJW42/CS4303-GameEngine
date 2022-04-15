package GameEngine.GameObjects;


import GameEngine.Components.MapBuildingComponents.NoClipController;
import GameEngine.Components.MapBuildingComponents.TileSelector;
import GameEngine.Components.UIRenderers.UIRectRenderer;
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

      this.components.add(new UIRectRenderer(this, new PVector(0, 0, 255), 100f, 50f));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
