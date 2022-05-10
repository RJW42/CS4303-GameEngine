package GameEngine.GameObjects.Guide;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UIRectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import GameEngine.GameObjects.MainMenu.MenuSelector;
import GameEngine.Utils.PGif;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;

public class GuideManager extends GameObject {
   // Attributes
   public boolean is_dead = false;

   public float box_width;
   public float box_height;
   public UIButton prev;
   public UIButton next;
   public UIButton menu;

   public PVector centre_pos;
   public PVector title_base_pos;
   public PVector title_pos;
   public float title_text_size;
   public Guides curr_guide;
   public PVector text_pos;
   public float text_size;
   public float total_height;
   public ArrayList<String> text;
   public PVector img_pos;
   public PImage curr_img;
   public PGif curr_gif;

   public int root_pos;


   // Constructor
   public GuideManager(GameEngine sys) {
      super(sys);

      // Add regular components 
      this.init_buttons();

      this.components.add(prev);
      this.components.add(next);
      this.components.add(menu);
   }


   // Methods
   public void prev_clicked(){
      if(root_pos == 0){
         return;
      }

      next.text_colour = TEXT_COLOUR;
      next.hover_text_colour = TEXT_HOVER_COLOUR;

      root_pos -= 1;
      setGuide(Guides.values()[root_pos]);

      if(root_pos == 0){
         prev.text_colour = new PVector(127, 127, 127);
         prev.hover_text_colour = new PVector(127, 127, 127);
      }
   }


   public void next_clicked(){
      if(root_pos == Guides.values().length - 1){
         return;
      }

      prev.text_colour = TEXT_COLOUR;
      prev.hover_text_colour = TEXT_HOVER_COLOUR;

      root_pos += 1;
      setGuide(Guides.values()[root_pos]);

      if(root_pos == Guides.values().length - 1){
         next.text_colour = new PVector(127, 127, 127);
         next.hover_text_colour = new PVector(127, 127, 127);
      }
   }


   public void menu_clicked(){
      // Go back to menu
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 3);
   }


   @Override
   public boolean isDestroyed() {
      return is_dead;
   }


   @Override
   public void draw() {
      // Draw components
      super.draw();

      // Draw the guide
      sys.pushUI();
      sys.textSize(title_text_size);
      sys.textAlign(PConstants.LEFT);
      sys.uiText(curr_guide.title, title_pos.x, title_pos.y);

      sys.textSize(text_size);
      float y = text_pos.y - sys.textAscent();
      float y_inc = sys.textAscent() + sys.textDescent();
      for(var l : text){
         sys.uiText(l, text_pos.x, y);
         y -= y_inc;
      }

      if(curr_img != null){
         sys.ui_image(curr_img, img_pos.x, img_pos.y, 0);
      } else if(curr_gif != null){
         curr_gif.ui_play(sys, img_pos, 0);
      }

      sys.popUI();
   }


   private void setGuide(Guides guide){
      // Reset img
      curr_img = null;
      curr_gif = null;

      // Set the title text
      curr_guide = guide;

      // Set title text size and pos
      float text_width = WIDTH * GameEngine.UI_SCALE;
      float text_height = HEIGHT * GameEngine.UI_SCALE;
      title_text_size = text_size(guide.title, text_width, text_height);
      sys.textSize(title_text_size);
      title_pos = new PVector(title_base_pos.x + 5f, title_base_pos.y - 5f - sys.textAscent());

      // Get the sizes for the text and img
      float max_item_width = box_width / 2f - SPACING * GameEngine.UI_SCALE;
      float max_item_height = box_height - (sys.textAscent() + sys.textDescent());

      // Get the size of the text and split it into lines
      text = new ArrayList<>();
      text_size = create_paragraph_size(guide.text, text, max_item_width, max_item_height, (int)title_text_size);
      text_pos = new PVector(
              centre_pos.x + SPACING * GameEngine.UI_SCALE / 2f,
              centre_pos.y + total_height / 2f
      );

      // Calculate the width for the image / gif
      float img_spacing = 100f * GameEngine.UI_SCALE;
      float scale = Math.min((max_item_width - img_spacing) / guide.i_width, (box_height) / guide.i_height);
      float img_width = scale * guide.i_width;
      float img_height = scale * guide.i_height;

      // Load in the image / gif
      if(guide.sprite_name != null) {
         curr_img = sys.sprite_manager.get_sprite(guide.sprite_name,
                 (int)img_width, (int)img_height
         );

         System.out.println(img_width);

         img_pos = new PVector(
                 centre_pos.x - (img_width / 2f + SPACING * GameEngine.UI_SCALE / 2f + img_spacing / 2f),
                 centre_pos.y
         );
      } else if(guide.gif_name != null){
         curr_gif = sys.sprite_manager.get_gif(guide.gif_name,
                 (int)img_width, (int)img_height
         );

         img_pos = new PVector(
                 centre_pos.x - (img_width / 2f + SPACING * GameEngine.UI_SCALE / 2f + img_spacing / 2f),
                 centre_pos.y
         );
      }
   }


   public void cache_images(){
      for(var guide : Guides.values()){
         setGuide(guide);
      }
   }


   private void init_buttons(){
      // Create three buttons
      menu = new UIButton(this, this::menu_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      menu.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      menu.rect_alpha_colour = BUTTON_ALPHA;

      prev = new UIButton(this, this::prev_clicked, "Previous",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      prev.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      prev.rect_alpha_colour = BUTTON_ALPHA;
      prev.text_colour = new PVector(127, 127, 127);
      prev.hover_text_colour = new PVector(127, 127, 127);

      next = new UIButton(this, this::next_clicked, "Next",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      next.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      next.rect_alpha_colour = BUTTON_ALPHA;

      prev.pos.x -= prev.width + SPACING * GameEngine.UI_SCALE;
      next.pos.x += next.width + SPACING * GameEngine.UI_SCALE;

      box_width = prev.width * 3 + SPACING * GameEngine.UI_SCALE * 2;
      box_height = box_width * 0.70f;

      prev.pos.y -= box_height / 2f + prev.height / 4f;
      next.pos.y -= box_height / 2f + prev.height / 4f;
      menu.pos.y -= box_height / 2f + prev.height / 4f;

      UIRectRenderer rect = new UIRectRenderer(this,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f), BUTTON_COLOUR, BORDER_COLOUR, BORDER_WIDTH,
              box_width / GameEngine.UI_SCALE, box_height / GameEngine.UI_SCALE, true
      );

      rect.pos.y += menu.height / 4f + SPACING * GameEngine.UI_SCALE;
      rect.alpha = BUTTON_ALPHA;

      this.components.add(rect);

      // Create menu variables
      centre_pos = rect.pos.copy();
      title_base_pos = new PVector(centre_pos.x - box_width / 2f, centre_pos.y + box_height / 2f);

      setGuide(Guides.values()[0]);
      root_pos = 0;
   }


   private float text_size(String text, float max_width, float max_height){
      // Get max height of text
      float text_size = 2;

      sys.textSize(text_size);
      float text_height = sys.textAscent() + sys.textDescent();

      while(text_height < max_height && sys.textWidth(text) < max_width){
         text_size += 1;
         sys.textSize(text_size);
         text_height = sys.textAscent() + sys.textDescent();
      }

      return text_size;
   }


   private float create_paragraph_size(String text, ArrayList<String> out, float max_width, float max_height, int max_size){
      // Get max height of text
      float text_size = 1;
      ArrayList<String> interal = new ArrayList<>();
      interal.add(text);

      sys.textSize(text_size);

      float text_width = 0;
      total_height = sys.textAscent() + sys.textDescent();

      while (total_height < max_height && text_width < max_width && text_size < max_size){
         // Update size
         text_size += 1;
         sys.textSize(text_size);

         float text_height = sys.textAscent() + sys.textDescent();

         // Update width
         text_width = 0;
         for(var s : interal){
            text_width = Math.max(sys.textWidth(s), text_width);
         }

         // Perform splits
         if(text_width > max_width) {
            ArrayList<String> new_internal = new ArrayList<>();
            StringBuilder new_line = new StringBuilder();

            for(var line : interal){
               for(var s : line.split(" ")){
                  if(sys.textWidth(new_line.toString() + " " + s) > max_width - 5f){
                     new_internal.add(new_line.toString());
                     new_line = new StringBuilder();
                  }
                  new_line.append(s).append(' ');
               }
            }

            // Add final line
            new_internal.add(new_line.deleteCharAt(new_line.length() - 1).toString());

            // Check still within height requirements
            float new_height = (text_height) * new_internal.size();
            if(new_height > max_height){
               break;
            }

            interal = new_internal;
            text_width = 0;

            for(var s : interal){
               text_width = Math.max(sys.textWidth(s), text_width);
            }
         }

         total_height = (text_height) * interal.size();
      }

      text_size -= 1;

      // Finish
      out.addAll(interal);
      return text_size;
   }
}
