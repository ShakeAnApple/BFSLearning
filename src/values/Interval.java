package values;

import java.io.Serializable;

/**
 * Created by Eskimos on 17.01.2018.
 */
public class Interval<VType> implements Serializable {
    private VType _from;
    private VType _to;

    public Interval(VType from, VType to){
        _from = from;
        _to = to;
    }

    public VType getFrom(){
        return _from;
    }

    public VType getTo(){
        return _to;
    }

    public boolean contains(VType value, ComparatorFunction<VType, Integer> comparator) {
        if ((comparator.apply(value, _from) >= 0 ) && (comparator.apply(value, _to) < 0)){
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Interval)){
            return false;
        }
        Interval otherInterval = (Interval)obj;

        return this._from.equals(otherInterval._from) && this._to.equals(otherInterval._to);
    }
}
