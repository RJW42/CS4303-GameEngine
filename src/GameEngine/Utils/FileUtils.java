package GameEngine.Utils;

import GameEngine.GameEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class FileUtils {
   private FileUtils(){} // Contain class should not be constructed


   public static boolean copyAndReplaceFile(String f1, String f2){
      // Delete current
      new File(f2).delete();

      // Replace with default
      File default_file = new File(f1);
      File new_file     = new File(f2);

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
         System.err.println(" - Failed to copy and replace a file: ");
         System.err.println(" - " + e.getMessage());
         return false;
      }
      return true;
   }


   public static Optional<ArrayList<Pair<String, String>>> parseConfigFile(String file_loc){
      // Init output
      ArrayList<Pair<String, String>> out = new ArrayList<>();

      // Read file
      try (BufferedReader reader = new BufferedReader(new FileReader(file_loc))){
         // Read each line
         String line = reader.readLine();
         while (line != null){
            // Check if the line is a comment
            if(line.strip().length() == 0 || line.strip().charAt(0) == '#')
               continue;

            // Check line is a pair
            String[] pair = line.split("=");
            if (pair.length != 2){
               System.out.println(" - Bad line in config: " + line);
               continue;
            }

            // Save valid pair in output
            out.add(new Pair<>(pair[0].strip(), pair[1].strip()));

            // Continue to next line
            line = reader.readLine();
         }
      } catch (IOException e){
         System.err.println(" - Failed to read config file: " + e.getMessage());
         return Optional.empty();
      }

      // Return out
      return Optional.of(out);
   }
}
