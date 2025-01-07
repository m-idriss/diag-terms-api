package com.dime.term;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TermResponse {

  private Long id;
  private String word;
  private List<String> synonyms;
}
