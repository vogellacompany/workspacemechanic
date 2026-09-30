package com.google.eclipse.mechanic;

import java.util.ArrayList;
import java.util.List;

public class ListCollector<T> implements ICollector<T> {
  private final List<T> list = new ArrayList<>();

  public static <T> ListCollector<T> create() {
    return new ListCollector<T>();
  }

  public void collect(T element) {
    list.add(element);
  }

  public List<T> get() {
    return list;
  }
}
