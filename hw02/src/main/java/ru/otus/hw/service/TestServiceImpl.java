package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private String userAnswerText;

    private Question question;

    private List<Question> questions;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var testResult = new TestResult(student);
        if (!getQuestions()) {
            return testResult;
        }

        for (Question question : questions) {
            this.question = question;
            printQuestion();
            printAnswer();
            readUserAnswer();
            testResult.applyAnswer(question, checkUserAnswer());
            printEmptyLine();
        }
        return testResult;
    }

    private void printEmptyLine() {
        ioService.printLine("");
    }

    private void printQuestion() {
        ioService.printFormattedLine("%s %n", question.text());
    }

    private void printAnswer() {
        for (int i = 0; i < question.answers().size(); i++) {
            Answer answer = question.answers().get(i);
            ioService.printFormattedLine("%d. %s%n", i + 1, answer.text());
        }
    }

    private void readUserAnswer() {
        userAnswerText = ioService.readStringWithPrompt("Enter the text of the correct answer: ").trim();
    }

    private boolean checkUserAnswer() {
        return question.answers().stream()
                .filter(a -> a.text().equalsIgnoreCase(userAnswerText))
                .findFirst()
                .map(Answer::isCorrect)
                .orElse(false);
    }

    private boolean  getQuestions() {
        try {
            questions = questionDao.findAll();
            return true;
        } catch (QuestionReadException e) {
            ioService.printFormattedLine("An error occurred while retrieving questions. %n");
            return false;
        }
    }
}
