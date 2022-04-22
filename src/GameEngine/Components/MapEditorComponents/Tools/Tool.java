package GameEngine.Components.MapEditorComponents.Tools;

import GameEngine.Components.Component;
import GameEngine.Components.MapEditorComponents.ToolMenu;
import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public abstract class Tool extends Component {
   // Attributes
   public boolean active;
   public ToolMenu menu;
   public UIButton.Icon icon;
   public String icon_text;
   public PVector pos;

   // Constructor
   public Tool(GameObject parent) {
      super(parent);

      // Init attributes
      this.active = false;
   }


   // Methods

}
