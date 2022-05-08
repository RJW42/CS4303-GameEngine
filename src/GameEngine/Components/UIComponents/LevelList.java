package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Levels.MainMenu;
import GameEngine.Levels.PlayLevel;
import processing.core.PVector;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static GameEngine.Components.TerrianComponents.TerrainLoader.SAVE_LOC;
import static GameEngine.GameEngine.UI_SCALE;
import static processing.core.PApplet.trim;

public class LevelList extends Component {
   // Attributes
   public static final int MAX_NUM_ROWS = 3;
   public static final float SPACING    = 1;

   public PVector text_colour;
   public PVector rect_colour;
   public PVector border_colour;
   public PVector hover_text_colour;
   public PVector hover_rect_colour;
   public PVector hover_border_colour;
   public PVector pos;
   public float rect_alpha_colour;
   public float hover_rect_alpha_colour;
   public float item_width;
   public float item_height;
   public float width;
   public float height;

   private final float padding;
   private final float border_width;

   private File[] files;
   private UIButton[][] buttons;
   private UIButton up_button;
   private UIButton down_button;
   private int root_position;

   // Constructor
   public LevelList(GameObject parent, PVector pos, PVector text_colour, PVector rect_colour, PVector border_colour, PVector hover_text_colour, PVector hover_rect_colour, PVector hover_border_colour, float padding, float border_width, float item_width, float item_height, float rect_alpha_colour, float hover_rect_alpha_colour) {
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
      this.rect_alpha_colour = rect_alpha_colour;
      this.hover_rect_alpha_colour = hover_rect_alpha_colour;

      load_levels();
   }


   // Methods 
   public void start() {}
   public void update() {}
   public void draw() {}


   private void clicked(int r, int c){
      // Check if valid
      if((r + root_position) * 2 + c >= files.length)
         return;

      // Start playing the given level
      String level = files[(r + root_position) * 2 + c].getName();
      ((MainMenu)sys.level_manager.getCurrentLevel()).advance = (new PlayLevel(sys, level));
   }


   private void up_pressed(){
      // Move all files up by one
      if(root_position == 0)
         return; // Can't do that as already at top

      if(root_position == (Math.ceil(files.length / 2f) - buttons.length)){
         down_button.text_colour = text_colour;
         down_button.hover_text_colour = hover_text_colour;
      }

      root_position -= 1;

      for(int r = 0; r < buttons.length; r++){
         for(int c = 0; c < 2; c++) {
            buttons[r][c].text = get_level_name(r + root_position, c);
            buttons[r][c].reset_text_size();
         }
      }

      if(root_position == 0){
         up_button.text_colour = new PVector(127, 127, 127);
         up_button.hover_text_colour = new PVector(127, 127, 127);
      }
   }


   private void down_pressed(){
      // Move all files down by one
      if(root_position == (Math.ceil(files.length / 2f) - buttons.length))
         return; // Can't do that as already at bottom

      if (root_position == 0) {
         up_button.text_colour = text_colour;
         up_button.hover_text_colour = hover_text_colour;
      }

      root_position += 1;

      for(int r = 0; r < buttons.length; r++){
         for(int c = 0; c < 2; c++) {
            buttons[r][c].text = get_level_name(r + root_position, c);
            buttons[r][c].reset_text_size();
         }
      }

      if(root_position == (Math.ceil(files.length / 2f) - buttons.length)){
         down_button.text_colour = new PVector(127, 127, 127);
         down_button.hover_text_colour = new PVector(127, 127, 127);
      }
   }


   private void load_levels(){
      // Open all files
      File folder = new File(SAVE_LOC);
      files = folder.listFiles();
      Arrays.sort(files, new Comparator<File>() {
         public int compare(File f1, File f2){
            return f1.getName().compareTo(f2.getName());
         }
      });

      // Create buttons
      int num_rows = Math.min(MAX_NUM_ROWS, (int)Math.ceil(files.length / 2f));
      int num_buttons = num_rows * 2;

      buttons = new UIButton[num_rows][2];

      float button_y = pos.y;
      if(num_buttons != files.length)
         button_y = add_up_and_down_buttons(button_y);
      root_position = 0;

      for(int r = 0; r < buttons.length; r++){
         float x = pos.x - item_width * UI_SCALE / 2f - 0.5f * UI_SCALE;
         for(int c = 0; c < 2; c++) {
            // Create button
            int rf = r;
            int cf = c;
            UIButton button = new UIButton(
                    parent, new UIButton.CallBack() {
               public void onClick() {
                  clicked(rf, cf);
               }
            },
                    get_level_name(r, c),
                    new PVector(x, button_y),
                    text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
                    hover_border_colour, padding, border_width, item_width, item_height, true
            );

            button.rect_alpha_colour = rect_alpha_colour;
            button.hover_rect_alpha_colour = hover_rect_alpha_colour;

            // Add to list
            buttons[r][c] = button;
            parent.addComponent(button);
            x += item_width * UI_SCALE + 0.5f * UI_SCALE * 2;
         }

         button_y -= buttons[0][0].height + SPACING * UI_SCALE;
      }

      // Use calculate the total width and height of the list
      width = buttons[0][0].width;
      height = ((up_button != null ? up_button.pos.y : buttons[0][0].pos.y)
                  - buttons[buttons.length - 1][0].pos.y) +
               ((up_button != null) ?
                       (up_button.height / 2f + buttons[buttons.length - 1][0].height / 2f) :
                       buttons[0][0].height);

      // update position based off this height
      pos.y -= height / 2f - buttons[0][0].height;
   }


   private float add_up_and_down_buttons(float button_y){
      // Create two buttons for moving up and down
      up_button = new UIButton(
              parent, this::up_pressed, "↑",
              new PVector(pos.x, button_y),
              text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
              hover_border_colour, padding, border_width, item_width, item_height, true
      );

      up_button.rect_alpha_colour = rect_alpha_colour;
      up_button.hover_rect_alpha_colour = hover_rect_alpha_colour;
      up_button.text_colour = new PVector(127, 127, 127);

      down_button = new UIButton(
              parent, this::down_pressed, "↓",
              new PVector(pos.x, button_y),
              text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
              hover_border_colour, padding, border_width, item_width, item_height, true
      );

      down_button.rect_alpha_colour = rect_alpha_colour;
      down_button.hover_rect_alpha_colour = hover_rect_alpha_colour;

      up_button.pos.x -= up_button.width / 2 + 0.5f * UI_SCALE;
      down_button.pos.x += down_button.width / 2 + 0.5f * UI_SCALE;

      parent.addComponent(up_button);
      parent.addComponent(down_button);

      return button_y - down_button.height - SPACING * UI_SCALE;
   }


   private String get_level_name(int r, int c){
      if(r * 2 + c >= files.length)
         return "";
      return files[r * 2 + c].getName().split("\\.")[0];
   }
}
