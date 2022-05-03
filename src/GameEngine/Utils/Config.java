package GameEngine.Utils;

import GameEngine.GameEngine;

import java.io.*;
import java.util.Locale;
import java.util.Optional;

public class Config {
   // Attributes
   public static final String FULL_SCREEN    = "FULL_SCREEN";
   public static final String WINDOW_WIDTH   = "WINDOW_WIDTH";
   public static final String WINDOW_HEIGHT  = "WINDOW_HEIGHT";
   public static final String UI_SCALE       = "UI_SCALE";
   public static final String DISPLAY        = "DISPLAY";

   public boolean full_screen;
   public int screen_width;
   public int screen_height;
   public int ui_scale;
   public int display;

   // Constructor
   public Config(){
      if(!init(GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE)){
         // Failed to read try reading the config folder
         System.out.println(" - Attempting to read the default config file");
         if(!init(GameEngine.DEFAULT_CONFIG_FOLDER + GameEngine.DEFAULT_CONFIG_FILE)){
            // Failed to read the default config file
            System.err.println(" - Failed to read the default config");
            System.exit(0);
         }

         // Save the default config file
         System.out.println(" - Read in defaults saving");
         reset_to_default();
      }
   }


   // Methods
   public void save(String file_loc){
      // Open and write config to file
      try (PrintWriter writer = new PrintWriter(new FileWriter(file_loc))) {
         writer.println(FULL_SCREEN + " = " + full_screen);
         writer.println(DISPLAY + " = " + display);
         writer.println(WINDOW_WIDTH + " = " + screen_width);
         writer.println(WINDOW_HEIGHT + " = " + screen_height);
         writer.println(UI_SCALE + " = " + ui_scale);
      }catch (IOException e){
         System.err.println(" - Failed to save to config file");
         System.exit(0);
      }
   }

   public void reset_to_default(){
      if(FileUtils.copyAndReplaceFile(
              GameEngine.DEFAULT_CONFIG_FOLDER + GameEngine.DEFAULT_CONFIG_FILE,
              GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE
      )) {
         return;
      }
      // Failed to reset to default
      System.err.println(" Failed to reset config file to default ");
   }


   // Parsing
   private boolean init(String file_loc){
      // Open the file
      System.out.println("Reading config file");
      return FileUtils.parseConfigFile(file_loc).map(
         pairs -> pairs
            .stream()
            .reduce(true, (val, pair) -> {
               // Parse each pair in the config file
               if(!parse_line(pair.first, pair.second))
                  return false;
               return val;
            }, Boolean::logicalOr
         )
      ).orElse(false);
   }


   private boolean parse_line(String key, String values){
      if(key.equalsIgnoreCase(FULL_SCREEN)){
         return parse_fullscreen(values);
      }else if(key.equalsIgnoreCase(WINDOW_WIDTH) || key.equalsIgnoreCase(WINDOW_HEIGHT)){
         return parse_window(key, values);
      }else if(key.equalsIgnoreCase(DISPLAY)){
         return parse_display(values);
      }else if(key.equalsIgnoreCase(UI_SCALE)){
         return parse_ui_scale(values);
      }

      // Unkown key
      System.out.println(" - Invalid key found in config file: " + key );
      return true;
   }


   private boolean parse_fullscreen(String value){
      return parse_bool(value).map(bool -> {
         this.full_screen = bool;
         return true;
      }).orElseGet(() -> {
         System.err.println(" - Failed to parse full screen");
         return false;
      });
   }


   private boolean parse_ui_scale(String value){
      return parse_int(value).map(ui_scale -> {
         if(ui_scale > 0) {
            this.ui_scale = ui_scale;
            return true;
         } else {
            System.err.println(" - Invalid ui scale value must be posative");
            return false;
         }
      }).orElseGet(() -> {
         System.err.println(" - Failed to parse ui scale");
         return false;
      });
   }


   private boolean parse_display(String value){
      return parse_int(value).map(display -> {
         this.display = display;
         return true;
      }).orElseGet(() -> {
         System.err.println(" - Failed to parse display");
         return false;
      });
   }


   private boolean parse_window(String key, String value){
      // Get int
      return parse_int(value).map(num -> {
         if(key.toUpperCase(Locale.ROOT).contains("WIDTH"))
            this.screen_width = num;
         else
            this.screen_height = num;
         return true;
      }).orElseGet(() -> {
         System.err.println(" - Failed to parse world size");
         return false;
      });
   }


   private Optional<Boolean> parse_bool(String value){
      // Parse the string into a bool
      try{
         return Optional.of(Boolean.parseBoolean(value));
      } catch (Exception e){
         return Optional.empty();
      }
   }


   private Optional<Integer> parse_int(String value){
      // Parse the string into an int
      try{
         return Optional.of(Integer.parseInt(value));
      } catch (Exception e){
         return Optional.empty();
      }
   }
}
