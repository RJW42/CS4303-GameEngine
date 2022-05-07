package GameEngine.GameObjects;


import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UITextRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.Levels.GameOver;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;


public class LeaderBoard extends GameObject {
   // Attributes
   public static final int WIDTH  = 100;
   public static final int HEIGHT = 20;
   public static final int PADDING = 50;
   public static final int ITEM_PADDING = 10;
   public static final int LIST_SIZE = 10;

   public boolean is_dead = false;

   private final UITextRenderer title;
   private final UIButton back_button;
   private final LoadedTerrainGenerator generator;


   // Constructor
   public LeaderBoard(GameEngine sys, String file_name, LoadedTerrainGenerator generator) {
      super(sys);

      String level_name = file_name.split("\\.")[0];

      this.generator = generator;

      // Add regular components 
      title = new UITextRenderer(this,
              new PVector(GameEngine.SCREEN_WIDTH /2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, "Level: " + level_name, PConstants.CENTER, WIDTH, HEIGHT
      );

      back_button = new UIButton(this, this::menu_pressed, "Main Menu",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              1f, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      back_button.rect_alpha_colour = BUTTON_ALPHA;
      back_button.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;

      this.components.add(title);
      this.components.add(back_button);

      this.load_leaderboard();
   }


   private void menu_pressed(){
      ((GameOver)(sys.level_manager.getCurrentLevel())).menu_pressed();
   }


   private void load_leaderboard(){
      // Calculate the max height of each element
      float item_height = (GameEngine.SCREEN_HEIGHT - (PADDING * 2f + title.max_height + ITEM_PADDING * LIST_SIZE)) / (LIST_SIZE);
      item_height = (item_height > title.max_height) ? title.max_height : item_height;

      float total_height = (item_height * 10) + title.max_height + ITEM_PADDING;

      item_height /= GameEngine.UI_SCALE;
      item_height -= ITEM_PADDING / (float)GameEngine.UI_SCALE;

      title.text_pos.y = GameEngine.SCREEN_HEIGHT / 2f + total_height / 2f - title.max_height;
      back_button.pos.y = title.text_pos.y + title.max_height / 2f;

      title.text_pos.x -= title.max_width / 2f + PADDING;
      back_button.pos.x += back_button.width / 2f + PADDING;

      float y = title.text_pos.y - ITEM_PADDING;
      var times = generator.get_times();
      times.sort(TerrainGenerator.Time::compareTo);

      for(int i = 0; i < LIST_SIZE; i++){
         var name = (i < times.size()) ? times.get(i).time + " - " + times.get(i).username : " - ";
         UITextRenderer renderer = create_renderer((i + 1) + ": " + name, item_height);
         renderer.text_pos.y  = y - (renderer.max_height + ITEM_PADDING);
         this.components.add(renderer);
         y = renderer.text_pos.y;
      }
   }


   private UITextRenderer create_renderer(String text, float max_height){
      return new UITextRenderer(this, new PVector(GameEngine.SCREEN_WIDTH /2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, text, PConstants.CENTER, WIDTH - ITEM_PADDING, (int)max_height
      );
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}
