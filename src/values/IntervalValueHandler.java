package values;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by Eskimos on 17.01.2018.
 */
public class IntervalValueHandler implements ValueHandler<Interval<Double>> {

    private Interval<Double>[] _intervals;
    private Interval<Double> _currentInterval;
    private Double _concreteValue;

    private transient ComparatorFunction<Double, Integer> _comparator;
    private transient Function<String, Double> _parser;

    private Integer _currentIntervalNum;

    public IntervalValueHandler(Interval<Double>[] intervals, Function<String, Double> parser,
                                ComparatorFunction<Double, Integer> comparator,
                                Double value){
        _intervals = intervals;
        _concreteValue = value;
        _parser = parser;
        _comparator = comparator;

        setCurrentInterval();
    }

    public IntervalValueHandler(Interval<Double>[] intervals, Function<String, Double> parser,
                                ComparatorFunction<Double, Integer> comparator,
                                Interval<Double> interval){
        _intervals = intervals;
        _parser = parser;
        _comparator = comparator;

        setCurrentInterval(interval);
    }

    private void setCurrentInterval() {
        if (_concreteValue == null){
            _currentInterval = null;
            return;
        }

        for(int i = 0; i< _intervals.length; i++){
            if (_intervals[i].contains(_concreteValue, _comparator)){
                _currentInterval = _intervals[i];
                _currentIntervalNum = i;
                break;
            }
        }
    }

    public void setCurrentInterval(Interval<Double> interval){
        _currentInterval = interval;
        _concreteValue = interval.getFrom();

        for(int i = 0; i< _intervals.length; i++){
            if (_currentInterval.equals(_intervals[i])){
                _currentIntervalNum = i;
            }
        }
    }

    public Interval<Double> getCurrentInterval() {
        return _currentInterval;
    }

    @Override
    public void parseAndSetValue(Object val) throws Exception {
        if (val instanceof String){
            String v = (String)val;
            _concreteValue = _parser.apply(v);

            setCurrentInterval();
        }
    }

    @Override
    public String toString(){
        return _concreteValue.toString();
    }

    @Override
    public ValueHandler<Interval<Double>> clone() {
        return new IntervalValueHandler(_intervals, _parser, _comparator, Double.valueOf(_concreteValue));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof IntervalValueHandler)){
            return false;
        }

        IntervalValueHandler otherInterval = (IntervalValueHandler) obj;
        return _currentInterval.equals(otherInterval._currentInterval);
    }

    public Double getConcreteValue() {
        return _concreteValue;
    }

    public Integer getCurrentIntervalNum() {
        return _currentIntervalNum;
    }
}
