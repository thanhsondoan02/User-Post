package com.example.userpost.model.user;

import com.example.userpost.constant.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Gender state) {
    return state != null ? state.getCode() : null;
  }

  @Override
  public Gender convertToEntityAttribute(Integer integer) {
    return integer != null ? Gender.fromCode(integer) : null;
  }
}