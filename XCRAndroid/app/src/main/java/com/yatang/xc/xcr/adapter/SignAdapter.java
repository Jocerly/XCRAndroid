package com.yatang.xc.xcr.adapter;

import com.yatang.xc.xcr.entity.SignEntity;
import com.yatang.xc.xcr.views.SignView;

import java.util.List;

/**
 * SignAdapter
 * Created by E.M on 2016/4/21.
 */
public class SignAdapter extends CalendarAdapter {
    private List<SignEntity> data;

    public SignAdapter(List<SignEntity> data) {
        this.data = data;
    }

    @Override
    public SignView.DayType getType(int dayOfMonth) {
        return SignView.DayType.valueOf(data.get(dayOfMonth - 1).getDayType());
    }
}
