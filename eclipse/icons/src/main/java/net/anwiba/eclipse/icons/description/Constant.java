package net.anwiba.eclipse.icons.description;

import java.text.MessageFormat;
import java.util.Arrays;

public class Constant implements IConstant {

  private final String constantName;
  private final String className;
  private final String packageName;

  public Constant(final String packageName, final String className, final String constantName) {
    this.packageName = packageName;
    this.className = className;
    this.constantName = constantName;
  }

  @Override
  public String getConstantName() {
    return this.constantName;
  }

  @Override
  public String getClassName() {
    return this.className;
  }

  @Override
  public String getPackageName() {
    return this.packageName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IConstant) {
      final IConstant other = (IConstant) obj;
      return Arrays.equals(
          new String[] { getPackageName(), getClassName(), getConstantName() },
          new String[] { other.getPackageName(), other.getClassName(), other.getConstantName() });
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new String[] { getPackageName(), getClassName(), getConstantName() });
  }

  @Override
  public String getName() {
    return MessageFormat.format("{0}.{1}.{2}", //$NON-NLS-1$
        this.packageName,
        this.className,
        this.constantName);
  }
}
