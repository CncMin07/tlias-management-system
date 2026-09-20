package com.fallenleaves.mapper;


import com.fallenleaves.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpExprMapper {
    void insertbatch(List<EmpExpr> exprList);

    void deleteByEmpIds(List<Integer> empIds);
}
