package com.dime.term;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TermApi {

  @JsonProperty("id")
  private String id;

  @JsonProperty("word")
  private String word;

  @JsonProperty("synonyms")
  private List<String> synonyms;
}