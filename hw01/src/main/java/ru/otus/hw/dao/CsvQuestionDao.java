package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<Question> questions;

        InputStream inputStream = getResourcesAsStream(fileNameProvider.getTestFileName());
        try (InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader)) {
            questions = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withIgnoreEmptyLine(true)
                    .withSeparator(';')
                    .build()
                    .parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());
        } catch (IOException | RuntimeException e) {
            throw new QuestionReadException("error  reading questions", e);
        }

        checkEmptyQuestions(questions);

        return questions;
    }

    private InputStream getResourcesAsStream(String fileName) throws QuestionReadException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (Objects.isNull(inputStream)) {
            throw new QuestionReadException("file not found: " + fileName);
        }

        return inputStream;
    }

    private void checkEmptyQuestions(List<Question> questions) {
        if (questions.isEmpty()) {
            throw new QuestionReadException("question is empty.");
        }
    }
}
