package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Driver;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DriverMapper {

    @Select("SELECT * FROM hjb_driver")
    List<Driver> findAll();

    @Select("SELECT * FROM hjb_driver WHERE Driver_License = #{driverLicense}")
    Driver findById(String driverLicense);

    @Insert("INSERT INTO hjb_driver(Driver_License, FNAME, LNAME, Birthday) " +
            "VALUES(#{driverLicense}, #{fname}, #{lname}, #{birthday})")
    void insert(Driver driver);

    @Update("UPDATE hjb_driver SET FNAME=#{fname}, LNAME=#{lname}, Birthday=#{birthday} " +
            "WHERE Driver_License=#{driverLicense}")
    void update(Driver driver);

    @Delete("DELETE FROM hjb_driver WHERE Driver_License = #{driverLicense}")
    void deleteById(String driverLicense);
}
