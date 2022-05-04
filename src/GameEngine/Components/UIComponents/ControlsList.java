package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PConstants;
import processing.core.PVector;

import static GameEngine.GameEngine.UI_SCALE;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;


public class ControlsList extends Component {
   // Attributes
   public static final int MAX_ITEMS_ON_LIST = 3;

   public float width;
   public float height;
   public PVector pos;

   private UITextRenderer[] control_editors; // Todo: <- change to controls component
   private String[] controls;

   private int root_position;
   private UIButton up_button;
   private UIButton down_button;

   // Constructor
   public ControlsList(GameObject parent, PVector pos) {
      super(parent);

      // Todo: change icons when no more levels

      // Init attributes
      this.pos = pos;

      load_controls();
   }


   // Methods 
   public void start() {}
   public void update() {}
   public void draw() {}

   private void up_pressed(){
      // Move all files up by one
      if(root_position == 0)
         return; // Can't do that as already at top

      root_position -= 1;

      for(int i = 0; i < control_editors.length; i++){
         control_editors[i].text = controls[i + root_position];
         control_editors[i].reset_text_size();
      }
   }

   private void down_pressed(){
      // Move all files down by one
      if(root_position == controls.length - control_editors.length)
         return; // Can't do that as already at bottom

      root_position += 1;

      for(int i = 0; i < control_editors.length; i++){
         control_editors[i].text = controls[i + root_position];
         control_editors[i].reset_text_size();
      }
   }

   private void load_controls(){
      // Get all controls
      controls = InputManager.REQUIRED_CONTROLS.clone();

      // Create control editors
      int num_editors = Math.min(MAX_ITEMS_ON_LIST, controls.length);

      control_editors = new UITextRenderer[num_editors];

      float button_y = pos.y;
      if(num_editors != controls.length)
         button_y = add_up_and_down_buttons(button_y);
      root_position = 0;

      for(int i = 0; i < control_editors.length; i++){
         // Create button
         UITextRenderer control_editor = new UITextRenderer(
                 parent, new PVector(pos.x, button_y), TEXT_COLOUR,  controls[i],
                 PConstants.CENTER, (int)WIDTH, (int) HEIGHT
         );

         button_y -= control_editor.max_height + SPACING * UI_SCALE;

         // Add to list
         control_editors[i] = control_editor;
         parent.addComponent(control_editor);
      }

      // Use calculate the total width and height of the list
      width = control_editors[0].max_width;
      height = ((up_button != null ? up_button.pos.y : control_editors[0].text_pos.y)
              - control_editors[control_editors.length - 1].text_pos.y) +
              ((up_button != null) ?
                      (up_button.height / 2f + control_editors[control_editors.length - 1].max_height / 2f) :
                      control_editors[0].max_height);

      // update position based off this height
      pos.y -= height / 2f - control_editors[0].max_height;
   }

   private float add_up_and_down_buttons(float button_y){
      // Create two buttons for moving up and down
      up_button = new UIButton(
              parent, this::up_pressed, "↑",
              new PVector(pos.x, button_y),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR,
              BORDER_HOVER_COLOUR, PADDING, BORDER_WIDTH, WIDTH / 2 - 0.5f, HEIGHT, true
      );

      down_button = new UIButton(
              parent, this::down_pressed, "↓",
              new PVector(pos.x, button_y),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR,
              BORDER_HOVER_COLOUR, PADDING, BORDER_WIDTH, WIDTH / 2 - 0.5f, HEIGHT, true
      );

      up_button.pos.x -= up_button.width / 2 + 0.5f * UI_SCALE;
      down_button.pos.x += down_button.width / 2 + 0.5f * UI_SCALE;

      parent.addComponent(up_button);
      parent.addComponent(down_button);

      return button_y - down_button.height - SPACING * UI_SCALE;
   }
}
