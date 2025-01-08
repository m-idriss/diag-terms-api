package com.dime.term;

import com.dime.model.TermRecord;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI)
public interface TermApiMapper {

  TermApiMapper INSTANCE = Mappers.getMapper(TermApiMapper.class);

  @Mapping(target = "word", source = "word", qualifiedByName = "lowercase")
  TermResponse toResponse(TermRecord termRecord);

  List<TermResponse> toResponses(List<TermRecord> termRecords);

  @Named("lowercase")
  default String lowercase(String word) {
    return word.toLowerCase();
  }

}