package GameEngine.Utils;

import java.io.*;
import java.util.Locale;
import java.util.Optional;

public class Config {
   // Attributes
   public boolean full_screen;
   public int screen_width;
   public int screen_height;
   public int world_width;
   public int world_height;
   public int display;


   // Constructor
   public Config(String file_loc){
      init(file_loc);
   }

   // Methods

   // Parsing
   private void init(String file_loc){
      // Open the file
      System.out.println("Reading config file");
      try (BufferedReader reader = new BufferedReader(new FileReader(file_loc))){
         // Read each line
         String line = reader.readLine();
         while (line != null){
            String[] pair = line.split("=");
            if (pair.length != 2){
               System.out.println(" - Bad line in config: " + line);
            }
            parse_line(pair[0].strip(), pair[1].strip());
            line = reader.readLine();
         }
      } catch (IOException e){
         System.out.println(" - Failed to read config file: " + e.getMessage());
         System.exit(0);
      }
   }

   private void parse_line(String key, String values){
      if(key.equalsIgnoreCase("FULL_SCREEN")){
         parse_fullscreen(values);
      }else if(key.equalsIgnoreCase("WINDOW_WIDTH") || key.equalsIgnoreCase("WINDOW_HEIGHT")){
         parse_window(key, values);
      }else if(key.equalsIgnoreCase("WORLD_WIDTH") || key.equalsIgnoreCase("WORLD_HEIGHT")){
         parse_world(key, values);
      }else if(key.equalsIgnoreCase("DISPLAY")){
         parse_display(values);
      }
   }


   private void parse_fullscreen(String value){
      parse_bool(value).ifPresentOrElse(bool -> {
         this.full_screen = bool;
      }, () -> {
         System.out.println("Failed to parse full screen");
         System.exit(0);
      });
   }


   private void parse_display(String value){
      parse_int(value).ifPresentOrElse(bool -> {
         this.display = bool;
      }, () -> {
         System.out.println("Failed to parse display");
         System.exit(0);
      });
   }


   private void parse_world(String key, String value){
      // Get int
      parse_int(value).ifPresentOrElse(num -> {
         if(key.toUpperCase(Locale.ROOT).contains("WIDTH"))
            this.world_width = num;
         else
            this.world_height = num;
      }, () -> {
         System.out.println("Failed to parse world size");
         System.exit(0);
      });
   }


   private void parse_window(String key, String value){
      // Get int
      parse_int(value).ifPresentOrElse(num -> {
         if(key.toUpperCase(Locale.ROOT).contains("WIDTH"))
            this.screen_width = num;
         else
            this.screen_height = num;
      }, () -> {
         System.out.println("Failed to parse world size");
         System.exit(0);
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
