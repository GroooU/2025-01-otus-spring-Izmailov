package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;
    private Question question;
    private Student student;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);

        List<Answer> answers = List.of(
                new Answer("Answer 1", false),
                new Answer("Answer 2", true),
                new Answer("Answer 3", false)
        );

        question = new Question("Question 1", answers);
        student = new Student("John", "Doe");
    }

    @Test
    void shouldEvaluateCorrectAnswer() {
        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readStringWithPrompt(any())).thenReturn("Answer 2");

        TestResult result = testService.executeTestFor(student);

        assertEquals(1, result.getRightAnswersCount());
        assertEquals(student, result.getStudent());
    }

    @Test
    void shouldEvaluateWrongAnswer() {
        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readStringWithPrompt(any())).thenReturn("Answer 3");

        TestResult result = testService.executeTestFor(student);

        assertEquals(0, result.getRightAnswersCount());
        assertEquals(student, result.getStudent());
    }

    @Test
    void shouldHandleQuestionReadException() {
        when(questionDao.findAll()).thenThrow(new QuestionReadException(""));

        TestResult result = testService.executeTestFor(student);

        assertEquals(0, result.getRightAnswersCount());
        assertEquals(student, result.getStudent());
    }
}