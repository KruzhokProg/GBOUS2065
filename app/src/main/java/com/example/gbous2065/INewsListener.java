package com.example.gbous2065;

import android.widget.TextView;

public interface INewsListener {
    void onNewsClickListener(int position, TextView date,
                             TextView title);
}
