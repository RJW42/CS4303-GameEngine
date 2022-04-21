package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import GameEngine.Levels.MainMenu;
import GameEngine.Levels.PlayLevel;
import processing.core.PVector;

import java.io.File;

import static GameEngine.Components.TerrianComponents.TerrainLoader.SAVE_LOC;
import static GameEngine.GameEngine.UI_SCALE;
import static processing.core.PApplet.trim;

public class LevelList extends Component {
   // Attributes
   public static final int MAX_ITEMS_ON_LIST = 3;
   public static final float SPACING         = 1;

   public PVector text_colour;
   public PVector rect_colour;
   public PVector border_colour;
   public PVector hover_text_colour;
   public PVector hover_rect_colour;
   public PVector hover_border_colour;
   public PVector pos;
   public float item_width;
   public float item_height;
   public float width;
   public float height;

   private final float padding;
   private final float border_width;

   private File[] files;
   private UIButton[] buttons;
   private UIButton up_button;
   private UIButton down_button;
   private int root_position;

   // Constructor
   public LevelList(GameObject parent, PVector pos, PVector text_colour, PVector rect_colour, PVector border_colour, PVector hover_text_colour, PVector hover_rect_colour, PVector hover_border_colour, float padding, float border_width, float item_width, float item_height) {
      super(parent);

      // Init attributes
      this.pos = pos;
      this.item_width = item_width;
      this.item_height = item_height;
      this.padding = padding;
      this.border_width = border_width;
      this.text_colour = text_colour;
      this.rect_colour = rect_colour;
      this.border_colour = border_colour;
      this.hover_border_colour = hover_border_colour;
      this.hover_rect_colour = hover_rect_colour;
      this.hover_text_colour = hover_text_colour;

      load_levels();
   }


   // Methods 
   public void start() {

   }
   public void update() {
   }
   public void draw() {
   }


   private void clicked(int id){
      // Start playing the given level
      sys.level_manager.startLevel(new PlayLevel(sys, files[id + root_position].getName()));
   }


   private void up_pressed(){
      // Move all files up by one
      if(root_position == 0)
         return; // Can't do that as already at top

      root_position -= 1;

      for(int i = 0; i < buttons.length; i++){
         buttons[i].text = get_level_name( i + root_position);
         buttons[i].reset_text_size();
      }
   }


   private void down_pressed(){
      // Move all files down by one
      if(root_position == files.length - buttons.length)
         return; // Can't do that as already at bottom

      root_position += 1;

      for(int i = 0; i < buttons.length; i++){
         buttons[i].text = get_level_name( i + root_position);
         buttons[i].reset_text_size();
      }
   }


   private void load_levels(){
      // Open all files
      File folder = new File(SAVE_LOC);
      files = folder.listFiles();

      // Create buttons
      int num_buttons = Math.min(MAX_ITEMS_ON_LIST, files.length);

      buttons = new UIButton[num_buttons];

      float button_y = pos.y;
      if(num_buttons != files.length)
         button_y = add_up_and_down_buttons(button_y);
      root_position = 0;

      for(int i = 0; i < buttons.length; i++){
         // Create button
         int id = i;
         UIButton button = new UIButton(
                 parent, new UIButton.CallBack() { public void onClick() {clicked(id); }},
                 get_level_name(i),
                 new PVector(pos.x, button_y),
                 text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
                 hover_border_colour, padding, border_width, item_width, item_height, true
         );

         button_y -= button.height + SPACING * UI_SCALE;

         // Add to list
         buttons[i] = button;
         parent.addComponent(button);
      }

      // Use calculate the total width and height of the list
      width = buttons[0].width;
      height = ((up_button != null ? up_button.pos.y : buttons[0].pos.y)
                  - buttons[buttons.length - 1].pos.y) +
               ((up_button != null) ?
                       (up_button.height / 2f + buttons[buttons.length - 1].height / 2f) :
                       buttons[0].height);

      // update position based off this height
      pos.y -= height / 2f - buttons[0].height;
   }


   private float add_up_and_down_buttons(float button_y){
      // Create two buttons for moving up and down
      up_button = new UIButton(
              parent, this::up_pressed, "↑",
              new PVector(pos.x, button_y),
              text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
              hover_border_colour, padding, border_width, item_width / 2 - 0.5f, item_height, true
      );

      down_button = new UIButton(
              parent, this::down_pressed, "↓",
              new PVector(pos.x, button_y),
              text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
              hover_border_colour, padding, border_width, item_width / 2 - 0.5f, item_height, true
      );

      up_button.pos.x -= up_button.width / 2 + 0.5f * UI_SCALE;
      down_button.pos.x += down_button.width / 2 + 0.5f * UI_SCALE;

      parent.addComponent(up_button);
      parent.addComponent(down_button);

      return button_y - down_button.height - SPACING * UI_SCALE;
   }


   private String get_level_name(int i){
      return files[i].getName().split("\\.")[0];
   }
}
