package com.example.userpost.util.convert;

import com.example.userpost.constant.HookEvent;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class HookEventConverter implements AttributeConverter<HookEvent, Integer> {

  @Override
  public Integer convertToDatabaseColumn(HookEvent state) {
    return state != null ? state.getCode() : null;
  }

  @Override
  public HookEvent convertToEntityAttribute(Integer integer) {
    return integer != null ? HookEvent.fromCode(integer) : null;
  }
}