package values;

import java.io.Serializable;

@FunctionalInterface
public interface ComparatorFunction<T1, TR>{
    public TR apply(T1 o1, T1 o2);
}
