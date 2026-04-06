package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Home;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HomeMapper {

    @Select("SELECT * FROM hjb_home")
    List<Home> findAll();

    @Select("SELECT * FROM hjb_home WHERE Home_ID = #{homeId}")
    Home findById(Integer homeId);

    @Select("SELECT * FROM hjb_home WHERE HJB_HOMEPOLICY_HP_ID = #{hpId}")
    List<Home> findByHomePolicyId(Integer hpId);

    @Insert("INSERT INTO hjb_home(Home_ID, PDate, PValue, Area, Home_Type, AFN, HSS, SP, Basement, HJB_HOMEPOLICY_HP_ID) " +
            "VALUES(#{homeId}, #{pdate}, #{pvalue}, #{area}, #{homeType}, #{afn}, #{hss}, #{sp}, #{basement}, #{hjbHomepolicyHpId})")
    void insert(Home home);

    @Update("UPDATE hjb_home SET PDate=#{pdate}, PValue=#{pvalue}, Area=#{area}, Home_Type=#{homeType}, " +
            "AFN=#{afn}, HSS=#{hss}, SP=#{sp}, Basement=#{basement}, HJB_HOMEPOLICY_HP_ID=#{hjbHomepolicyHpId} " +
            "WHERE Home_ID=#{homeId}")
    void update(Home home);

    @Delete("DELETE FROM hjb_home WHERE Home_ID = #{homeId}")
    void deleteById(Integer homeId);
}
