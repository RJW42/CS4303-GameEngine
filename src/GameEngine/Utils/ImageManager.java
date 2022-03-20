package GameEngine.Utils;

import GameEngine.GameEngine;
import processing.core.PImage;

import java.io.File;
import java.util.*;

public class ImageManager {
   // Attributes
   private GameEngine sys;
   private HashMap<String, HashMap<Integer, PImage>> images;
   private HashMap<String, HashMap<Integer, PGif>> gifs;

   // Constructor
   public ImageManager(GameEngine sys){
      // Init varaibles
      this.sys = sys;
      this.images = new HashMap<>();
      this.gifs = new HashMap<>();

      // Load all sprites and gifs
      load_sprites();
      load_gifs();
   }


   // Methods
   public PImage get_sprite(String name){
      if(images.containsKey(name)){
         return images
            .get(name)
            .entrySet()
            .stream()
            .findFirst()
            .orElse(new AbstractMap.SimpleEntry<>(null, null))
            .getValue();
      }
      return null;
   }

   public PGif get_gif(String name){
      if(gifs.containsKey(name)){
         return gifs
            .get(name)
            .entrySet()
            .stream()
            .findFirst()
            .orElse(new AbstractMap.SimpleEntry<>(null, null))
            .getValue();
      }
      return null;
   }


   public PImage get_sprite(String name, int width, int height){
      // Get index
      int index = get_size_id(width, height);
      if(!images.containsKey(name)) {
         System.out.println("Failed to find sprite: " + name);
         return null;
      }

      // Check if size already created
      var image_map = images.get(name);
      if(image_map.containsKey(index)){
         return image_map.get(index);
      }

      // Resize image
      PImage img = get_sprite(name);
      img = img.copy();
      img.resize(width, height);

      image_map.put(index, img);

      return img;
   }

   public PGif get_gif(String name, int width, int height){
      // Get index
      int index = get_size_id(width, height);
      if(!gifs.containsKey(name)) {
         System.out.println("Failed to find gif: " + name);
         return null;
      }

      // Check if size already created
      var gif_map = gifs.get(name);
      if(gif_map.containsKey(index)){
         return gif_map.get(index);
      }

      // Resize image
      PGif gif = get_gif(name);
      gif = gif.copy();
      gif.resize(width, height);

      gif_map.put(index, gif);

      return gif;
   }


   private void load_sprites(){
      // Open folder and get all images
      System.out.println("Loading Sprites");

      File sprites_folder = new File(GameEngine.SPRITE_FOLDER);
      File[] sprite_files = sprites_folder.listFiles();

      // Check if empty
      if(sprite_files == null) {
         System.out.println(" - Failed to open sprite folder");
         return;
      }

      for(var file : sprite_files){
         // Load this sprite if possible
         System.out.println(" - Attempting: " + file.getName());

         // Get the images sprite name
         String sprite_name = file.getName().split("\\.")[0];

         // Load image
         PImage img = sys.loadImage(file.getAbsolutePath());

         if(img == null){
            System.out.println("    - Failed: " + sprite_name);
            continue;
         }

         System.out.println("    - Ok: " + sprite_name);
         HashMap<Integer, PImage> sizes = images.getOrDefault(sprite_name, new HashMap<>());
         sizes.put(get_size_id(img.width, img.height), img);
         images.put(sprite_name, sizes);
      }

      // Add new line for neatness
      System.out.println();
   }


   private void load_gifs(){
      // Open folder containing all gifs
      System.out.println("Loading gifs");

      File gifs_folder = new File(GameEngine.GIFS_FOLDER);
      String[] gifs = gifs_folder.list();

      if(gifs == null){
         System.out.println("Failed to open gifs folder");
         return;
      }

      for(var gif_name : gifs){
         // Open gif folder
         System.out.println(" - Attempting: " + gif_name);
         String folder_name = gifs_folder.getAbsolutePath() + "/" + gif_name + "/";
         File[] gif_folder = new File(folder_name).listFiles();

         if(gif_folder == null){
            System.out.println("    - Failed to open gif folder: " + folder_name);
         }

         // Open each image within this folder
         ArrayList<Pair<PImage, String>> frames = new ArrayList<>();

         for(var frame : gif_folder){
            // Get the frames name
            String frame_name = frame.getName().split("\\.")[0];

            // Load image
            PImage img = sys.loadImage(frame.getAbsolutePath());

            if(img == null){
               System.out.println("    - Failed to open frame: " + frame_name);
               continue;
            }

            System.out.println("    - Ok: " + frame_name);
            frames.add(new Pair<>(img, frame_name));
         }

         // Sort the frames then create the gif
         frames.sort(new Comparator<Pair<PImage, String>>() {
            @Override
            public int compare(Pair<PImage, String> o1, Pair<PImage, String> o2) {
               return o1.getSecond().compareTo(o2.getSecond());
            }
         });

         // Create a gif from these frames
         PImage[] frames_arr = new PImage[frames.size()];
         for(int i = 0; i < frames.size(); i++)
            frames_arr[i] = frames.get(i).getFirst();

         PGif gif = new PGif(frames_arr);

         // Save this gif to the gifs array
         HashMap<Integer, PGif> sizes = this.gifs.getOrDefault(gif_name, new HashMap<>());
         sizes.put(get_size_id(gif.width, gif.height), gif);
         this.gifs.put(gif_name, sizes);
      }
   }

   private int get_size_id(int width, int height){
      return Objects.hash(width, height);
   }
}
