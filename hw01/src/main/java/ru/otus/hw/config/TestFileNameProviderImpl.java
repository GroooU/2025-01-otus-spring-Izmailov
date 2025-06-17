package ru.otus.hw.config;

public class TestFileNameProviderImpl implements TestFileNameProvider {

    private final String testFileName;

    public TestFileNameProviderImpl(String testFileName) {
        this.testFileName = testFileName;
    }

    @Override
    public String getTestFileName() {
        return testFileName;
    }
}