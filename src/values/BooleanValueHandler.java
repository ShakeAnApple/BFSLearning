package values;

import java.util.stream.Stream;

public class BooleanValueHandler implements ValueHandler<Boolean> {
    private boolean _value;

    public BooleanValueHandler(){

    }

    public BooleanValueHandler(boolean value) {
        _value = value;
    }

//    @Override
//    public Boolean getSymbol() {
//        return _value;
//    }
//
//    @Override
//    public void setValue(Boolean val) {
//        _value = val;
//    }

    @Override
    public void parseAndSetValue(Object val) throws Exception {
        if (val instanceof Boolean){
            _value = (Boolean)(val);
        } else if(val instanceof boolean[]) {
            boolean[] resultsAr = (boolean[]) val;
            _value = resultsAr[resultsAr.length - 1];
        } else if (val instanceof String){
            if (((String) val).length() > 1){
                _value = Boolean.parseBoolean((String) val);
            } else {
                _value = "1".equalsIgnoreCase((String)val);
            }
        }
    }

    @Override
    public ValueHandler<Boolean> clone() {
        return new BooleanValueHandler(_value);
    }

    @Override
    public String toString() {
        return ((Integer)(_value ? 1 : 0)).toString();
    }

    @Override
    public boolean equals(Object obj) {
        return _value == ((BooleanValueHandler)obj)._value;
    }
}
