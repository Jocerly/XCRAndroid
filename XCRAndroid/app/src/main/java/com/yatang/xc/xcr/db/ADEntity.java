package com.yatang.xc.xcr.db;

import java.util.List;

/**
 * Created by dengjiang on 2017/9/5.
 */

public class ADEntity {
    public List<ADEntityBase> ShortcutSecondList;
    public String ShortcutFirstName;
    public List<ADEntityBase> getShortcutSecondList() {
        return ShortcutSecondList;
    }

    public String getShortcutFirstName() {
        return ShortcutFirstName;
    }

    public void setShortcutFirstName(String shortcutFirstName) {
        ShortcutFirstName = shortcutFirstName;
    }

    public void setShortcutSecondList(List<ADEntityBase> shortcutSecondList) {
        ShortcutSecondList = shortcutSecondList;
    }
}
