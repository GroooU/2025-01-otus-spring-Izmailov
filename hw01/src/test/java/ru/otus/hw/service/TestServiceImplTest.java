package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.mockito.Mockito.*;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void shouldPrintQuestionsAndAnswers() {
        List<Answer> answers = List.of(
                new Answer("Answer 1", true),
                new Answer("Answer 2", false)
        );
        List<Question> questions = List.of(
                new Question("Question 1", answers)
        );

        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printFormattedLine("%s %n", "Question 1");
        verify(ioService).printFormattedLine("%d. %s %n", 1, "Answer 1");
        verify(ioService).printFormattedLine("%d. %s %n", 2, "Answer 2");
        verify(ioService).printFormattedLine("", "Question 1");
    }

    @Test
    void shouldHandleQuestionReadException() {
        when(questionDao.findAll()).thenThrow(new QuestionReadException("Failed to read"));

        testService.executeTest();

        verify(ioService).printFormattedLine("An error occurred while retrieving questions: %s%n", "Failed to read");
    }
}