package net.anwiba.eclipse.project.name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class NameHitMap implements INameHitMap {

  Map<String, AtomicLong> names = new HashMap<>();

  @Override
  public void add(final String name) {
    if (!this.names.containsKey(name)) {
      this.names.put(name, new AtomicLong());
    }
    this.names.get(name).incrementAndGet();
  }

  @Override
  public void reset() {
    this.names.clear();
  }

  @Override
  public Iterable<String> getNames() {
    final ArrayList<String> values = new ArrayList<>(this.names.keySet());
    Collections.sort(values);
    return values;
  }

  @Override
  public long getNumberOfUses(final String name) {
    return this.names.get(name).get();
  }

  @Override
  public boolean isEmpty() {
    return this.names.isEmpty();
  }
}
