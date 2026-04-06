package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.VehicleDriver;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VehicleDriverMapper {

    @Select("SELECT * FROM hjb_vd")
    List<VehicleDriver> findAll();

    @Select("SELECT * FROM hjb_vd WHERE HJB_VEHICLE_VIN = #{vin}")
    List<VehicleDriver> findByVin(String vin);

    @Select("SELECT * FROM hjb_vd WHERE HJB_DRIVER_Driver_License = #{driverLicense}")
    List<VehicleDriver> findByDriverLicense(String driverLicense);

    @Insert("INSERT INTO hjb_vd(HJB_VEHICLE_VIN, HJB_DRIVER_Driver_License) " +
            "VALUES(#{hjbVehicleVin}, #{hjbDriverDriverLicense})")
    void insert(VehicleDriver vd);

    @Delete("DELETE FROM hjb_vd WHERE HJB_VEHICLE_VIN = #{vin} AND HJB_DRIVER_Driver_License = #{driverLicense}")
    void delete(@Param("vin") String vin, @Param("driverLicense") String driverLicense);
}
