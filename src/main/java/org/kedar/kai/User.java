package org.kedar.kai;

import java.util.Collections;
import java.util.List;

/**
 * Models the user, that is also the vertex in the graph.
 */
public final class User {
    final int id; // could be long?
    /** Ids of the students that are coached by this user */
    private final List<User> students; //is int[] enough? ... perhaps
    private int version;
    private Component component;

    User(int id, int version, List<User> students) {
        this.id = id;
        this.version = version;
        this.component = null;
        this.students = students;
    }
    User(int id) {
        this(id, 1, Collections.<User>emptyList());
    }

    void infect(int version) {
        this.version = version;
    }

    int version() {
        return this.version;
    }
    void setComponent(Component c) {
        this.component = c;
    }
    Component getComponent() {
        return this.component;
    }
    List<User> getStudents() {
        return students;
    }
    @Override
    public String toString() {
        return "id: " + id;
    }
}
