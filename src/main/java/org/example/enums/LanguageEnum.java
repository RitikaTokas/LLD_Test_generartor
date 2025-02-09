package org.example.enums;

public enum LanguageEnum {
    PYTHON("Python"),
    JAVA("Java"),
    C_PLUS_PLUS("C++"),
    JAVASCRIPT("JavaScript"),
    RUBY("Ruby"),
    SWIFT("Swift"),
    KOTLIN("Kotlin"),
    GO("Go"),
    PHP("PHP"),
    C_SHARP("C#"),
    TYPESCRIPT("TypeScript"),
    SCALA("Scala"),
    RUST("Rust"),
    DART("Dart");

    private final String displayName;

    LanguageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}