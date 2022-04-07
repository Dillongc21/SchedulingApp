package com.scheduler.client.util;

import com.scheduler.common.util.TimeMeridiem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.IntegerStringConverter;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Initializer {

    final static private int HOUR_MIN = 1;
    final static private int HOUR_MAX = 12;
    final static private int HOUR_INITIAL = 12;
    final static private int MINUTE_MIN = 0;
    final static private int MINUTE_MAX = 59;
    final static private int MINUTE_INITIAL = 0;
    final static private int MINUTE_STEP_INTERVAL = 15;
    final static private ObservableList<String> timeMeridiems = FXCollections.observableArrayList(
            Arrays.stream(TimeMeridiem.values())
                    .map(Enum::toString)
                    .collect(Collectors.toList()));

    final static private IntegerStringConverter minuteStringConverter = new IntegerStringConverter() {
        @Override public String toString(Integer value) {
            if (value == null)
                return "";
            return String.format("%02d",value);
        }
    };

    public static SpinnerValueFactory.IntegerSpinnerValueFactory generateHourSpinnerFactory() {
        var factory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(HOUR_MIN, HOUR_MAX, HOUR_INITIAL);
        factory.setWrapAround(true);
        return factory;
    }

    public static SpinnerValueFactory.IntegerSpinnerValueFactory generateMinuteSpinnerFactory() {
        var factory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(MINUTE_MIN, MINUTE_MAX, MINUTE_INITIAL, MINUTE_STEP_INTERVAL);
        factory.setWrapAround(true);
        factory.setConverter(minuteStringConverter);
        return factory;
    }

    public static SpinnerValueFactory.ListSpinnerValueFactory<String> generateMeridiemSpinnerFactory() {
        var factory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(timeMeridiems);
        factory.setWrapAround(true);
        factory.setValue(timeMeridiems.get(0));
        return factory;
    }
}
