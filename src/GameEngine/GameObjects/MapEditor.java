package GameEngine.GameObjects;


import GameEngine.Components.MapEditorComponents.NoClipController;
import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.MapEditorComponents.ToolMenu;
import GameEngine.Components.MapEditorComponents.Tools.ItemPlace;
import GameEngine.Components.MapEditorComponents.Tools.ItemSelect;
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
      this.components.add(new ToolMenu(this));

      // Add tools
      this.components.add(new ItemSelect(this));
      this.components.add(new ItemPlace(this));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
