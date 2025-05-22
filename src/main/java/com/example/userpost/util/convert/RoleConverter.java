package com.example.userpost.util.convert;

import com.example.userpost.constant.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter implements AttributeConverter<Role, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Role state) {
    return state != null ? state.getCode() : null;
  }

  @Override
  public Role convertToEntityAttribute(Integer integer) {
    return integer != null ? Role.fromCode(integer) : null;
  }
}