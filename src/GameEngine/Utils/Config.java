package GameEngine.Utils;

import GameEngine.GameEngine;

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
   public Config(){
      if(!init(GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE)){
         // Failed to read try reading the config folder
         System.out.println(" - Attempting to read the default config file");
         if(!init(GameEngine.CONFIG_FOLDER + GameEngine.DEFAULT_CONFIG_FILE)){
            // Failed to read the default config file
            System.out.println(" - No config file found");
            System.exit(0);
         }

         // Save the default config file
         System.out.println(" - Read in defaults saving");
         reset_to_default();
      }
   }


   // Methods
   public void reset_to_default(){
      // Delete current
      new File(GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE).delete();

      // Replace with default
      File default_file = new File(GameEngine.CONFIG_FOLDER + GameEngine.DEFAULT_CONFIG_FILE);
      File new_file     = new File(GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE);

      try{
         // Create new config file
         if(!new_file.exists())
            new_file.createNewFile();

         InputStream oInStream = new FileInputStream(default_file);
         OutputStream oOutStream = new FileOutputStream(new_file);

         // Transfer bytes from in to out
         byte[] oBytes = new byte[1024];
         int nLength;

         BufferedInputStream oBuffInputStream = new BufferedInputStream(oInStream);
         while((nLength = oBuffInputStream.read(oBytes)) > 0) {
            oOutStream.write(oBytes, 0, nLength);
         }
         oInStream.close();
         oOutStream.close();
      } catch (IOException e){
         System.out.println("Failed to reset config file to defaults");
         System.out.println(e.getMessage());
         System.exit(0);
      }

   }


   // Parsing
   private boolean init(String file_loc){
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
         return false;
      }
      return true;
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
