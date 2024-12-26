package com.dime.term;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class Term {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  private String id;

  @Column(unique = true)
  @JsonProperty("word")
  private String word;

  @JsonProperty("synonyms")
  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> synonyms;

}