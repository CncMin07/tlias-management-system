package com.fallenleaves.service;

import com.fallenleaves.pojo.Clazz;
import com.fallenleaves.pojo.ClazzQueryParam;
import com.fallenleaves.pojo.PageResult;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public interface ClazzService {


    PageResult<Clazz> page(ClazzQueryParam clazzQueryParam);

    void deleteClazz(Integer id);

    void saveClazz(Clazz clazz);

    Clazz getClazzById(Integer id);

    void updateClazz(Clazz clazz);

    List<Clazz> findAll();
}
