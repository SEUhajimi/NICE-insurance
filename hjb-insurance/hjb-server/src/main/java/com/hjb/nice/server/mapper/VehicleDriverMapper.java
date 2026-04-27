package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.VehicleDriver;
import org.apache.ibatis.annotations.*;
import java.util.List;

// Driver now has a direct FK to Vehicle (hjb_driver.HJB_VEHICLE_VIN); hjb_vd is removed.
// These queries expose the same driver-vehicle pairs via hjb_driver.
@Mapper
public interface VehicleDriverMapper {

    @Select("SELECT HJB_VEHICLE_VIN, Driver_License AS HJB_DRIVER_Driver_License FROM hjb_driver")
    List<VehicleDriver> findAll();

    @Select("SELECT HJB_VEHICLE_VIN, Driver_License AS HJB_DRIVER_Driver_License " +
            "FROM hjb_driver WHERE HJB_VEHICLE_VIN = #{vin}")
    List<VehicleDriver> findByVin(String vin);

    @Select("SELECT HJB_VEHICLE_VIN, Driver_License AS HJB_DRIVER_Driver_License " +
            "FROM hjb_driver WHERE Driver_License = #{driverLicense}")
    List<VehicleDriver> findByDriverLicense(String driverLicense);
}
