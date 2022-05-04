package GameEngine.Utils.Managers;

import GameEngine.GameEngine;
import ddf.minim.AudioSample;
import ddf.minim.Minim;

import java.io.File;
import java.util.HashMap;

public class AudioManager {
   // Attributes
   public final GameEngine sys;
   public final Minim minim;

   private HashMap<String, String> sound_files;
   private HashMap<String, AudioSample> sound_samples;


   // Constructor
   public AudioManager(GameEngine sys, Minim minim) {
      this.sys = sys;
      this.minim = minim;
      this.sound_files = new HashMap<>();
      this.sound_samples = new HashMap<>();

      load_sounds();
   }


   // Methods
   public void stop(){
      // Close all audio files
      for(var entry : sound_samples.entrySet())
         entry.getValue().close();

      // Close minim
      minim.stop();
   }


   public AudioSample get_sample(String name){
      if(!sound_files.containsKey(name)) {
         System.err.println("Unable to find sound: " + name);
         System.exit(0);
         return null;
      }

      if(sound_samples.containsKey(name))
         return sound_samples.get(name);

      AudioSample sample = minim.loadSample(sound_files.get(name));
      sound_samples.put(name, sample);

      return sample;
   }


   private void load_sounds(){
      System.out.println("Loading sounds");

      File sounds_folder = new File(GameEngine.SOUNDS_FOLDER);
      File[] sound_files = sounds_folder.listFiles();

      // check if empty
      if(sound_files == null){
         System.err.println(" - Failed to open sounds folder");
         return;
      }

      // Open each sound
      for(var file : sound_files){
         // Load sound
         System.out.println(" - Attempting: " + file.getName());

         String sound_name = file.getName().split("\\.")[0];

         this.sound_files.put(sound_name, file.getAbsolutePath());
      }

   }
}
