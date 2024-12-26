package com.dime.term;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TermMapper {

  public TermRecord toRecord(Term term) {
    TermRecord termRecord = new TermRecord();
    termRecord.setId(term.getId());
    termRecord.setWord(term.getWord());
    termRecord.setSynonyms(term.getSynonyms());
    return termRecord;
  }

}