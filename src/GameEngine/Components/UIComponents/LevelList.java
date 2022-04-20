package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.io.File;
import java.util.Arrays;

import static GameEngine.Components.TerrianComponents.TerrainLoader.SAVE_LOC;
import static GameEngine.GameEngine.UI_SCALE;
import static processing.core.PApplet.trim;

public class LevelList extends Component {
   // Attributes
   public static final int MAX_ITEMS_ON_LIST = 2;
   public static final float SPACING         = 1;

   public PVector text_colour;
   public PVector rect_colour;
   public PVector border_colour;
   public PVector hover_text_colour;
   public PVector hover_rect_colour;
   public PVector hover_border_colour;
   public PVector pos;
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
      this.width = item_width;
      this.height = item_height;
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
      // Todo: implement this function 
      //       Called every frame during update stage  
   }


   public void draw() {
      // Todo: implement this function 
      //       Called every frame during draw stage  
   }


   private void clicked(int id){
      // Todo: implement, load this level
      System.out.println(id);

   }


   private void up_pressed(){
      // Move all files up by one
      if(root_position == 0)
         return; // Can't do that as already at top
      System.out.println("UP");
      // Todo: finish here !!!!!!!
   }


   private void down_pressed(){
      System.out.println("DOWN");
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
                 hover_border_colour, padding, border_width, width, height, true
         );

         button_y -= button.height + SPACING * UI_SCALE;

         // Add to list
         buttons[i] = button;
         parent.addComponent(button);
      }
   }


   private float add_up_and_down_buttons(float button_y){
      // Create two buttons for moving up and down
      up_button = new UIButton(
              parent, this::up_pressed, "↑",
              new PVector(pos.x, button_y),
              text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
              hover_border_colour, padding, border_width, width / 2 - 0.5f, height, true
      );

      down_button = new UIButton(
              parent, this::down_pressed, "↓",
              new PVector(pos.x, button_y),
              text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour,
              hover_border_colour, padding, border_width, width / 2 - 0.5f, height, true
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
