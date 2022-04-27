package GameEngine.GameObjects;


import GameEngine.Components.MapEditorComponents.NoClipController;
import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.MapEditorComponents.ToolMenu;
import GameEngine.Components.MapEditorComponents.Tools.*;
import GameEngine.GameEngine;
import GameEngine.Levels.MainMenu;
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
      this.components.add(new Save(this));
      this.components.add(new Exit(this, this::on_exit));
      this.components.add(new RandomColour(this));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   private void on_exit(){
      sys.level_manager.startLevel(new MainMenu(sys));
   }
}
