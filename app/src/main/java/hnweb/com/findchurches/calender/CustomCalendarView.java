/*
 * Copyright (c) 2016 Stacktips {link: http://stacktips.com}.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hnweb.com.findchurches.calender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import hnweb.com.findchurches.R;
import hnweb.com.findchurches.calender.utils.CalendarUtils;


public class CustomCalendarView extends LinearLayout {

    private Context mContext;
    private View view;
    private ImageView previousMonthButton;
    private ImageView nextMonthButton;
    private CalendarListener calendarListener;
    private Calendar currentCalendar;
    private Locale locale;
    private Date lastSelectedDay;
    private Typeface customTypeface;

    private int firstDayOfWeek = Calendar.SUNDAY;
    private List<DayDecorator> decorators = null;

    private static final String DAY_OF_WEEK = "dayOfWeek";
    private static final String DAY_OF_MONTH_TEXT = "dayOfMonthText";
    private static final String DAY_OF_MONTH_CONTAINER = "dayOfMonthContainer";

    private int disabledDayBackgroundColor;
    private int disabledDayTextColor;
    private int calendarBackgroundColor;
    private int selectedDayBackground;
    private int weekLayoutBackgroundColor;
    private int calendarTitleBackgroundColor;
    private int selectedDayTextColor;
    private int calendarTitleTextColor;
    private int dayOfWeekTextColor;
    private int dayOfMonthTextColor;
    private int currentDayOfMonth;
    DayView viewn;
    private int currentMonthIndex = 0;
    private boolean isOverflowDateVisible = true;
    private Drawable dateBackgroundDrawable;

    public CustomCalendarView(Context mContext) {
        this(mContext, null);
    }


    public CustomCalendarView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        this.mContext = mContext;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (isInEditMode())
                return;
        }

        getAttributes(attrs);

        initializeCalendar();
    }


    private void getAttributes(AttributeSet attrs) {
        final TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0);
        calendarBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_calendarBackgroundColor, getResources().getColor(R.color.white));
        calendarTitleBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_titleLayoutBackgroundColor, getResources().getColor(R.color.white));
        calendarTitleTextColor = typedArray.getColor(R.styleable.CustomCalendarView_calendarTitleTextColor, getResources().getColor(R.color.black));
        weekLayoutBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_weekLayoutBackgroundColor, getResources().getColor(R.color.white));
        dayOfWeekTextColor = typedArray.getColor(R.styleable.CustomCalendarView_dayOfWeekTextColor, getResources().getColor(R.color.black));
        dayOfMonthTextColor = typedArray.getColor(R.styleable.CustomCalendarView_dayOfMonthTextColor, getResources().getColor(R.color.black));
        disabledDayBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_disabledDayBackgroundColor, getResources().getColor(R.color.day_disabled_background_color));
        disabledDayTextColor = typedArray.getColor(R.styleable.CustomCalendarView_disabledDayTextColor, getResources().getColor(R.color.day_disabled_text_color));

        selectedDayBackground = typedArray.getColor(R.styleable.CustomCalendarView_selectedDayBackgroundColor, getResources().getColor(R.color.selected_day_background));

        selectedDayTextColor = typedArray.getColor(R.styleable.CustomCalendarView_selectedDayTextColor, getResources().getColor(R.color.white));
        currentDayOfMonth = typedArray.getColor(R.styleable.CustomCalendarView_currentDayOfMonthColor, getResources().getColor(R.color.current_day_of_month));

        typedArray.recycle();
    }

    public void initializeCalendar() {
        final LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.custom_calendar_layout, this, true);
        previousMonthButton = (ImageView) view.findViewById(R.id.leftButton);
        nextMonthButton = (ImageView) view.findViewById(R.id.rightButton);

        previousMonthButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex--;
                currentCalendar = Calendar.getInstance(Locale.getDefault());
                currentCalendar.add((Calendar.MONTH), currentMonthIndex);

                refreshCalendar(currentCalendar);
                if (calendarListener != null) {
                    calendarListener.onMonthChanged(currentCalendar.getTime());
                }
            }
        });

        nextMonthButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex++;
                currentCalendar = Calendar.getInstance(Locale.getDefault());
                currentCalendar.add((Calendar.MONTH), currentMonthIndex);
                refreshCalendar(currentCalendar);

                if (calendarListener != null) {
                    calendarListener.onMonthChanged(currentCalendar.getTime());
                }
            }
        });

        // Initialize calendar for current month
        Locale locale = mContext.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);

        setFirstDayOfWeek(Calendar.SUNDAY);
        refreshCalendar(currentCalendar);
    }

    /**
     * Display calendar title with next previous month button
     */
    private void initializeTitleLayout() {
        View titleLayout = view.findViewById(R.id.titleLayout);
        titleLayout.setBackgroundColor(calendarTitleBackgroundColor);

        String dateText = new DateFormatSymbols(locale).getShortMonths()[currentCalendar.get(Calendar.MONTH)].toString();
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());

        TextView dateTitle = (TextView) view.findViewById(R.id.dateTitle);
        dateTitle.setTextColor(calendarTitleTextColor);
        dateTitle.setText(dateText + " " + currentCalendar.get(Calendar.YEAR));
        dateTitle.setTextColor(calendarTitleTextColor);
        if (null != getCustomTypeface()) {
            dateTitle.setTypeface(getCustomTypeface(), Typeface.BOLD);
        }

    }

    /**
     * Initialize the calendar week layout, considers start day
     */
    @SuppressLint("DefaultLocale")
    private void initializeWeekLayout() {
        TextView dayOfWeek;
        String dayOfTheWeekString;

        //Setting background color white
        View titleLayout = view.findViewById(R.id.weekLayout);
        titleLayout.setBackgroundColor(weekLayoutBackgroundColor);

        final String[] weekDaysArray = new DateFormatSymbols(locale).getShortWeekdays();
        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfTheWeekString = weekDaysArray[i];
            if (dayOfTheWeekString.length() > 3) {
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 3).toUpperCase();
            }

            dayOfWeek = (TextView) view.findViewWithTag(DAY_OF_WEEK + getWeekIndex(i, currentCalendar));
            dayOfWeek.setText(dayOfTheWeekString);
            dayOfWeek.setTextColor(dayOfWeekTextColor);

            if (null != getCustomTypeface()) {
                dayOfWeek.setTypeface(getCustomTypeface());
            }
        }
    }

    private void setDaysInCalendar() {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.setFirstDayOfWeek(getFirstDayOfWeek());
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        // Calculate dayOfMonthIndex
        int dayOfMonthIndex = getWeekIndex(firstDayOfMonth, calendar);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        final Calendar startCalendar = (Calendar) calendar.clone();
        //Add required number of days
        startCalendar.add(Calendar.DATE, -(dayOfMonthIndex - 1));
        int monthEndIndex = 42 - (actualMaximum + dayOfMonthIndex - 1);

        ViewGroup dayOfMonthContainer;
        for (int i = 1; i < 43; i++) {
            dayOfMonthContainer = (ViewGroup) view.findViewWithTag(DAY_OF_MONTH_CONTAINER + i);
            viewn = (DayView) view.findViewWithTag(DAY_OF_MONTH_TEXT + i);
            if (viewn == null)
                continue;

            //Apply the default styles
            dayOfMonthContainer.setOnClickListener(null);
            viewn.bind(startCalendar.getTime(), getDecorators());
            viewn.setVisibility(View.VISIBLE);

            if (null != getCustomTypeface()) {
                viewn.setTypeface(getCustomTypeface());
            }

            if (CalendarUtils.isSameMonth(calendar, startCalendar)) {
                dayOfMonthContainer.setOnClickListener(onDayOfMonthClickListener);
                viewn.setBackgroundColor(calendarBackgroundColor);
                viewn.setTextColor(dayOfWeekTextColor);
                //Set the current day color
                markDayAsCurrentDay(startCalendar);
            } else {
                viewn.setBackgroundColor(disabledDayBackgroundColor);
                viewn.setTextColor(disabledDayTextColor);

                if (!isOverflowDateVisible())
                    viewn.setVisibility(View.GONE);
                else if (i >= 36 && ((float) monthEndIndex / 7.0f) >= 1) {
                    viewn.setVisibility(View.GONE);
                }
            }
            viewn.decorate();


            startCalendar.add(Calendar.DATE, 1);
            dayOfMonthIndex++;
        }

        // If the last week row has no visible days, hide it or show it in case
        ViewGroup weekRow = (ViewGroup) view.findViewWithTag("weekRow6");
        viewn = (DayView) view.findViewWithTag("dayOfMonthText36");
        if (viewn.getVisibility() != VISIBLE) {
            weekRow.setVisibility(GONE);
        } else {
            weekRow.setVisibility(VISIBLE);
        }
    }

    public void clearDayOfTheMonthStyle(Date currentDate) {
        if (currentDate != null) {
            final Calendar calendar = getTodaysCalendar();
            calendar.setFirstDayOfWeek(getFirstDayOfWeek());
            calendar.setTime(currentDate);

            viewn = getDayOfMonthText(calendar);
            viewn.setBackgroundColor(calendarBackgroundColor);
            viewn.setTextColor(dayOfWeekTextColor);
            viewn.decorate();
        }
    }

    public void clearDayOfTheMonthStyle1(Date currentDate) {
        if (currentDate != null) {
            final Calendar calendar = getTodaysCalendar();
            calendar.setFirstDayOfWeek(getFirstDayOfWeek());
            calendar.setTime(currentDate);

            viewn = getDayOfMonthText(calendar);
            //dayView.setBackgroundColor(calendarBackgroundColor);
            viewn.setTextColor(dayOfWeekTextColor);
            viewn.decorate();
        }
    }


    public DayView getDayOfMonthText(Calendar currentCalendar) {
        return (DayView) getView(DAY_OF_MONTH_TEXT, currentCalendar);
    }

    public DayView getDayOfMonthText1(Calendar currentCalendar) {
        return (DayView) getView(DAY_OF_MONTH_TEXT, currentCalendar);
    }

    private int getDayIndexByDate(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int index = currentDay + monthOffset;
        return index;
    }

    private int getMonthOffset(Calendar currentCalendar) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(getFirstDayOfWeek());
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {
            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();
        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    public View getView(String key, Calendar currentCalendar) {
        int index = getDayIndexByDate(currentCalendar);
        View childView = view.findViewWithTag(key + index);
        return childView;
    }

    private Calendar getTodaysCalendar() {
        Calendar currentCalendar = Calendar.getInstance(mContext.getResources().getConfiguration().locale);
        currentCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        return currentCalendar;
    }

    @SuppressLint("DefaultLocale")
    public void refreshCalendar(Calendar currentCalendar) {
        this.currentCalendar = currentCalendar;
        this.currentCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        locale = mContext.getResources().getConfiguration().locale;

        // Set date title
        initializeTitleLayout();

        // Set weeks days titles
        initializeWeekLayout();

        // Initialize and set days in calendar
        setDaysInCalendar();
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public void markDayAsCurrentDay(Calendar calendar) {
        if (calendar != null && CalendarUtils.isToday(calendar)) {
            viewn = getDayOfMonthText(calendar);
            currentDayOfMonth = (Color.parseColor("#3F51B5"));
            //currentDayOfMonth = typedArray.getColor(R.styleable.CustomCalendarView_currentDayOfMonthColor, getResources().getColor(R.color.current_day_of_month));
            viewn.setTextColor(currentDayOfMonth);
        }
    }

    public void markDayAsSelectedDay(Date currentDate) {
        final Calendar currentCalendar = getTodaysCalendar();
        currentCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        currentCalendar.setTime(currentDate);

        // Clear previous marks
        clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate);

        // Mark current day as selected
        viewn = getDayOfMonthText(currentCalendar);
        //  view.setBackground(getResources().getDrawable(R.drawable.ic_circle_red));
        viewn.setBackground(getResources().getDrawable(R.drawable.ic_circle_blue));
        // view.setBackgroundColor(selectedDayBackground);
        viewn.setTextColor(selectedDayTextColor);
    }

    public void markDayAsSelectedDay1(Date currentDate) {
        final Calendar currentCalendar = getTodaysCalendar();
        currentCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        currentCalendar.setTime(currentDate);

        // Clear previous marks
        clearDayOfTheMonthStyle1(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate);

        // Mark current day as selected
        viewn = getDayOfMonthText1(currentCalendar);
        //view.setBackgroundColor(selectedDayBackground);
        viewn.setTextColor(selectedDayTextColor);
    }


    private void storeLastValues(Date currentDate) {
        lastSelectedDay = currentDate;
    }

    public void setCalendarListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    private OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Extract day selected
            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_MONTH_CONTAINER.length(), tagId.length());
            final TextView dayOfMonthText = (TextView) view.findViewWithTag(DAY_OF_MONTH_TEXT + tagId);
            String text = dayOfMonthText.getText().toString();
            // Fire event
            final Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(getFirstDayOfWeek());
            calendar.setTime(currentCalendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfMonthText.getText().toString()));
            markDayAsSelectedDay(calendar.getTime());

            //Set the current day color
            markDayAsCurrentDay(currentCalendar);

            if (calendarListener != null)
                calendarListener.onDateSelected(calendar.getTime());
        }
    };

    public List<DayDecorator> getDecorators() {
        return decorators;
    }

    public void setDecorators(List<DayDecorator> decorators) {
        this.decorators = decorators;
    }

    public boolean isOverflowDateVisible() {
        return isOverflowDateVisible;
    }

    public void setShowOverflowDate(boolean isOverFlowEnabled) {
        isOverflowDateVisible = isOverFlowEnabled;
    }

    public void setCustomTypeface(Typeface customTypeface) {
        this.customTypeface = customTypeface;
    }

    public Typeface getCustomTypeface() {
        return customTypeface;
    }

    public Calendar getCurrentCalendar() {
        return currentCalendar;
    }


    public void setSelectedDayGreen(ArrayList<String> currentDate) {

        Calendar mCalendar = Calendar.getInstance();
        int daysInMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Log.e("All Days Red", String.valueOf(daysInMonth));
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        ArrayList<String> allDays = new ArrayList<String>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        allDays.clear();
        for (int i = 0; i < daysInMonth; i++) {
            // Add day to list
            allDays.add(mFormat.format(mCalendar.getTime()));
            // Move next day
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            Log.e("All Days Red", mCalendar.toString());
        }

        allDays.removeAll(currentDate);
        Log.e("AllDAYSFINAL", String.valueOf(allDays.removeAll(currentDate)));
        //setBackgroundColorOfRedOrGreen(allDays, R.color.maroon);
        //setBackgroundColorOfRedOrGreen(currentDate, R.color.green);

        // if (mCalendar != null && CalendarUtils.isToday(currentDate)) {
        // DayView dayOfMonth = getDayOfMonthText(calendar);
        // dayOfMonth.setTextColor(currentDayOfMonth);
        // dayOfMonth.setBackgroundColor(getResources().getColor(R.color.golden));
        // }


    }

    public void setSelectedDayRed(int month) {
        Calendar mycal = new GregorianCalendar(Calendar.YEAR, (month - 1), 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        ArrayList<String> allDays = new ArrayList<String>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        allDays.clear();
        for (int i = 0; i < daysInMonth; i++) {
            // Add day to list
            allDays.add(mFormat.format(mycal.getTime()));
            // Move next day
            mycal.add(Calendar.DAY_OF_MONTH, 1);
            Log.e("All Days Red", mycal.toString());
        }
        //setBackgroundColorOfRedOrGreen(allDays, R.color.maroon);


    }

    @SuppressLint("LongLogTag")
    public void setBackgroundColorOfRedOrGreen(ArrayList<String> daysList, String date, String status) {
        String[] date_split = date.split("-");
        String year = date_split[0];
        Log.d("Come year is ", year);
        String month = date_split[1];
        Log.d("Come month is ", month);
        String date_s = date_split[2];
        Log.d("Come month is ", date_s);

        int yearint = Integer.parseInt(year);
        Log.d("Come monthint is ", String.valueOf(yearint));

        int monthint = Integer.parseInt(month);
        Log.d("Come monthint is ", String.valueOf(monthint));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearint);
        Log.d("0 ", "0");
        cal.set(Calendar.MONTH, monthint);
        Log.d("0 ", "1");
        cal.set(Calendar.DAY_OF_MONTH, 0);
        Log.d("0 ", "2");
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("0 ", "3");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("0 ", String.valueOf(maxDay));
        System.out.print(df.format(cal.getTime()));

        String[] get_date_split = df.format(cal.getTime()).split("-");
        String get_year = get_date_split[0];
        Log.d("get_Custom year is ", get_year);
        String get_month = get_date_split[1];
        Log.d("get_Custom month is ", get_month);
        String get_date_s = get_date_split[2];
        Log.d("get_Custom month is ", get_date_s);
//-----------------------------------------------------------------------------------------------------
        int get_yearint = Integer.parseInt(get_year);
        Log.d("get_yearint_Custom yeaR is ", String.valueOf(get_yearint));
        int come_yearint = Integer.parseInt(year);
        Log.d("come_yearint _Custom year is ", String.valueOf(come_yearint));
        if (come_yearint >= get_yearint) {
            Log.i("if ", "True");
//-----------------------------------------------------------------------------------------------------
            for (int ii = 1; ii < maxDay + 1; ii++) {
                cal.set(Calendar.DAY_OF_MONTH, ii);
                System.out.print(", " + df.format(cal.getTime()));
                Log.d("Month Date is-> ", df.format(cal.getTime()));
//========================================================================================================
                for (int i = 0; i < daysList.size(); i++) {
                    String appointmentDate = daysList.get(i).toString();

                    Log.d("dateis ", appointmentDate);

                    String[] parts = appointmentDate.split(",");
                    String partDate = parts[0]; // 004
                    //   String Appointmentstatus = parts[1]; // 034556
                    // Log.d("dateCount", partDate);
                    // Log.d("dateCount", Appointmentstatus);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date greenDate = format.parse(appointmentDate);

                        final Calendar currentCalendar = getTodaysCalendar();
                        // currentCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
                        currentCalendar.setTime(greenDate);

                        if (partDate.equals(df.format(cal.getTime()))) {
                            // Log.d("if-Date is ", "if Excute..");

                            viewn = getDayOfMonthText(currentCalendar);
                            //view.setLayoutParams(new ViewGroup.LayoutParams(24,24));
                            Drawable image;

                            if (status.equals("UnAvailable")) {
                                int color = Color.parseColor("#FE2E2E");
                                view.setBackground(getResources().getDrawable(R.drawable.ic_new));
                            }/*else if (Appointmentstatus.equals("processing")){
                                int color = Color.parseColor("#FFFF00");
                                view.setBackground(getResources().getDrawable(R.drawable.ic_circle_yellow));
                            }else if (Appointmentstatus.equals("available")||Appointmentstatus.equals("Available")){
                                int color = Color.parseColor("#80FF00");
                                view.setBackground(getResources().getDrawable(R.drawable.ic_circle_green));
                            }else if (status.equals("selected")){
                                view.setBackground(getResources().getDrawable(R.drawable.ic_circle_blue));

                            }
*/

                            //view.setBackgroundColor(Color.parseColor("#008000"));
                            //view.setTextColor(Color.WHITE);
                          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//
                                view.setBackground(image);
                            }*/


                            markDayAsCurrentDay(currentCalendar);
                            //Log.d("if-Date is 0 ","if Excute End..");

                        } else {
                            //  Log.d("Date is ", "Else Excute..");
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }

//========================================================================================================
            }
//-----------------------------------------------------------------------------------------------------
            //else
        } else {
            Log.i("else ", "False");
        }
//-----------------------------------------------------------------------------------------------------
    }

    @SuppressLint("LongLogTag")

    public void setBackgroundColorOfRedOrGreenNew(ArrayList<String> daysList, String date, String status) {

        System.out.print("daylist" + daysList.size());
        String[] date_split = date.split("-");
        String year = date_split[0];
        Log.d("Come year is ", year);
        String month = date_split[1];
        Log.d("Come month is ", month);
        String date_s = date_split[2];
        Log.d("Come month is ", date_s);

        int yearint = Integer.parseInt(year);
        Log.d("Come monthint is ", String.valueOf(yearint));

        int monthint = Integer.parseInt(month);
        Log.d("Come monthint is ", String.valueOf(monthint));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearint);
        Log.d("0 ", "0");
        cal.set(Calendar.MONTH, monthint);
        Log.d("0 ", "1");
        cal.set(Calendar.DAY_OF_MONTH, 0);
        Log.d("0 ", "2");
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("0 ", "3");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("0 ", String.valueOf(maxDay));
        System.out.print(df.format(cal.getTime()));

        String[] get_date_split = df.format(cal.getTime()).split("-");
        String get_year = get_date_split[0];
        Log.d("get_Custom year is ", get_year);
        String get_month = get_date_split[1];
        Log.d("get_Custom month is ", get_month);
        String get_date_s = get_date_split[2];
        Log.d("get_Custom month is ", get_date_s);
//-----------------------------------------------------------------------------------------------------
        int get_yearint = Integer.parseInt(get_year);
        Log.d("get_yearint_Custom yeaR is ", String.valueOf(get_yearint));
        int come_yearint = Integer.parseInt(year);
        Log.d("come_yearint _Custom year is ", String.valueOf(come_yearint));
        if (come_yearint >= get_yearint) {
            Log.i("if ", "True");
//-----------------------------------------------------------------------------------------------------
            for (int ii = 1; ii < maxDay + 1; ii++) {
                cal.set(Calendar.DAY_OF_MONTH, ii);
                System.out.print(", " + df.format(cal.getTime()));
                Log.d("Month Date is-> ", df.format(cal.getTime()));
//========================================================================================================
                for (int i = 0; i < daysList.size(); i++) {
                    String appointmentDate = daysList.get(i).toString();

                    Log.d("dateis ", appointmentDate);


                    // Log.d("dateCount", partDate);
                    // Log.d("dateCount", Appointmentstatus);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date greenDate = format.parse(appointmentDate);

                        final Calendar currentCalendar = getTodaysCalendar();
                        // currentCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
                        currentCalendar.setTime(greenDate);
                        System.out.println("Datendsad,.." + greenDate);
                        Log.d("Month,..", df.format(cal.getTime()));

                        System.out.println("Month,.." + df.format(cal.getTime()));

                        //its in MM/dd/yyyy
                        String newDate = null;
                        /*Date dtDob = new Date(appointmentDate);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            newDate = sdf.format(dtDob);
                        } catch (ParseException e) {
                            System.out.println("ecsddfs.." +e);

                        }*/

                        System.out.println("newDate.." + appointmentDate);
                        if (appointmentDate.equals(df.format(cal.getTime()))) {
                            // Log.d("if-Date is ", "if Excute..");

                            viewn = getDayOfMonthText(currentCalendar);
                            //view.setLayoutParams(new ViewGroup.LayoutParams(24,24));
                            Drawable image;

                            if (status.equals("UnAvailable")) {
                                int color = Color.parseColor("#FE2E2E");
                                viewn.setBackground(getResources().getDrawable(R.drawable.single_dot));
                            }/*else if (Appointmentstatus.equals("processing")){
                                int color = Color.parseColor("#FFFF00");
                                view.setBackground(getResources().getDrawable(R.drawable.ic_circle_yellow));
                            }else if (Appointmentstatus.equals("available")||Appointmentstatus.equals("Available")){
                                int color = Color.parseColor("#80FF00");
                                view.setBackground(getResources().getDrawable(R.drawable.ic_circle_green));
                            }else if (status.equals("selected")){
                                view.setBackground(getResources().getDrawable(R.drawable.ic_circle_blue));

                            }*/


                            //view.setBackgroundColor(Color.parseColor("#008000"));
                            //view.setTextColor(Color.WHITE);
                          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//
                                view.setBackground(image);
                            }*/


                            markDayAsCurrentDay(currentCalendar);
                            //Log.d("if-Date is 0 ","if Excute End..");

                        } else {
                            //  Log.d("Date is ", "Else Excute..");
                        }
                    } catch (ParseException e) {
                        System.out.println("Exceptionsddff.." + e);
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (java.text.ParseException e) {
                        System.out.println("Exceptionnew.." + e);
                        e.printStackTrace();
                    }
                }

//========================================================================================================
            }
//-----------------------------------------------------------------------------------------------------
            //else
        } else {
            Log.i("else ", "False");
        }
//-----------------------------------------------------------------------------------------------------
    }


}