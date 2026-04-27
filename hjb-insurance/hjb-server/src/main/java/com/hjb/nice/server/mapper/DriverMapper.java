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

    @Select("SELECT * FROM hjb_driver WHERE HJB_VEHICLE_VIN = #{vin}")
    List<Driver> findByVin(String vin);

    @Insert("INSERT INTO hjb_driver(Driver_License, FNAME, LNAME, Birthday, HJB_VEHICLE_VIN) " +
            "VALUES(#{driverLicense}, #{fname}, #{lname}, #{birthday}, #{hjbVehicleVin})")
    void insert(Driver driver);

    @Update("UPDATE hjb_driver SET FNAME=#{fname}, LNAME=#{lname}, Birthday=#{birthday}, " +
            "HJB_VEHICLE_VIN=#{hjbVehicleVin} WHERE Driver_License=#{driverLicense}")
    void update(Driver driver);

    @Delete("DELETE FROM hjb_driver WHERE Driver_License = #{driverLicense}")
    void deleteById(String driverLicense);
}
