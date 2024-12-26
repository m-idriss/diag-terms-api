package com.dime.term;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TermRecord {

  @JsonProperty("id")
  private String id;

  @JsonProperty("word")
  private String word;

  @JsonProperty("synonyms")
  private List<String> synonyms;

}