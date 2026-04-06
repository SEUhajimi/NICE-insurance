package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee findByUsername(String username);

    @Select("SELECT * FROM employee WHERE emp_id = #{empId}")
    Employee findById(Integer empId);

    @Select("SELECT * FROM employee")
    List<Employee> findAll();

    @Insert("INSERT INTO employee(username, password, email, fname, lname, role) " +
            "VALUES(#{username}, #{password}, #{email}, #{fname}, #{lname}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "empId")
    void insert(Employee employee);

    @Update("UPDATE employee SET email=#{email}, fname=#{fname}, lname=#{lname}, role=#{role} " +
            "WHERE emp_id=#{empId}")
    void update(Employee employee);

    @Delete("DELETE FROM employee WHERE emp_id = #{empId}")
    void deleteById(Integer empId);
}
