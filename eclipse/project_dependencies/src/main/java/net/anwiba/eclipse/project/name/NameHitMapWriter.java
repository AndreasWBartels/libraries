package net.anwiba.eclipse.project.name;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NameHitMapWriter {

  public void write(final INameHitMap nameHitMap, final File file) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      for (final String name : nameHitMap.getNames()) {
        writer.write("\"" + name + "\", " + nameHitMap.getNumberOfUses(name));
        writer.newLine();
      }
    }
  }
}
