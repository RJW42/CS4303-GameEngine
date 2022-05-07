package GameEngine.Components.MapEditorComponents;


import GameEngine.Components.Component;
import GameEngine.Components.MapEditorComponents.Tools.Tool;
import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.Arrays;
import java.util.List;


public class ToolMenu extends Component {
   // Attributes
   public static final PVector BACKGROUND_C     = new PVector();
   public static final PVector BORDER_C         = new PVector(255, 255, 255);
   public static final PVector BACKGROUND_H_C   = new PVector(165, 231, 242);
   public static final PVector BORDER_H_C       = new PVector();
   public static final PVector TEXT_C           = new PVector(224, 244, 255);
   public static final PVector TEXT_H_C         = new PVector(0, 0, 0);
   public static final PVector ACTIVE_C         = new PVector(66, 245, 245);
   public static final float ITEM_WIDTH         = 25;
   public static final float ITEM_HEIGHT        = 25;
   public static final float ITEM_BORDER_WIDTH  = 2;
   public static final float ITEM_PADDING       = 10;

   public boolean mouse_hovering;

   private PVector pos;
   private UIButton[] buttons;
   private List<Tool> tools;
   private int current_active;

   // Constructor
   public ToolMenu(GameObject parent) {
      super(parent);

      // Init attributes
      this.pos = new PVector(
              (ITEM_WIDTH * GameEngine.UI_SCALE) / 2f + 10,
              (ITEM_HEIGHT * GameEngine.UI_SCALE) / 2f + 10
      );
      this.current_active = -1;
   }


   // Methods
   public void start() {
      // Get all tools
      this.tools = parent.getComponents(Tool.class);
      this.buttons = new UIButton[tools.size()];

      // Create buttons for all tools
      create_buttons();

      // Add buttons to the parent
      for(var button : buttons)
         parent.addComponent(button);
   }

   public void update() {
      // Check if the current active is no longer active
      if(current_active >= 0 && !tools.get(current_active).active)
         toggle_tool(current_active);

      // Check if the mouse is hovering over any buttons. This value
      // is used by the tools, so they won't interfere with the menu.
      mouse_hovering = Arrays.stream(buttons).anyMatch(x -> x.mouse_over);
   }

   public void draw() {}


   private void toggle_tool(int id){
      // Check if there is already an active tool
      if(current_active >= 0) {
         // Deactivate this button
         buttons[current_active].rect_colour = BACKGROUND_C;
         buttons[current_active].hover_rect_colour = BACKGROUND_H_C;
         tools.get(current_active).active = false;
      }

      // Check if the given tool is a new tool
      if(id == current_active){
         current_active = -1;
         return;
      }

      // Set new tool to active
      buttons[id].rect_colour = ACTIVE_C;
      buttons[id].hover_rect_colour = ACTIVE_C;
      tools.get(id).active = true;
      current_active = id;
   }


   private void create_buttons(){
      // Create a button for each tool
      float x = pos.x;
      float y = pos.y;

      for(int i = 0; i < tools.size(); i++){
         // Give these tools a reference to the menu
         Tool tool = tools.get(i);
         tool.menu = this;

         // Create button and set position
         UIButton button = create_button(i, tool);
         buttons[i] = button;

         button.pos.x = x;
         button.pos.y = y;
         tool.pos = button.pos;

         x += button.width + 2;
      }
   }


   private UIButton create_button(int id, Tool tool){
      if(tool.icon != null)
         return new UIButton(
                 parent, () -> toggle_tool(id), tool.icon, new PVector(),
                 TEXT_C, BACKGROUND_C, BORDER_C, TEXT_H_C, BACKGROUND_H_C, BORDER_H_C,
                 ITEM_PADDING, ITEM_BORDER_WIDTH, ITEM_WIDTH, ITEM_HEIGHT, true
         );
      else
         return new UIButton(
                 parent, () -> toggle_tool(id), tool.icon_text, new PVector(),
                 TEXT_C, BACKGROUND_C, BORDER_C, TEXT_H_C, BACKGROUND_H_C, BORDER_H_C,
                 ITEM_PADDING, ITEM_BORDER_WIDTH, ITEM_WIDTH, ITEM_HEIGHT, true
         );
   }
}
