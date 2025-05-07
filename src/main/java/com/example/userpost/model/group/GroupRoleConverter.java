package com.example.userpost.model.group;

import com.example.userpost.constant.GroupRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GroupRoleConverter implements AttributeConverter<GroupRole, Integer> {
  @Override
  public Integer convertToDatabaseColumn(GroupRole groupRole) {
    return groupRole != null ? groupRole.getCode() : null;
  }

  @Override
  public GroupRole convertToEntityAttribute(Integer integer) {
    return integer != null ? GroupRole.fromCode(integer) : null;
  }
}