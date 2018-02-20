package values;

import java.io.Serializable;

public interface ValueHandler<VType> extends Serializable {
    void parseAndSetValue(Object val) throws Exception;

    ValueHandler<VType> clone();
}


