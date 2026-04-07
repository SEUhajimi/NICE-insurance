package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.CustomerAccount;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CustomerAccountMapper {

    @Select("SELECT * FROM customer_account WHERE username = #{username}")
    CustomerAccount findByUsername(String username);

    @Select("SELECT * FROM customer_account WHERE email = #{email}")
    CustomerAccount findByEmail(String email);

    @Select("SELECT * FROM customer_account WHERE customer_id = #{customerId}")
    CustomerAccount findByCustomerId(Integer customerId);

    @Insert("INSERT INTO customer_account(customer_id, username, password, email, fname, lname, gender, marital_status, addr_street, addr_city, addr_state, zipcode) " +
            "VALUES(#{customerId}, #{username}, #{password}, #{email}, #{fname}, #{lname}, #{gender}, #{maritalStatus}, #{addrStreet}, #{addrCity}, #{addrState}, #{zipcode})")
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    void insert(CustomerAccount account);

    @Update("UPDATE customer_account SET password = #{password} WHERE account_id = #{accountId}")
    void updatePassword(@Param("accountId") Integer accountId, @Param("password") String password);

    @Update("UPDATE customer_account SET customer_id = #{customerId} WHERE account_id = #{accountId}")
    void updateCustomerId(@Param("accountId") Integer accountId, @Param("customerId") Integer customerId);
}
