/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.thread.job;

import net.anwiba.commons.lang.collection.IMutableObjectList;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;

public class JobBuilder {

  private String title;
  private String text;
  private String description;

  private final IMutableObjectList<IDescribedTask> taskLists = new ObjectList<>();

  public IJob build() {
    return new Job(new IJobConfiguration() {

      @Override
      public String getTitle() {
        return JobBuilder.this.title;
      }

      @Override
      public String getText() {
        return JobBuilder.this.text;
      }

      @Override
      public String getDescription() {
        return JobBuilder.this.description;
      }

      @Override
      public IObjectList<IDescribedTask> getSubTasks() {
        return JobBuilder.this.taskLists;
      }

    });
  }

  public JobBuilder setTitle(final String title) {
    this.title = title;
    return this;
  }

  public JobBuilder setText(final String text) {
    this.text = text;
    return this;
  }

  public JobBuilder setDescription(final String description) {
    this.description = description;
    return this;
  }

  public JobBuilder addDescribedTask(final IDescribedTask task) {
    this.taskLists.add(task);
    return this;
  }

  public JobBuilder addDescribedTask(final String describtion, final ITask task) {
    this.taskLists.add(new DescribedTaskBuilder().setDescription(describtion).setTask(task).build());
    return this;
  }

}
