package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Plan;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PlanMapper {

    @Select("SELECT * FROM hjb_plan WHERE is_active = 1 ORDER BY plan_type, amount")
    List<Plan> findActive();

    @Select("SELECT * FROM hjb_plan ORDER BY plan_type, amount")
    List<Plan> findAll();

    @Insert("INSERT INTO hjb_plan(plan_name, plan_type, amount, features, is_active) " +
            "VALUES(#{planName}, #{planType}, #{amount}, #{features}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "planId")
    void insert(Plan plan);

    @Update("UPDATE hjb_plan SET plan_name=#{planName}, plan_type=#{planType}, " +
            "amount=#{amount}, features=#{features}, is_active=#{isActive} " +
            "WHERE plan_id=#{planId}")
    void update(Plan plan);

    @Delete("DELETE FROM hjb_plan WHERE plan_id=#{planId}")
    void deleteById(Integer planId);
}
