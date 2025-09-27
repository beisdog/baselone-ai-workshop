package ch.erni.ai.demo.dataprep;

import java.io.File;
import java.util.Arrays;

public class RenameScans {


    public static void main(String[] args) {
        // Change this to your target directory
        String dirPath = "/Users/david.beisert/deverni/workspace/baselone-ai-workshop/data/scaninbox";

        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            System.err.println("Not a directory: " + dirPath);
            return;
        }

        // List files (ignoring subdirectories)
        File[] files = dir.listFiles(File::isFile);
        if (files == null || files.length == 0) {
            System.out.println("No files found in: " + dirPath);
            return;
        }

        // Optional: sort files for predictable renaming order
        //Arrays.sort(files);

        int counter = 1;
        for (File file : files) {
            String newName = "scan" + counter + ".pdf";
            File newFile = new File(dir, newName);

            // If a file with that name already exists, skip or handle collision
            if (newFile.exists()) {
                System.err.println("File already exists: " + newFile.getName());
            } else {
                boolean success = file.renameTo(newFile);
                if (success) {
                    System.out.println("Renamed: " + file.getName() + " -> " + newFile.getName());
                } else {
                    System.err.println("Failed to rename: " + file.getName());
                }
            }
            counter++;
        }
    }
}
