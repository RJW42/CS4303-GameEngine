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
      // Check if the mouse is over any of the tools
      tools.forEach(Tool::check_hover);
   }

   public void draw() {
      sys.pushUI();
      // Draw outer UI
      sys.fill(0);
      sys.rect(x_pox, y_pos, width, height);

      // Draw each tool
      tools.forEach(Tool::draw_border);
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

         tool.selector = this;
         tool.height = tool_icon_size - TOOL_BORDER_SIZE * scale * 2;
         tool.width = tool_icon_size - TOOL_BORDER_SIZE * scale * 2;
         tool.x = x_pox + TOOL_BORDER_SIZE * scale + i * tool_icon_size;
         tool.y = y_pos + TOOL_BORDER_SIZE * scale;
         tool.border_width = TOOL_BORDER_SIZE * scale;
      }
   }

   public void set_active(Tool tool){
      // Todo: change this, to be better
      if(current_tool != null){
         this.current_tool.cancel();
      }

      this.current_tool = tool;
   }

   public void set_unactive(){
      this.current_tool = null;
   }
}
