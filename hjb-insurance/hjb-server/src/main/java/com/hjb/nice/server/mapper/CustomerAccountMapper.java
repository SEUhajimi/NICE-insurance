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

    @Insert("INSERT INTO customer_account(customer_id, username, password, email) " +
            "VALUES(#{customerId}, #{username}, #{password}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    void insert(CustomerAccount account);
}
