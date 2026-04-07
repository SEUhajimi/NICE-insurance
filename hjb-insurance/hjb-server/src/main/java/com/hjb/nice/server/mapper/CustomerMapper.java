package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Customer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerMapper {

    @Select("SELECT * FROM hjb_customer")
    List<Customer> findAll();

    @Select("SELECT * FROM hjb_customer WHERE CUST_ID = #{custId}")
    Customer findById(Integer custId);

    @Insert("INSERT INTO hjb_customer(CUST_ID, FNAME, LNAME, Gender, Marital_status, Cust_Type, Addr_Street, Addr_City, Addr_State, Zipcode) " +
            "VALUES(#{custId}, #{fname}, #{lname}, #{gender}, #{maritalStatus}, #{custType}, #{addrStreet}, #{addrCity}, #{addrState}, #{zipcode})")
    void insert(Customer customer);

    @Update("UPDATE hjb_customer SET FNAME=#{fname}, LNAME=#{lname}, Gender=#{gender}, " +
            "Marital_status=#{maritalStatus}, Cust_Type=#{custType}, Addr_Street=#{addrStreet}, " +
            "Addr_City=#{addrCity}, Addr_State=#{addrState}, Zipcode=#{zipcode} WHERE CUST_ID=#{custId}")
    void update(Customer customer);

    @Delete("DELETE FROM hjb_customer WHERE CUST_ID = #{custId}")
    void deleteById(Integer custId);

    @Update("UPDATE hjb_customer SET Cust_Type=#{custType} WHERE CUST_ID=#{custId}")
    void updateCustType(@Param("custId") Integer custId, @Param("custType") String custType);

    // 注册时使用，依赖数据库 AUTO_INCREMENT 生成 CUST_ID
    @Insert("INSERT INTO hjb_customer(FNAME, LNAME, Gender, Marital_status, Cust_Type, Addr_Street, Addr_City, Addr_State, Zipcode) " +
            "VALUES(#{fname}, #{lname}, #{gender}, #{maritalStatus}, #{custType}, #{addrStreet}, #{addrCity}, #{addrState}, #{zipcode})")
    @Options(useGeneratedKeys = true, keyProperty = "custId")
    void insertForRegister(Customer customer);
}
