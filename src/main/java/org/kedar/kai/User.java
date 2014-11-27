package org.kedar.kai;

import java.util.Collections;
import java.util.List;

/**
 * Models the user, that is also the vertex in the graph.
 */
final class User implements Comparable<User> {
    final int id; // could be long?
    /**
     * Ids of the students that are coached by this user
     */
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

    /**
     * Implementation of natural ordering for instances of this class. This ordering is
     * consistent with equals. Thus, for another instance, <code>that</code>, of this class following holds:
     * <p>
     * <code>
     *     this.compareTo(that) = 0 if and only if this.equals(that)
     * </code>
     * </p>
     * @param that User another user to compare this user with
     * @return -1 if this user's id is &lt; that user's id, 1 if this user's id &gt; that user's id, 0 otherwise
     */
    @Override
    public int compareTo(User that) {
        if (this.id < that.id)
            return -1;
        if (this.id > that.id)
            return 1;
        return 0;
    }
    @Override
    public int hashCode() {
        return id;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User that = (User)o;
            return this.id == that.id;
        }
        return false;
    }
}
