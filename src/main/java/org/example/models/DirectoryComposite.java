// Composite - Directory
package org.example.models;
import java.util.ArrayList;
import java.util.List;
public class DirectoryComposite extends FileComponent {
    private List<FileComponent> children = new ArrayList<>();

    public DirectoryComposite(String name) {
        super(name);
    }

    public void add(FileComponent component) {
        children.add(component);
    }

    public List<FileComponent> getChildren() {
        return children;
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder("{\"name\": \"" + name + "\", \"type\": \"directory\", \"children\": [");
        for (int i = 0; i < children.size(); i++) {
            json.append(children.get(i).toJson());
            if (i < children.size() - 1) json.append(", ");
        }
        json.append("]}");
        return json.toString();
    }
}
