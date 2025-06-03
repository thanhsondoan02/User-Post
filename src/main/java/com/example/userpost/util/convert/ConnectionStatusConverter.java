package com.example.userpost.util.convert;

import com.example.userpost.constant.ConnectionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ConnectionStatusConverter implements AttributeConverter<ConnectionStatus, Integer> {

  @Override
  public Integer convertToDatabaseColumn(ConnectionStatus state) {
    return state != null ? state.getCode() : null;
  }

  @Override
  public ConnectionStatus convertToEntityAttribute(Integer integer) {
    return integer != null ? ConnectionStatus.fromCode(integer) : null;
  }
}