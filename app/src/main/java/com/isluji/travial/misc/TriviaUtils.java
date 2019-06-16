package com.isluji.travial.misc;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TriviaUtils {

    /**
     * @see <a href="https://stackoverflow.com/questions/5062264/find-all-views-with-tag">
     *          Copyright: Shlomi Schwartz
     *     </a>
     */
    public static List<View> getViewsByTag(ViewGroup root, String tag){
        List<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);

            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();

            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }

        return views;
    }
}
