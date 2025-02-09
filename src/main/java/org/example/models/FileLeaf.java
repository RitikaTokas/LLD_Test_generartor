package org.example.models;
// Leaf - File
public class FileLeaf extends FileComponent {
    private String content;

    public FileLeaf(String name, String content) {
        super(name);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toJson() {
        return String.format("{\"name\": \"%s\", \"type\": \"file\", \"content\": \"%s\"}", name, content);
    }
}