/*=========================================================================*
 * FIH Nanjing Design Center                                               *
 * FILENAME    :  ChooserDialog.java                                       *
 * CREATED BY  :  @author xiao.su.zhuang                                   *
 * CREATED DATE:  2013/7/25                                                *
 *-------------------------------------------------------------------------*
 * PURPOSE: This is an example for file header.                            *
 *                                                                         *
 *                                                                         *
 *=========================================================================*/
package com.fihtdc.smartbracelet.view;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fihtdc.smartbracelet.R;
import com.fihtdc.smartbracelet.wheel.OnWheelChangedListener;
import com.fihtdc.smartbracelet.wheel.OnWheelScrollListener;
import com.fihtdc.smartbracelet.wheel.WheelView;
import com.fihtdc.smartbracelet.wheel.adapters.ArrayWheelAdapter;
import com.fihtdc.smartbracelet.wheel.adapters.NumericWheelAdapter;

public class ChooserDialog extends Dialog implements OnWheelChangedListener, OnWheelScrollListener,
        OnClickListener {

    /**
     * 
     * the length of the value array is 2,value[0] is the actuall value,value[1]
     * stands for unit
     */
    public static interface onCompleteResultLisener {
        void onResult(String[] value, int dialogType);
    }

    private onCompleteResultLisener mOnResultLisener;
    private Context mContext;
    private Resources mResource;
    private int mDialogType;
    private int[] mWheelPosition;
    private String[] mValueAndUnit;

    public static final int CHOOSER_HEIGHT_DIALOG = 1;
    public static final int CHOOSER_WEIGHT_DIALOG = 2;
    public static final int CHOOSER_BIRTHDAY_DIALOG = 3;
    public static final int CHOOSER_ALARM_DIALOG = 3;

    public static final int CHOOSER_HEIGHT_DIALOG_UNIT_IS_CM = 0;
    public static final int CHOOSER_HEIGHT_DIALOG_UNIT_IS_IN = 1;
    public static final int CHOOSER_WEIGHT_DIALOG_UNIT_IS_KG = 0;
    public static final int CHOOSER_WEIGHT_DIALOG_UNIT_IS_LB = 1;
    public static final int CHOOSER_BIRTHDAY_DIALOG_UNIT_IS_DATE = 0;

    // Height cm max
    private static final int HEIGHT_CM_HUNDREDS_PLACE_MAX = 2;
    private static final int HEIGHT_CM_TENS_PLACE_MAX = 7;
    private static final int HEIGHT_CM_UNIT_PLACE_MAX = 2;
    private static final int HEIGHT_CM_MAX = 100 * HEIGHT_CM_HUNDREDS_PLACE_MAX + 10
            * HEIGHT_CM_TENS_PLACE_MAX + HEIGHT_CM_UNIT_PLACE_MAX;

    // Height cm min
    private static final int HEIGHT_CM_HUNDREDS_PLACE_MIN = 0;
    private static final int HEIGHT_CM_TENS_PLACE_MIN = 8;
    private static final int HEIGHT_CM_UNIT_PLACE_MIN = 0;
    private static final int HEIGHT_CM_MIN = 100 * HEIGHT_CM_HUNDREDS_PLACE_MIN + 10
            * HEIGHT_CM_TENS_PLACE_MIN + HEIGHT_CM_UNIT_PLACE_MIN;

    // Height in max
    private static final int HEIGHT_IN_FT_MAX = 8;
    private static final int HEIGHT_IN_IN_MAX = 11;
    private static final int HEIGHT_IN_MAX = 12 * HEIGHT_IN_FT_MAX + HEIGHT_IN_IN_MAX;

    // Height in min
    private static final int HEIGHT_IN_FT_MIN = 2;
    private static final int HEIGHT_IN_IN_MIN = 7;
    private static final int HEIGHT_IN_MIN = 12 * HEIGHT_IN_FT_MIN + HEIGHT_IN_IN_MIN;

    // Weight KG max
    private static final int WEIGHT_KG_HUNDREDS_PLACE_MAX = 2;
    private static final int WEIGHT_KG_TENS_PLACE_MAX = 2;
    private static final int WEIGHT_KG_UNIT_PLACE_MAX = 7;
    private static final int WEIGHT_KG_MAX = 100 * WEIGHT_KG_HUNDREDS_PLACE_MAX + 10
            * WEIGHT_KG_TENS_PLACE_MAX + WEIGHT_KG_UNIT_PLACE_MAX;

    // Weight KG min
    private static final int WEIGHT_KG_HUNDREDS_PLACE_MIN = 0;
    private static final int WEIGHT_KG_TENS_PLACE_MIN = 3;
    private static final int WEIGHT_KG_UNIT_PLACE_MIN = 0;
    private static final int WEIGHT_KG_MIN = 100 * WEIGHT_KG_HUNDREDS_PLACE_MIN + 10
            * WEIGHT_KG_TENS_PLACE_MIN + WEIGHT_KG_UNIT_PLACE_MIN;

    // Weight LB max
    private static final int WEIGHT_LB_HUNDREDS_PLACE_MAX = 4;
    private static final int WEIGHT_LB_TENS_PLACE_MAX = 9;
    private static final int WEIGHT_LB_UNIT_PLACE_MAX = 9;
    private static final int WEIGHT_LB_MAX = 100 * WEIGHT_LB_HUNDREDS_PLACE_MAX + 10
            * WEIGHT_LB_TENS_PLACE_MAX + WEIGHT_LB_UNIT_PLACE_MAX;

    // Weight LB min
    private static final int WEIGHT_LB_HUNDREDS_PLACE_MIN = 0;
    private static final int WEIGHT_LB_TENS_PLACE_MIN = 6;
    private static final int WEIGHT_LB_UNIT_PLACE_MIN = 6;
    private static final int WEIGHT_LB_MIN = 100 * WEIGHT_LB_HUNDREDS_PLACE_MIN + 10
            * WEIGHT_LB_TENS_PLACE_MIN + WEIGHT_LB_UNIT_PLACE_MIN;

    /**
     * @param context
     * @param dialogType
     * @param listener
     */
    public ChooserDialog(Context context, int dialogType, String[] valueAndUnit,
            onCompleteResultLisener listener) {
        super(context, R.style.ChooerDialogTheme);
        setCancelable(true);
        mContext = context;
        mDialogType = dialogType;
        // mWheelPosition = position;
        mValueAndUnit = valueAndUnit;
        mOnResultLisener = listener;
        mResource = mContext.getResources();
    }

    private void getHeightWheelShowPos(String[] valueAndUnit) {
        if (valueAndUnit != null && valueAndUnit.length == 2) {
            String value = valueAndUnit[0];
            if (TextUtils.isEmpty(value)) {
                value = "1";
            }
            int unit = Integer.valueOf(valueAndUnit[1]);
            if (unit == CHOOSER_HEIGHT_DIALOG_UNIT_IS_CM) {
                int length = value.length();
                int pos0 = 0;
                int pos1 = 0;
                int pos2 = 0;
                switch (length) {
                case 3:
                    pos0 = Integer.valueOf(String.valueOf(value.charAt(0)));
                    pos1 = Integer.valueOf(String.valueOf(value.charAt(1)));
                    pos2 = Integer.valueOf(String.valueOf(value.charAt(2)));
                    break;
                case 2:
                    pos1 = Integer.valueOf(String.valueOf(value.charAt(0)));
                    pos2 = Integer.valueOf(String.valueOf(value.charAt(1)));
                    break;
                case 1:
                    pos2 = Integer.valueOf(String.valueOf(value.charAt(0)));
                    break;
                default:
                    break;
                }
                mWheelPosition = new int[] { pos0 - mLocation0CmStartPos,
                        pos1 - mLocation1CmStartPos, pos2 - mLocation2CmStartPos, 0 };
            } else if (unit == CHOOSER_HEIGHT_DIALOG_UNIT_IS_IN) {
                int valueWhenUintIn = Integer.valueOf(value);
                int ftPartValue = valueWhenUintIn / 12;
                int inPartValue = valueWhenUintIn % 12;
                mWheelPosition = new int[] { ftPartValue - mLocation0InStartPos, 0,
                        inPartValue - mLocation2InStartPos, 1 };
            }

        }

    }

    private void getWeightWheelShowPos(String[] valueAndUnit) {
        if (valueAndUnit != null && valueAndUnit.length == 2) {
            String value = valueAndUnit[0];
            if (TextUtils.isEmpty(value)) {
                value = "1";
            }
            int unit = Integer.valueOf(valueAndUnit[1]);
            int length = value.length();
            int pos0 = 0;
            int pos1 = 0;
            int pos2 = 0;
            switch (length) {
            case 3:
                pos0 = Integer.valueOf(String.valueOf(value.charAt(0)));
                pos1 = Integer.valueOf(String.valueOf(value.charAt(1)));
                pos2 = Integer.valueOf(String.valueOf(value.charAt(2)));
                break;
            case 2:
                pos1 = Integer.valueOf(String.valueOf(value.charAt(0)));
                pos2 = Integer.valueOf(String.valueOf(value.charAt(1)));
                break;
            case 1:
                pos2 = Integer.valueOf(String.valueOf(value.charAt(0)));
                break;
            default:
                break;
            }

            if (unit == CHOOSER_WEIGHT_DIALOG_UNIT_IS_KG) {
                mWheelPosition = new int[] { pos0 - mLocation0KgStartPos,
                        pos1 - mLocation1KgStartPos, pos2 - mLocation2KgStartPos, 0 };
            } else if (unit == CHOOSER_WEIGHT_DIALOG_UNIT_IS_LB) {
                mWheelPosition = new int[] { pos0 - mLocation0LbStartPos,
                        pos1 - mLocation1LbStartPos, pos2 - mLocation2LbStartPos, 1 };
            }

        }

    }

    private int mBirthdayYearBegin = 1900;
    private int mBirthdayMonthBegin = 1;
    private int mBirthdayDayBegin = 1;

    private void getBirthdayWheelShowPos(String[] valueAndUnit) {
        if (valueAndUnit != null) {
            String value = valueAndUnit[0];
            if (TextUtils.isEmpty(value) || !value.contains(",")) {
                Calendar now = Calendar.getInstance();
                value = now.get(Calendar.YEAR) + "," + (now.get(Calendar.MONTH) + 1) + ","
                        + now.get(Calendar.DAY_OF_MONTH);
            }
            int firstDivisionIndex = value.indexOf(",");
            int lastDivisionIndex = value.lastIndexOf(",");
            String year = value.substring(0, firstDivisionIndex);
            String month = value.substring(firstDivisionIndex + 1, lastDivisionIndex);
            String day = value.substring(lastDivisionIndex + 1, value.length());
            int yearIntValue = Integer.valueOf(year);
            int monthIntValue = Integer.valueOf(month);
            int dayIntValue = Integer.valueOf(day);
            mWheelPosition = new int[] { yearIntValue - mBirthdayYearBegin,
                    monthIntValue - mBirthdayMonthBegin, dayIntValue - mBirthdayDayBegin };
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser_dialog);
        initView();
        Window theWindow = getWindow();
        theWindow.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        theWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private WheelView mLocation0WV;
    private WheelView mLocation1WV;
    private WheelView mLocation2WV;
    private WheelView mLocation3WV;

    private void judgeWhetherHeightLegal(int[] wheelPosition) {
        if (wheelPosition == null || wheelPosition.length != 4) {
            wheelPosition = new int[4];
            resetCmWheelPositionToMin(wheelPosition);
            mWheelPosition = wheelPosition;
            return;
        }

        if (wheelPosition[3] == 0) {
            if (isHeightCMMin(wheelPosition[0] + mLocation0CmStartPos, wheelPosition[1]
                    + mLocation1CmStartPos, wheelPosition[2] + mLocation2CmStartPos)) {
                resetCmWheelPositionToMin(wheelPosition);
                mWheelPosition = wheelPosition;
            } else if (isHeightCMMin(wheelPosition[0] + mLocation0CmStartPos, wheelPosition[1]
                    + mLocation1CmStartPos, wheelPosition[2] + mLocation2CmStartPos)) {
                resetCmWheelPositionToMax(wheelPosition);
                mWheelPosition = wheelPosition;
            }
            return;
        } else if (wheelPosition[3] == 1) {
            if (isHeightInMin(wheelPosition[0] + mLocation0InStartPos, wheelPosition[2]
                    + mLocation2InStartPos)) {
                resetInWheelPositionToMin(wheelPosition);
                mWheelPosition = wheelPosition;
            } else if (isHeightInMin(wheelPosition[0] + mLocation0InStartPos, wheelPosition[2]
                    + mLocation2InStartPos)) {
                resetInWheelPositionToMax(wheelPosition);
                mWheelPosition = wheelPosition;
            }
            return;
        }

        if (wheelPosition[3] < 0 || wheelPosition[3] > 1) {
            resetCmWheelPositionToMin(wheelPosition);
            mWheelPosition = wheelPosition;
            return;
        }

    }

    private void judgeWhetherWeightLegal(int[] wheelPosition) {
        if (wheelPosition == null || wheelPosition.length != 4) {
            wheelPosition = new int[4];
            resetKgWheelPositionToMin(wheelPosition);
            mWheelPosition = wheelPosition;
            return;
        }

        if (wheelPosition[3] == 0) {
            if (isWeightKGMin(wheelPosition[0] + mLocation0KgStartPos, wheelPosition[1]
                    + mLocation1KgStartPos, wheelPosition[2] + mLocation2KgStartPos)) {
                resetKgWheelPositionToMin(wheelPosition);
                mWheelPosition = wheelPosition;
            } else if (isWeightKGMax(wheelPosition[0] + mLocation0KgStartPos, wheelPosition[1]
                    + mLocation1KgStartPos, wheelPosition[2] + mLocation2KgStartPos)) {
                resetKgWheelPositionToMax(wheelPosition);
                mWheelPosition = wheelPosition;
            }
            return;
        } else if (wheelPosition[3] == 1) {
            if (isWeightLBMin(wheelPosition[0] + mLocation0LbStartPos, wheelPosition[1]
                    + mLocation1LbStartPos, wheelPosition[2] + mLocation2LbStartPos)) {
                resetLbWheelPositionToMin(wheelPosition);
                mWheelPosition = wheelPosition;
            } else if (isWeightLBMin(wheelPosition[0] + mLocation0LbStartPos, wheelPosition[1]
                    + mLocation1LbStartPos, wheelPosition[2] + mLocation2LbStartPos)) {
                resetLbWheelPositionToMax(wheelPosition);
                mWheelPosition = wheelPosition;
            }
            return;
        }

        if (wheelPosition[3] < 0 || wheelPosition[3] > 1) {
            resetKgWheelPositionToMin(wheelPosition);
            mWheelPosition = wheelPosition;
            return;
        }

    }

    private void judgeWhetherBirthdayLegal(int[] wheelPosition) {
        Calendar now = Calendar.getInstance();
        int cmPos0 = mLocation0WV.getCurrentItem();
        int cmPos1 = mLocation1WV.getCurrentItem();
        int cmPos2 = mLocation2WV.getCurrentItem();
        int year = mBirthdayYearBegin + cmPos0;
        int month = mBirthdayMonthBegin + cmPos1 - 1;
        int day = mBirthdayDayBegin + cmPos2;

        Calendar select = Calendar.getInstance();
        select.set(year, month, day);
        Calendar standard = Calendar.getInstance();
        standard.set(Calendar.YEAR, year);
        standard.set(Calendar.MONTH, month);
        int maxDay = standard.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        //WheelPosition is not right or date after today
        if (wheelPosition == null || wheelPosition.length != 3 || select.after(now)) {
            wheelPosition[0] = now.get(Calendar.YEAR) - mBirthdayYearBegin;
            wheelPosition[1] = now.get(Calendar.MONTH) - mBirthdayMonthBegin + 1;
            wheelPosition[2] = now.get(Calendar.DAY_OF_MONTH) - mBirthdayDayBegin;
            wheelPosition = new int[] { wheelPosition[0], wheelPosition[1], wheelPosition[2] };
            mWheelPosition = wheelPosition;
            
            mLocation0WV.setCurrentItem(wheelPosition[0]);
            mLocation1WV.setCurrentItem(wheelPosition[1]);
            mLocation2WV.setCurrentItem(wheelPosition[2]);
        } else if (day > maxDay) {
            //Selected day of month is not exist
            wheelPosition[2] = maxDay - mBirthdayDayBegin;
            wheelPosition = new int[] { wheelPosition[0], wheelPosition[1], wheelPosition[2] };
            mWheelPosition = wheelPosition;
            mLocation2WV.setCurrentItem(wheelPosition[2]);
        }
    }


    private void resetCmWheelPositionToMin(int[] wheelPosition) {
        wheelPosition[0] = HEIGHT_CM_HUNDREDS_PLACE_MIN;
        wheelPosition[1] = HEIGHT_CM_TENS_PLACE_MIN;
        wheelPosition[2] = HEIGHT_CM_UNIT_PLACE_MIN;
        wheelPosition[3] = 0;
    }

    private void resetCmWheelPositionToMax(int[] wheelPosition) {
        wheelPosition[0] = HEIGHT_CM_HUNDREDS_PLACE_MAX;
        wheelPosition[1] = HEIGHT_CM_TENS_PLACE_MAX;
        wheelPosition[2] = HEIGHT_CM_UNIT_PLACE_MAX;
        wheelPosition[3] = 0;
    }

    private void resetInWheelPositionToMin(int[] wheelPosition) {
        wheelPosition[0] = 0;
        wheelPosition[1] = 0;
        wheelPosition[2] = HEIGHT_IN_IN_MIN;
        wheelPosition[3] = 1;
    }

    private void resetInWheelPositionToMax(int[] wheelPosition) {
        wheelPosition[0] = mLocation0InEndPos - mLocation0InStartPos;
        wheelPosition[1] = 0;
        wheelPosition[2] = HEIGHT_IN_IN_MAX;
        wheelPosition[3] = 1;
    }

    private void resetKgWheelPositionToMin(int[] wheelPosition) {
        wheelPosition[0] = WEIGHT_KG_HUNDREDS_PLACE_MIN;
        wheelPosition[1] = WEIGHT_KG_TENS_PLACE_MIN;
        wheelPosition[2] = WEIGHT_KG_UNIT_PLACE_MIN;
        wheelPosition[3] = 0;
    }

    private void resetKgWheelPositionToMax(int[] wheelPosition) {
        wheelPosition[0] = WEIGHT_KG_HUNDREDS_PLACE_MAX;
        wheelPosition[1] = WEIGHT_KG_TENS_PLACE_MAX;
        wheelPosition[2] = WEIGHT_KG_UNIT_PLACE_MAX;
        wheelPosition[3] = 0;
    }

    private void resetLbWheelPositionToMin(int[] wheelPosition) {
        wheelPosition[0] = WEIGHT_LB_HUNDREDS_PLACE_MIN;
        wheelPosition[1] = WEIGHT_LB_TENS_PLACE_MIN;
        wheelPosition[2] = WEIGHT_LB_UNIT_PLACE_MIN;
        wheelPosition[3] = 0;
    }

    private void resetLbWheelPositionToMax(int[] wheelPosition) {
        wheelPosition[0] = WEIGHT_LB_HUNDREDS_PLACE_MAX;
        wheelPosition[1] = WEIGHT_LB_TENS_PLACE_MAX;
        wheelPosition[2] = WEIGHT_LB_UNIT_PLACE_MAX;
        wheelPosition[3] = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.done:
            switch (mDialogType) {
            case CHOOSER_HEIGHT_DIALOG:
                if (mOnResultLisener != null) {
                    int[] cmPosArray = new int[4];
                    cmPosArray[0] = mLocation0WV.getCurrentItem();
                    cmPosArray[1] = mLocation1WV.getCurrentItem();
                    cmPosArray[2] = mLocation2WV.getCurrentItem();
                    cmPosArray[3] = mLocation3WV.getCurrentItem();
                    String[] value = getCurrentValueAccordingPos(cmPosArray, CHOOSER_HEIGHT_DIALOG);
                    mOnResultLisener.onResult(value, mDialogType);
                }
                break;
            case CHOOSER_WEIGHT_DIALOG:
                if (mOnResultLisener != null) {
                    int[] cmPosArray = new int[4];
                    cmPosArray[0] = mLocation0WV.getCurrentItem();
                    cmPosArray[1] = mLocation1WV.getCurrentItem();
                    cmPosArray[2] = mLocation2WV.getCurrentItem();
                    cmPosArray[3] = mLocation3WV.getCurrentItem();
                    String[] value = getCurrentValueAccordingPos(cmPosArray, CHOOSER_WEIGHT_DIALOG);
                    mOnResultLisener.onResult(value, mDialogType);
                }
                break;
            case CHOOSER_BIRTHDAY_DIALOG:
                if (mOnResultLisener != null) {
                    int[] cmPosArray = new int[3];
                    cmPosArray[0] = mLocation0WV.getCurrentItem();
                    cmPosArray[1] = mLocation1WV.getCurrentItem();
                    cmPosArray[2] = mLocation2WV.getCurrentItem();
                    String[] value = getCurrentValueAccordingPos(cmPosArray,
                            CHOOSER_BIRTHDAY_DIALOG);
                    mOnResultLisener.onResult(value, mDialogType);
                }
                break;

            default:
                break;
            }
            break;

        default:
            break;
        }
        dismiss();
    }

    private TextView mDialogTitleTV;
    private ImageView mDoneBtn;
    private String[] mBirthdayYArray;
    private String[] mBirthdayMArray;
    private String[] mBirthdayDArray;

    private void initView() {
        mLocation0WV = (WheelView) findViewById(R.id.location0);
        mLocation1WV = (WheelView) findViewById(R.id.location1);
        mLocation2WV = (WheelView) findViewById(R.id.location2);
        mLocation3WV = (WheelView) findViewById(R.id.location3);
        mDialogTitleTV = (TextView) findViewById(R.id.title);
        mDoneBtn = (ImageView) findViewById(R.id.done);
        mDoneBtn.setOnClickListener(this);
        switch (mDialogType) {
        case CHOOSER_HEIGHT_DIALOG:
            initTitleText(R.string.chooser_height_dialog_title);
            getHeightWheelShowPos(mValueAndUnit);
            judgeWhetherHeightLegal(mWheelPosition);
            wheelCashPosition(mWheelPosition);
            initChooerHeightDialogData();
            break;
        case CHOOSER_WEIGHT_DIALOG:
            initTitleText(R.string.chooser_weight_dialog_title);
            getWeightWheelShowPos(mValueAndUnit);
            judgeWhetherWeightLegal(mWheelPosition);
            wheelCashPosition(mWheelPosition);
            initChooerWeightDialogData();
            break;
        case CHOOSER_BIRTHDAY_DIALOG:
            mLocation3WV.setVisibility(View.GONE);
            mLocation0WV.setLayoutParams(new LayoutParams(mResource
                    .getDimensionPixelSize(R.dimen.birthday_wheel_year_width),
                    LayoutParams.WRAP_CONTENT));
            mLocation1WV.setLayoutParams(new LayoutParams(mResource
                    .getDimensionPixelSize(R.dimen.birthday_wheel_month_width),
                    LayoutParams.WRAP_CONTENT));
            mLocation2WV.setLayoutParams(new LayoutParams(mResource
                    .getDimensionPixelSize(R.dimen.birthday_wheel_month_width),
                    LayoutParams.WRAP_CONTENT));
            initTitleText(R.string.chooser_birthday_dialog_title);
            initBirthdayData();
            getBirthdayWheelShowPos(mValueAndUnit);
            judgeWhetherBirthdayLegal(mWheelPosition);
            wheelCashPosition(mWheelPosition);
            initChooerBirthdayDialogData();
            break;

        default:
            break;
        }
        mLocation0WV.addChangingListener(this);
        mLocation0WV.addScrollingListener(this);

        mLocation1WV.addChangingListener(this);
        mLocation1WV.addScrollingListener(this);

        mLocation2WV.addChangingListener(this);
        mLocation2WV.addScrollingListener(this);

        mLocation3WV.addChangingListener(this);
        mLocation3WV.addScrollingListener(this);
    }

    private void initBirthdayData() {
        mBirthdayYArray = mContext.getResources().getStringArray(R.array.profile_birthday_year);
        mBirthdayMArray = mContext.getResources().getStringArray(R.array.profile_birthday_month);
        mBirthdayDArray = mContext.getResources().getStringArray(R.array.profile_birthday_day);
    }

    private void initTitleText(int StringId) {
        mDialogTitleTV.setText(StringId);
    }

    private void wheelCashPosition(int[] wheelPosition) {
        if (mDialogType != CHOOSER_BIRTHDAY_DIALOG) {
            mInitLocation3Indes = wheelPosition[3];

            if (mInitLocation3Indes == 0) {
                mInitLocation0Data0Indes = wheelPosition[0];
                mInitLocation1Data0Indes = wheelPosition[1];
                mInitLocation2Data0Indes = wheelPosition[2];
            } else {
                mInitLocation0Data1Indes = wheelPosition[0];
                mInitLocation1Data1Indes = wheelPosition[1];
                mInitLocation2Data1Indes = wheelPosition[2];
            }
        } else {
            mInitLocation0Data0Indes = wheelPosition[0];
            mInitLocation1Data0Indes = wheelPosition[1];
            mInitLocation2Data0Indes = wheelPosition[2];
        }

    }

    /**
     * @author F3060905 at 2013/7/25
     * @return int[]
     * @param @param cmLoactions the length is four,and the first three digits
     *        represent the value,the end digit represent unit
     * @param @return
     * @Description: TODO
     * @throws
     */
    private static int[] transformCmToIn(int[] cmLoactions) {
        if (cmLoactions == null || cmLoactions.length <= 0) {
            return null;
        }
        int pos0Value = mLocation0CmStartPos + cmLoactions[0];
        int pos1Value = mLocation1CmStartPos + cmLoactions[1];
        int pos2Value = mLocation2CmStartPos + cmLoactions[2];
        int value = 0;
        StringBuffer buffer = new StringBuffer();
        buffer.append(pos0Value).append(pos1Value).append(pos2Value);
        value = Integer.valueOf(buffer.toString());
        float inchValue = (float) (value / 2.54);

        if (inchValue > HEIGHT_IN_MAX) {
            inchValue = HEIGHT_IN_MAX;
        } else if (value < HEIGHT_IN_MIN) {
            inchValue = HEIGHT_IN_MIN;
        }

        int ftLocationValue = (int) (inchValue / 12);
        int inchLoactionValue = (int) Math.floor(inchValue % 12);
        int ftPos = ftLocationValue - mLocation0InStartPos;
        int inPos = inchLoactionValue - mLocation2InStartPos;
        return new int[] { ftPos, 0, inPos, 1 };
    }

    private static int[] transformInToCm(int[] inLoactions) {
        if (inLoactions == null || inLoactions.length <= 0) {
            return null;
        }
        int pos0Value = mLocation0InStartPos + inLoactions[0];
        int pos2Value = mLocation2InStartPos + inLoactions[2];
        int totalInchValue = pos0Value * 12 + pos2Value;
        int transformedCmvalue = (int) Math.ceil(totalInchValue * 2.54);

        if (transformedCmvalue > HEIGHT_CM_MAX) {
            transformedCmvalue = HEIGHT_CM_MAX;
        } else if (transformedCmvalue < HEIGHT_CM_MIN) {
            transformedCmvalue = HEIGHT_CM_MIN;
        }

        String transformedCmvalueStr = String.valueOf(transformedCmvalue);
        int length = transformedCmvalueStr.length();
        int transformedCmPos0Value = 0;
        int transformedCmPos1Value = 0;
        int transformedCmPos2Value = 0;
        switch (length) {
        case 3:
            transformedCmPos0Value = Integer
                    .valueOf(String.valueOf(transformedCmvalueStr.charAt(0)));
            transformedCmPos1Value = Integer
                    .valueOf(String.valueOf(transformedCmvalueStr.charAt(1)));
            transformedCmPos2Value = Integer
                    .valueOf(String.valueOf(transformedCmvalueStr.charAt(2)));
            break;
        case 2:
            transformedCmPos1Value = Integer
                    .valueOf(String.valueOf(transformedCmvalueStr.charAt(0)));
            transformedCmPos2Value = Integer
                    .valueOf(String.valueOf(transformedCmvalueStr.charAt(1)));
            break;
        case 1:
            transformedCmPos2Value = Integer
                    .valueOf(String.valueOf(transformedCmvalueStr.charAt(0)));
            break;
        default:
            break;
        }
        int cmPos0 = transformedCmPos0Value - mLocation0CmStartPos;
        int cmPos1 = transformedCmPos1Value - mLocation1CmStartPos;
        int cmPos2 = transformedCmPos2Value - mLocation2CmStartPos;
        return new int[] { cmPos0, cmPos1, cmPos2, 0 };
    }

    private static int[] transformLbToKg(int[] lbLoactions) {
        if (lbLoactions == null || lbLoactions.length <= 0) {
            return null;
        }
        int pos0Value = mLocation0LbStartPos + lbLoactions[0];
        int pos1Value = mLocation1LbStartPos + lbLoactions[1];
        int pos2Value = mLocation2LbStartPos + lbLoactions[2];
        StringBuffer buffer = new StringBuffer();
        buffer.append(pos0Value).append(pos1Value).append(pos2Value);
        int lbvalue = Integer.valueOf(buffer.toString());
        int kgValue = (int) Math.ceil(0.45359 * lbvalue);

        if (kgValue > WEIGHT_KG_MAX) {
            kgValue = WEIGHT_KG_MAX;
        } else if (kgValue < WEIGHT_KG_MIN) {
            kgValue = WEIGHT_KG_MIN;
        }

        String transformedKgvalueStr = String.valueOf(kgValue);
        int length = transformedKgvalueStr.length();
        int transformedKgPos0Value = 0;
        int transformedKgPos1Value = 0;
        int transformedKgPos2Value = 0;
        switch (length) {
        case 3:
            transformedKgPos0Value = Integer
                    .valueOf(String.valueOf(transformedKgvalueStr.charAt(0)));
            transformedKgPos1Value = Integer
                    .valueOf(String.valueOf(transformedKgvalueStr.charAt(1)));
            transformedKgPos2Value = Integer
                    .valueOf(String.valueOf(transformedKgvalueStr.charAt(2)));
            break;
        case 2:
            transformedKgPos1Value = Integer
                    .valueOf(String.valueOf(transformedKgvalueStr.charAt(0)));
            transformedKgPos2Value = Integer
                    .valueOf(String.valueOf(transformedKgvalueStr.charAt(1)));
            break;
        case 1:
            transformedKgPos2Value = Integer
                    .valueOf(String.valueOf(transformedKgvalueStr.charAt(0)));
            break;
        default:
            break;
        }
        int cmPos0 = transformedKgPos0Value - mLocation0KgStartPos;
        int cmPos1 = transformedKgPos1Value - mLocation1KgStartPos;
        int cmPos2 = transformedKgPos2Value - mLocation2KgStartPos;
        return new int[] { cmPos0, cmPos1, cmPos2, 0 };
    }

    private static int[] transformKgToLb(int[] kgLoactions) {
        if (kgLoactions == null || kgLoactions.length <= 0) {
            return null;
        }
        int pos0Value = mLocation0KgStartPos + kgLoactions[0];
        int pos1Value = mLocation1KgStartPos + kgLoactions[1];
        int pos2Value = mLocation2KgStartPos + kgLoactions[2];
        StringBuffer buffer = new StringBuffer();
        buffer.append(pos0Value).append(pos1Value).append(pos2Value);
        int kgvalue = Integer.valueOf(buffer.toString());
        int lbValue = (int) Math.floor(2.2046 * kgvalue);

        if (lbValue > WEIGHT_LB_MAX) {
            lbValue = WEIGHT_LB_MAX;
        } else if (lbValue < WEIGHT_LB_MIN) {
            lbValue = WEIGHT_LB_MIN;
        }

        String transformedlbvalueStr = String.valueOf(lbValue);
        int length = transformedlbvalueStr.length();
        int transformedLbPos0Value = 0;
        int transformedLbPos1Value = 0;
        int transformedLbPos2Value = 0;

        switch (length) {
        case 3:
            transformedLbPos0Value = Integer
                    .valueOf(String.valueOf(transformedlbvalueStr.charAt(0)));
            transformedLbPos1Value = Integer
                    .valueOf(String.valueOf(transformedlbvalueStr.charAt(1)));
            transformedLbPos2Value = Integer
                    .valueOf(String.valueOf(transformedlbvalueStr.charAt(2)));
            break;
        case 2:
            transformedLbPos1Value = Integer
                    .valueOf(String.valueOf(transformedlbvalueStr.charAt(0)));
            transformedLbPos2Value = Integer
                    .valueOf(String.valueOf(transformedlbvalueStr.charAt(1)));
            break;
        case 1:
            transformedLbPos2Value = Integer
                    .valueOf(String.valueOf(transformedlbvalueStr.charAt(0)));
            break;
        default:
            break;
        }
        int cmPos0 = transformedLbPos0Value - mLocation0LbStartPos;
        int cmPos1 = transformedLbPos1Value - mLocation1LbStartPos;
        int cmPos2 = transformedLbPos2Value - mLocation2LbStartPos;
        return new int[] { cmPos0, cmPos1, cmPos2, 1 };
    }

    private String[] getCurrentValueAccordingPos(int[] wheelsPos, int dialogType) {
        if (wheelsPos != null) {
            String[] valueAndUnit = new String[2];
            switch (dialogType) {
            case CHOOSER_HEIGHT_DIALOG:
                if (wheelsPos[3] == 0) {
                    StringBuffer heightValueWhenCm = new StringBuffer();
                    heightValueWhenCm.append(mLocation0CmStartPos + wheelsPos[0])
                            .append(mLocation1CmStartPos + wheelsPos[1])
                            .append(mLocation2CmStartPos + wheelsPos[2]);
                    valueAndUnit[0] = String.valueOf(Integer.valueOf(heightValueWhenCm.toString()));
                    valueAndUnit[1] = String.valueOf(CHOOSER_HEIGHT_DIALOG_UNIT_IS_CM);
                } else {
                    int ftPart = mLocation0InStartPos + wheelsPos[0];
                    int inPart = mLocation2InStartPos + wheelsPos[2];
                    int whenUnitIsIn = ftPart * 12 + inPart;
                    valueAndUnit[0] = String.valueOf(whenUnitIsIn);
                    valueAndUnit[1] = String.valueOf(CHOOSER_HEIGHT_DIALOG_UNIT_IS_IN);
                }
                return valueAndUnit;
            case CHOOSER_WEIGHT_DIALOG:
                if (wheelsPos[3] == 0) {
                    StringBuffer wdightValueWhenKg = new StringBuffer();
                    wdightValueWhenKg.append(mLocation0KgStartPos + wheelsPos[0])
                            .append(mLocation1KgStartPos + wheelsPos[1])
                            .append(mLocation2KgStartPos + wheelsPos[2]);
                    valueAndUnit[0] = String.valueOf(Integer.valueOf(wdightValueWhenKg.toString()));
                    valueAndUnit[1] = String.valueOf(CHOOSER_WEIGHT_DIALOG_UNIT_IS_KG);
                } else {
                    StringBuffer wdightValueWhenKg = new StringBuffer();
                    wdightValueWhenKg.append(mLocation0LbStartPos + wheelsPos[0])
                            .append(mLocation1LbStartPos + wheelsPos[1])
                            .append(mLocation2LbStartPos + wheelsPos[2]);
                    valueAndUnit[0] = String.valueOf(Integer.valueOf(wdightValueWhenKg.toString()));
                    valueAndUnit[1] = String.valueOf(CHOOSER_WEIGHT_DIALOG_UNIT_IS_LB);
                }
                return valueAndUnit;
            case CHOOSER_BIRTHDAY_DIALOG:
                StringBuffer birthdayValue = new StringBuffer();
                birthdayValue.append(mBirthdayYearBegin + wheelsPos[0]).append(",")
                        .append(mBirthdayMonthBegin + wheelsPos[1]).append(",")
                        .append(mBirthdayDayBegin + wheelsPos[2]);
                valueAndUnit[0] = birthdayValue.toString();
                valueAndUnit[1] = String.valueOf(CHOOSER_BIRTHDAY_DIALOG_UNIT_IS_DATE);
                return valueAndUnit;
            default:
                break;
            }
        }
        return null;

    }

    private void initChooerHeightDialogData() {
        if (mInitLocation3Indes == 0) {
            whenUnitIsCm();
        } else {
            whenUnitIsIn();
        }
        setHeightUnitData();
        mLocation3WV.setCurrentItem(mInitLocation3Indes);
    }

    private void initChooerWeightDialogData() {
        if (mInitLocation3Indes == 0) {
            whenUnitIsKg();

        } else {
            whenUnitIsLb();
        }
        setWeightUnitData();
        mLocation3WV.setCurrentItem(mInitLocation3Indes);
    }

    private void setWeightUnitData() {
        ArrayWheelAdapter<String> weightAdapter = new ArrayWheelAdapter<String>(mContext,
                new String[] { mContext.getString(R.string.profile_unit_kg),mContext.getString(R.string.profile_unit_lb) });
        weightAdapter.setItemResource(R.layout.wheel_text_item);
        weightAdapter.setItemTextResource(R.id.text);
        mLocation3WV.setViewAdapter(weightAdapter);
    }

    private void setHeightUnitData() { 
        ArrayWheelAdapter<String> heightAdapter = new ArrayWheelAdapter<String>(mContext,
                new String[] { mContext.getString(R.string.profile_unit_cm), mContext.getString(R.string.profile_unit_in) });
        heightAdapter.setItemResource(R.layout.wheel_text_item);
        heightAdapter.setItemTextResource(R.id.text);
        mLocation3WV.setViewAdapter(heightAdapter);
    }

    private void initChooerBirthdayDialogData() {
        ArrayWheelAdapter<String> birthdayYAdapter = new ArrayWheelAdapter<String>(mContext,
                mBirthdayYArray);
        birthdayYAdapter.setItemResource(R.layout.wheel_text_item);
        birthdayYAdapter.setItemTextResource(R.id.text);
        mLocation0WV.setViewAdapter(birthdayYAdapter);

        ArrayWheelAdapter<String> birthdayMAdapter = new ArrayWheelAdapter<String>(mContext,
                mBirthdayMArray);
        birthdayMAdapter.setItemResource(R.layout.wheel_text_item);
        birthdayMAdapter.setItemTextResource(R.id.text);
        mLocation1WV.setViewAdapter(birthdayMAdapter);

        ArrayWheelAdapter<String> birthdayDAdapter = new ArrayWheelAdapter<String>(mContext,
                mBirthdayDArray);
        birthdayDAdapter.setItemResource(R.layout.wheel_text_item);
        birthdayDAdapter.setItemTextResource(R.id.text);
        mLocation2WV.setViewAdapter(birthdayDAdapter);
        setCurrentData0Item();
    }

    private NumericWheelAdapter mLoaction0Data1Adapter;
    private NumericWheelAdapter mLoaction1Data1Adapter;
    private NumericWheelAdapter mLoaction2Data1Adapter;
    private static int mLocation0InStartPos = 2;
    private static int mLocation0InEndPos = 8;
    private static int mLocation2InStartPos = 0;
    private static int mLocation2InEndPos = 11;

    private static int mLocation0LbStartPos = 0;
    private static int mLocation0LbEndPos = 4;
    private static int mLocation1LbStartPos = 0;
    private static int mLocation1LbEndPos = 9;
    private static int mLocation2LbStartPos = 0;
    private static int mLocation2LbEndPos = 9;

    private int mInitLocation0Data0Indes;
    private int mInitLocation1Data0Indes;
    private int mInitLocation2Data0Indes;
    private int mInitLocation3Indes;

    private int mInitLocation0Data1Indes;
    private int mInitLocation1Data1Indes;
    private int mInitLocation2Data1Indes;

    private void whenUnitIsIn() {
        if (mLoaction0Data1Adapter == null) {
            mLoaction0Data1Adapter = new NumericWheelAdapter(mContext, mLocation0InStartPos,
                    mLocation0InEndPos);
        }
        mLoaction0Data1Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction0Data1Adapter.setItemTextResource(R.id.text);
        mLocation0WV.setViewAdapter(mLoaction0Data1Adapter);
        if (mInitLocation0Data0Indes < 0
                || mInitLocation0Data0Indes > (mLocation0InEndPos - mLocation0InStartPos)) {
            mInitLocation0Data0Indes = 0;
        }
        setFtUnitToPos1();
        if (mLoaction2Data1Adapter == null) {
            mLoaction2Data1Adapter = new NumericWheelAdapter(mContext, mLocation2InStartPos,
                    mLocation2InEndPos);
        }
        mLoaction2Data1Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction2Data1Adapter.setItemTextResource(R.id.text);
        mLocation2WV.setViewAdapter(mLoaction2Data1Adapter);
        setCurrentData1Item();

    }

    private void whenUnitIsLb() {
        if (mLoaction0Data1Adapter == null) {
            mLoaction0Data1Adapter = new NumericWheelAdapter(mContext, mLocation0LbStartPos,
                    mLocation0LbEndPos);
        }
        mLoaction0Data1Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction0Data1Adapter.setItemTextResource(R.id.text);
        mLocation0WV.setViewAdapter(mLoaction0Data1Adapter);

        if (mLoaction1Data1Adapter == null) {
            mLoaction1Data1Adapter = new NumericWheelAdapter(mContext, mLocation1LbStartPos,
                    mLocation1LbEndPos);
        }
        mLoaction1Data1Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction1Data1Adapter.setItemTextResource(R.id.text);
        mLocation1WV.setViewAdapter(mLoaction1Data1Adapter);

        if (mLoaction2Data1Adapter == null) {
            mLoaction2Data1Adapter = new NumericWheelAdapter(mContext, mLocation1LbStartPos,
                    mLocation2LbEndPos);
        }
        mLoaction2Data1Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction2Data1Adapter.setItemTextResource(R.id.text);
        mLocation2WV.setViewAdapter(mLoaction2Data1Adapter);
        setCurrentData1Item();

    }

    private void setCurrentData1Item() {
        mLocation0WV.setCurrentItem(mInitLocation0Data1Indes);
        mLocation1WV.setCurrentItem(mInitLocation1Data1Indes);
        mLocation2WV.setCurrentItem(mInitLocation2Data1Indes);
    }

    private void setFtUnitToPos1() {
        ArrayWheelAdapter<String> ftUnitAdapter = new ArrayWheelAdapter<String>(mContext,
                new String[] { mContext.getString(R.string.profile_unit_ft) });
        ftUnitAdapter.setItemResource(R.layout.wheel_text_item);
        ftUnitAdapter.setItemTextResource(R.id.text);
        mLocation1WV.setViewAdapter(ftUnitAdapter);

    }

    private NumericWheelAdapter mLoaction0Data0Adapter;
    private NumericWheelAdapter mLoaction1Data0Adapter;
    private NumericWheelAdapter mLoaction2Data0Adapter;
    private static int mLocation0CmStartPos = 0;
    private static int mLocation0CmEndPos = 2;
    private static int mLocation1CmStartPos = 0;
    private static int mLocation1CmEndPos = 9;
    private static int mLocation2CmStartPos = 0;
    private static int mLocation2CmEndPos = 9;

    private static int mLocation0KgStartPos = 0;
    private static int mLocation1KgStartPos = 0;
    private static int mLocation2KgStartPos = 0;
    private static int mLocation0KgEndPos = 2;
    private static int mLocation1KgEndPos = 9;
    private static int mLocation2KgEndPos = 9;

    private void whenUnitIsCm() {
        if (mLoaction0Data0Adapter == null) {
            mLoaction0Data0Adapter = new NumericWheelAdapter(mContext, mLocation0CmStartPos,
                    mLocation0CmEndPos);
        }
        mLoaction0Data0Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction0Data0Adapter.setItemTextResource(R.id.text);
        mLocation0WV.setViewAdapter(mLoaction0Data0Adapter);

        if (mLoaction1Data0Adapter == null) {
            mLoaction1Data0Adapter = new NumericWheelAdapter(mContext, mLocation1CmStartPos,
                    mLocation1CmEndPos);
        }
        mLoaction1Data0Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction1Data0Adapter.setItemTextResource(R.id.text);
        mLocation1WV.setViewAdapter(mLoaction1Data0Adapter);

        if (mLoaction2Data0Adapter == null) {
            mLoaction2Data0Adapter = new NumericWheelAdapter(mContext, mLocation2CmStartPos,
                    mLocation2CmEndPos);
        }
        mLoaction2Data0Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction2Data0Adapter.setItemTextResource(R.id.text);
        mLocation2WV.setViewAdapter(mLoaction2Data0Adapter);
        setCurrentData0Item();
    }

    private void whenUnitIsKg() {
        if (mLoaction0Data0Adapter == null) {
            mLoaction0Data0Adapter = new NumericWheelAdapter(mContext, mLocation0KgStartPos,
                    mLocation0KgEndPos);
        }
        mLoaction0Data0Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction0Data0Adapter.setItemTextResource(R.id.text);
        mLocation0WV.setViewAdapter(mLoaction0Data0Adapter);

        if (mLoaction1Data0Adapter == null) {
            mLoaction1Data0Adapter = new NumericWheelAdapter(mContext, mLocation1KgStartPos,
                    mLocation1KgEndPos);
        }
        mLoaction1Data0Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction1Data0Adapter.setItemTextResource(R.id.text);
        mLocation1WV.setViewAdapter(mLoaction1Data0Adapter);

        if (mLoaction2Data0Adapter == null) {
            mLoaction2Data0Adapter = new NumericWheelAdapter(mContext, mLocation2KgStartPos,
                    mLocation2KgEndPos);
        }
        mLoaction2Data0Adapter.setItemResource(R.layout.wheel_text_item);
        mLoaction2Data0Adapter.setItemTextResource(R.id.text);
        mLocation2WV.setViewAdapter(mLoaction2Data0Adapter);
        setCurrentData0Item();
    }

    private void setCurrentData0Item() {
        mLocation0WV.setCurrentItem(mInitLocation0Data0Indes);
        mLocation1WV.setCurrentItem(mInitLocation1Data0Indes);
        mLocation2WV.setCurrentItem(mInitLocation2Data0Indes);
    }

    // private boolean mLocation0WVScrolling = false;
    // private boolean mLocation1WVScrolling = false;
    // private boolean mLocation2WVScrolling = false;
    // private boolean mLocation3WVScrolling = false;

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // if (!mScrolling) {
        //
        // }

    }

    @Override
    public void onScrollingStarted(WheelView wheel) {

    }

    private void confirmHeightWheelNeedPosition(int flag) {
        int[] cmPosArray = new int[4];
        cmPosArray[0] = mLocation0WV.getCurrentItem();
        cmPosArray[1] = mLocation1WV.getCurrentItem();
        cmPosArray[2] = mLocation2WV.getCurrentItem();
        cmPosArray[3] = flag;
        if (flag == 0) {
            int[] transformedInNeedPos = transformCmToIn(cmPosArray);
            judgeWhetherHeightLegal(transformedInNeedPos);
            wheelCashPosition(transformedInNeedPos);
        } else if (flag == 1) {
            int[] transformedCmNeedPos = transformInToCm(cmPosArray);
            judgeWhetherHeightLegal(transformedCmNeedPos);
            wheelCashPosition(transformedCmNeedPos);
        }
    }

    private void confirmWeightWheelNeedPosition(int flag) {
        int[] cmPosArray = new int[4];
        cmPosArray[0] = mLocation0WV.getCurrentItem();
        cmPosArray[1] = mLocation1WV.getCurrentItem();
        cmPosArray[2] = mLocation2WV.getCurrentItem();
        cmPosArray[3] = flag;
        if (flag == 0) {
            int[] transformedLbNeedPos = transformKgToLb(cmPosArray);
            judgeWhetherWeightLegal(transformedLbNeedPos);
            wheelCashPosition(transformedLbNeedPos);
        } else if (flag == 1) {
            int[] transformedKgNeedPos = transformLbToKg(cmPosArray);
            judgeWhetherWeightLegal(transformedKgNeedPos);
            wheelCashPosition(transformedKgNeedPos);
        }
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
        switch (mDialogType) {
        case CHOOSER_HEIGHT_DIALOG:
            heightScrollFinished(wheel);
            break;
        case CHOOSER_WEIGHT_DIALOG:
            weightScrollFinished(wheel);
            break;
        case CHOOSER_BIRTHDAY_DIALOG:
            birthdayScrollFinished(wheel);
        default:
            break;
        }
    }

    private void heightScrollFinished(WheelView wheel) {
        if (wheel == mLocation3WV) {
            int pos = mLocation3WV.getCurrentItem();
            if (mInitLocation3Indes == 1 && pos == 0) {
                mInitLocation3Indes = 0;
                confirmHeightWheelNeedPosition(1);
                whenUnitIsCm();
            } else if (mInitLocation3Indes == 0 && pos == 1) {
                mInitLocation3Indes = 1;
                confirmHeightWheelNeedPosition(0);
                whenUnitIsIn();
            }

        } else {
            judgeHeightSelectLegal();
        }

    }

    private void weightScrollFinished(WheelView wheel) {
        if (wheel == mLocation3WV) {
            int pos = mLocation3WV.getCurrentItem();
            if (mInitLocation3Indes == 1 && pos == 0) {
                mInitLocation3Indes = 0;
                confirmWeightWheelNeedPosition(1);
                whenUnitIsKg();
            } else if (mInitLocation3Indes == 0 && pos == 1) {
                mInitLocation3Indes = 1;
                confirmWeightWheelNeedPosition(0);
                whenUnitIsLb();
            }
        } else {
            judgeWeightSelectLegal();
        }
    }
    
    private void birthdayScrollFinished(WheelView wheel) {
        judgeWhetherBirthdayLegal(mWheelPosition);
    }

    private void judgeHeightSelectLegal() {
        int unitPos = mLocation3WV.getCurrentItem();
        int cmPos0 = mLocation0WV.getCurrentItem();
        int cmPos1 = mLocation1WV.getCurrentItem();
        int cmPos2 = mLocation2WV.getCurrentItem();
        if (unitPos == 0) {
            if (isHeightCMMin(cmPos0 + mLocation0CmStartPos, cmPos1 + mLocation1CmStartPos, cmPos2
                    + mLocation2CmStartPos)) {
                mInitLocation0Data0Indes = HEIGHT_CM_HUNDREDS_PLACE_MIN;
                mInitLocation1Data0Indes = HEIGHT_CM_TENS_PLACE_MIN;
                mInitLocation2Data0Indes = HEIGHT_CM_UNIT_PLACE_MIN;
                mLocation0WV.setCurrentItem(mInitLocation0Data0Indes);
                mLocation1WV.setCurrentItem(mInitLocation1Data0Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data0Indes);
            } else if (isHeightCMMax(cmPos0 + mLocation0CmStartPos, cmPos1 + mLocation1CmStartPos,
                    cmPos2 + mLocation2CmStartPos)) {
                mInitLocation0Data0Indes = HEIGHT_CM_HUNDREDS_PLACE_MAX;
                mInitLocation1Data0Indes = HEIGHT_CM_TENS_PLACE_MAX;
                mInitLocation2Data0Indes = HEIGHT_CM_UNIT_PLACE_MAX;
                mLocation0WV.setCurrentItem(mInitLocation0Data0Indes);
                mLocation1WV.setCurrentItem(mInitLocation1Data0Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data0Indes);
            }
        } else if (unitPos == 1) {
            if (isHeightInMin(cmPos0 + mLocation0InStartPos, cmPos2 + mLocation2InStartPos)) {
                mInitLocation0Data1Indes = 0;
                mInitLocation2Data1Indes = HEIGHT_IN_IN_MIN;
                mLocation0WV.setCurrentItem(mInitLocation0Data1Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data1Indes);
            } else if (isHeightInMax(cmPos0 + mLocation0InStartPos, cmPos2 + mLocation2InStartPos)) {
                mInitLocation0Data1Indes = mLocation0WV.getViewAdapter().getItemsCount() - 1;
                mInitLocation2Data1Indes = mLocation2WV.getViewAdapter().getItemsCount() - 1;
                mLocation0WV.setCurrentItem(mInitLocation0Data1Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data1Indes);
            }

        }
    }

    private void judgeWeightSelectLegal() {
        int unitPos = mLocation3WV.getCurrentItem();
        int cmPos0 = mLocation0WV.getCurrentItem();
        int cmPos1 = mLocation1WV.getCurrentItem();
        int cmPos2 = mLocation2WV.getCurrentItem();
        if (unitPos == 0) {
            if (isWeightKGMin(cmPos0 + mLocation0KgStartPos, cmPos1 + mLocation1KgStartPos, cmPos2
                    + mLocation2KgStartPos)) {
                mInitLocation0Data0Indes = WEIGHT_KG_HUNDREDS_PLACE_MIN;
                mInitLocation1Data0Indes = WEIGHT_KG_TENS_PLACE_MIN;
                mInitLocation2Data0Indes = WEIGHT_KG_UNIT_PLACE_MIN;
                mLocation0WV.setCurrentItem(mInitLocation0Data0Indes);
                mLocation1WV.setCurrentItem(mInitLocation1Data0Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data0Indes);
            } else if (isWeightKGMax(cmPos0 + mLocation0KgStartPos, cmPos1 + mLocation1KgStartPos,
                    cmPos2 + mLocation2KgStartPos)) {
                mInitLocation0Data0Indes = WEIGHT_KG_HUNDREDS_PLACE_MAX;
                mInitLocation1Data0Indes = WEIGHT_KG_TENS_PLACE_MAX;
                mInitLocation2Data0Indes = WEIGHT_KG_UNIT_PLACE_MAX;
                mLocation0WV.setCurrentItem(mInitLocation0Data0Indes);
                mLocation1WV.setCurrentItem(mInitLocation1Data0Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data0Indes);
            }
        } else if (unitPos == 1) {
            if (isWeightLBMin(cmPos0 + mLocation0LbStartPos, cmPos1 + mLocation1LbStartPos, cmPos2
                    + mLocation2LbStartPos)) {
                mInitLocation0Data1Indes = WEIGHT_LB_HUNDREDS_PLACE_MIN;
                mInitLocation1Data1Indes = WEIGHT_LB_TENS_PLACE_MIN;
                mInitLocation2Data1Indes = WEIGHT_LB_UNIT_PLACE_MIN;
                mLocation0WV.setCurrentItem(mInitLocation0Data1Indes);
                mLocation1WV.setCurrentItem(mInitLocation1Data1Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data1Indes);
            } else if (isWeightLBMax(cmPos0 + mLocation0LbStartPos, cmPos1 + mLocation1LbStartPos,
                    cmPos2 + mLocation2LbStartPos)) {
                mInitLocation0Data1Indes = WEIGHT_LB_HUNDREDS_PLACE_MAX;
                mInitLocation1Data1Indes = WEIGHT_LB_TENS_PLACE_MAX;
                mInitLocation2Data1Indes = WEIGHT_LB_UNIT_PLACE_MAX;
                mLocation0WV.setCurrentItem(mInitLocation0Data1Indes);
                mLocation1WV.setCurrentItem(mInitLocation1Data1Indes);
                mLocation2WV.setCurrentItem(mInitLocation2Data1Indes);
            }
        }
    }

    private boolean isHeightCMMax(int hundred, int ten, int unit) {
        if ((100 * hundred + 10 * ten + unit) > HEIGHT_CM_MAX) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isHeightCMMin(int hundred, int ten, int unit) {
        if ((100 * hundred + 10 * ten + unit) < HEIGHT_CM_MIN) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isHeightInMax(int ft, int in) {
        if ((12 * ft + in) > HEIGHT_IN_MAX) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isHeightInMin(int ft, int in) {
        if ((12 * ft + in) < HEIGHT_IN_MIN) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isWeightKGMax(int hundred, int ten, int unit) {
        if ((100 * hundred + 10 * ten + unit) > WEIGHT_KG_MAX) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isWeightKGMin(int hundred, int ten, int unit) {
        if ((100 * hundred + 10 * ten + unit) < WEIGHT_KG_MIN) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isWeightLBMax(int hundred, int ten, int unit) {
        if ((100 * hundred + 10 * ten + unit) > WEIGHT_LB_MAX) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isWeightLBMin(int hundred, int ten, int unit) {
        if ((100 * hundred + 10 * ten + unit) < WEIGHT_LB_MIN) {
            return true;
        } else {
            return false;
        }
    }

}
