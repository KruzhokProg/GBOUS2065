package com.example.gbous2065.Models;

import java.util.List;

public interface ScheduleCallBack {
    void onSuccess(ScheduleByBuilding data, List<String> letters, List<String> grades);
}
