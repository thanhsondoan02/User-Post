package com.example.userpost.model.base;

import com.example.userpost.constant.State;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class StateWriteConverter implements Converter<State, Integer> {
  @Override
  public Integer convert(@NonNull State source) {
    return source.getCode();
  }
}

