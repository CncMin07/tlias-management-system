package com.fallenleaves.service;


import com.fallenleaves.pojo.ClazzOption;
import com.fallenleaves.pojo.DegreeOption;
import com.fallenleaves.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {

    JobOption getEmpJobData();

    List<Map<String, Object>> getEmpGenderData();

    List<Map<String, Object>> getStudentDegreeData();

    ClazzOption countStudentData();
}
