package com.dime.term;

import com.dime.model.TermRecord;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class TermApiResponseMapperTest {


  @Test
  void testToResponse() {
    TermRecord termRecord = new TermRecord();
    termRecord.setId(1L);
    termRecord.setWord("word");
    termRecord.setSynonyms(List.of("synonym1", "synonym2"));

    TermResponse term = TermApiMapper.INSTANCE.toResponse(termRecord);

    assertEquals(termRecord.getId(), term.getId());
    assertEquals(termRecord.getWord(), term.getWord());
    assertEquals(termRecord.getSynonyms(), term.getSynonyms());
  }

  @Test
  void testToResponses() {
    TermRecord termRecord = new TermRecord();
    termRecord.setId(1L);
    termRecord.setWord("word");
    termRecord.setSynonyms(List.of("synonym1", "synonym2"));

    List<TermResponse> terms = TermApiMapper.INSTANCE.toResponses(List.of(termRecord));

    assertEquals(1, terms.size());
    assertEquals(termRecord.getId(), terms.get(0).getId());
    assertEquals(termRecord.getWord(), terms.get(0).getWord());
    assertEquals(termRecord.getSynonyms(), terms.get(0).getSynonyms());
  }

}
