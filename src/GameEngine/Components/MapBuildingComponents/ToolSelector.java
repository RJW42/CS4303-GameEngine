package GameEngine.Components.MapBuildingComponents;


import GameEngine.Components.Component;
import GameEngine.Components.MapBuildingComponents.Tools.Tool;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;

import java.util.ArrayList;
import java.util.List;


public class ToolSelector extends Component {
   // Attributes
   private static int TOOL_ICON_SIZE = 20;
   private static int TOOL_BORDER_SIZE = 1;
   private static int x_pox = 10;
   private static int y_pos = 10;

   private Tool current_tool;
   private int width;
   private int height;
   private List<Tool> tools;

   // Constructor
   public ToolSelector(GameObject parent) {
      super(parent);

      // Init attributes

   }


   // Methods 
   public void start() {
      // Get all tools
      tools = parent.getComponents(Tool.class);

      // Use tools to set ui scale
      set_scale();
   }

   public void update() {

   }

   public void draw() {
      sys.pushUI();
      // Draw outer UI
      sys.fill(0);
      sys.rect(x_pox, y_pos, width, height);

      // Draw each tool
      tools.forEach(Tool::draw_icon);

      sys.popUI();
   }

   public void set_scale(){
      // Set the position and size of each tool button
      int scale = GameEngine.UI_SCALE;
      int tool_icon_size = TOOL_ICON_SIZE * scale;


      width = tools.size() * tool_icon_size;
      height = tool_icon_size;

      for(int i = 0; i < tools.size(); i++){
         Tool tool = tools.get(i);

         tool.active = false;
         tool.height = tool_icon_size - TOOL_BORDER_SIZE * scale * 2;
         tool.width = tool_icon_size - TOOL_BORDER_SIZE * scale * 2;
         tool.x = x_pox + TOOL_BORDER_SIZE * scale + i * tool_icon_size;
         tool.y = y_pos + TOOL_BORDER_SIZE * scale;
      }
   }
}
