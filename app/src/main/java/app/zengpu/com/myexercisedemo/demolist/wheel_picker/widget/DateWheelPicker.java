/*
 * Copyright  2017  zengp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.zengpu.com.myexercisedemo.demolist.wheel_picker.widget;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.zengpu.com.myexercisedemo.demolist.wheel_picker.TripleWheelPicker;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.bean.Data;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.dialog.WheelPicker;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.parser.DataParser;

/**
 * Created by zengp on 2017/11/29.
 */

@SuppressLint("ValidFragment")
public class DateWheelPicker extends TripleWheelPicker {

    private DateBuilder dateBuilder;
    private List<Data> years = new ArrayList<>();
    private List<Data> monthsAll = new ArrayList<>();
    private List<Data> monthsForMaxYear = new ArrayList<>();
    private List<Data> days = new ArrayList<>();

    protected DateWheelPicker(DateBuilder builder) {
        super(builder);
        dateBuilder = builder;
    }

    public static DateBuilder instance() {
        return new DateBuilder<DateWheelPicker>(DateWheelPicker.class);
    }

    @Override
    protected void parseData() {
        // create data
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        int[] limit = dateBuilder.limit;
        int maxYear = currentYear, maxMonth = currentMonth, maxDay = currentDay;
        if (null != limit) {
            if (limit.length > 0) maxYear = limit[0];
            if (limit.length > 1) maxMonth = limit[1];
            if (limit.length > 2) maxDay = limit[2];
        }
        // data year
        if (builder.isAll) {
            Data data = new Data();
            data.id = 0;
            data.data = "不限";
            years.add(data);
        } else {
            int startYear = maxYear;
            int endYear = 1917;
            if (startYear < endYear) startYear = endYear;
            for (int year = startYear; year >= endYear; year--) {
                Data data = new Data();
                data.data = year + "";
                data.id = year;
                years.add(data);
            }
        }
        // data month all
        if (builder.isAll) {
            Data data = new Data();
            data.id = 0;
            data.data = "";
            monthsAll.add(data);
        }
        for (int start = 12; start >= 1; start--) {
            Data data = new Data();
            data.data = start + "";
            data.id = start;
            monthsAll.add(data);
        }
        // data month for max year
        maxMonth = Math.max(1, Math.min(12, maxMonth));
        if (maxMonth == 12) monthsForMaxYear = monthsAll;
        else {
            if (builder.isAll) {
                Data data = new Data();
                data.id = 0;
                data.data = "";
                monthsForMaxYear.add(data);
            }
            for (int start = maxMonth; start >= 1; start--) {
                Data data = new Data();
                data.data = start + "";
                data.id = start;
                monthsForMaxYear.add(data);
            }
        }

        // for days
        if (builder.isAll) {
            Data data = new Data();
            data.id = 0;
            data.data = "";
            days.add(data);
        }
        for (int start = 31; start >= 1; start--) {
            Data data = new Data();
            data.data = start + "";
            data.id = start;
            days.add(data);
        }
        // units
        String[] units = builder.units;
        if (null != units) {
            if (units.length > 0)
                rv_picker1.setUnit(units[0]);
            if (units.length > 1)
                rv_picker2.setUnit(units[1]);
            if (units.length > 2)
                rv_picker3.setUnit(units[2]);
        }
        // default position. find by defPosition firstly, then defValues
        int defP1 = 0, defP2 = 0, defP3 = 0;
        int[] defPosition = builder.defPosition;
        if (null != defPosition) {
            if (defPosition.length > 0) defP1 = defPosition[0];
            if (defPosition.length > 1) defP2 = defPosition[1];
            if (defPosition.length > 2) defP3 = defPosition[2];
        }
//        defP1 = Math.min(Math.max(0, defP1), datas.size() - 1);
//        defP2 = Math.min(Math.max(0, defP2), datas2.size() - 1);
//        defP3 = Math.min(Math.max(0, defP3), datas3.size() - 1);

        rv_picker3.setData(days);
        rv_picker3.scrollTargetPositionToCenter(defP3);
        rv_picker2.setData(monthsAll);
        rv_picker2.scrollTargetPositionToCenter(defP2);
        rv_picker1.setData(years);
        rv_picker1.scrollTargetPositionToCenter(defP1);
    }

    @Override
    public void onWheelScrollChanged(RecyclerWheelPicker wheelPicker, boolean isScrolling, int position, Data data) {
        if (!rv_picker1.isInitFinish() || !rv_picker2.isInitFinish() || !rv_picker3.isInitFinish())
            return;

        if (wheelPicker == rv_picker1) {
            if (!isScrolling && null != data) {
                pickData1 = data.data;
            } else {
                pickData1 = "";
            }
        } else if (wheelPicker == rv_picker2) {
            if (!isScrolling && null != data) {
                pickData2 = data.data;
            } else {
                pickData2 = "";
            }
        } else if (wheelPicker == rv_picker3) {
            if (!isScrolling && null != data)
                pickData3 = data.data;
            else pickData3 = "";
        }
    }

    public static class DateBuilder<T extends WheelPicker> extends Builder {

        public int[] limit;

        public DateBuilder(Class clazz) {
            super(clazz);
        }

        public DateBuilder limit(int... limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public WheelPicker build() {
            try {
                Constructor constructor = clazz.getDeclaredConstructor(DateBuilder.class);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
