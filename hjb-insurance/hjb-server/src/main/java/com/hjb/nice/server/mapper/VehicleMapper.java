package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Vehicle;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VehicleMapper {

    @Select("SELECT * FROM hjb_vehicle")
    List<Vehicle> findAll();

    @Select("SELECT * FROM hjb_vehicle WHERE VIN = #{vin}")
    Vehicle findById(String vin);

    @Select("SELECT * FROM hjb_vehicle WHERE HJB_AUTOPOLICY_AP_ID = #{apId}")
    List<Vehicle> findByAutoPolicyId(Integer apId);

    @Insert("INSERT INTO hjb_vehicle(VIN, MMY, Status, HJB_AUTOPOLICY_AP_ID) " +
            "VALUES(#{vin}, #{mmy}, #{status}, #{hjbAutopolicyApId})")
    void insert(Vehicle vehicle);

    @Update("UPDATE hjb_vehicle SET MMY=#{mmy}, Status=#{status}, " +
            "HJB_AUTOPOLICY_AP_ID=#{hjbAutopolicyApId} WHERE VIN=#{vin}")
    void update(Vehicle vehicle);

    @Delete("DELETE FROM hjb_vehicle WHERE VIN = #{vin}")
    void deleteById(String vin);
}
