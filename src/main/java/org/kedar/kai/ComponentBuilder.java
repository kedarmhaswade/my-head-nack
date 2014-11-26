package org.kedar.kai;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Models a class that builds the Components. The essence of this exercise is to build a data structure
 * that is more like write-once-read-always in that we create the connected components from a given
 * set of relationships and query them for various things of interest w.r.t. infections. This is the reason we
 * do not capture the adjacency list/matrix representation of the graph at all. In a bigger project, of course,
 * this is not what we would do, but instead, add the vertex references into components and each vertex
 * would point to its component (like we do here too).
 * @author kedar
 */
class ComponentBuilder {

    private final SortedSet<Component> components;
    private User[] vertices;

    ComponentBuilder() {
        Comparator<Component> sizeComparator = new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                if (o1.size() < o2.size())
                    return -1;
                if (o1.size() > o2.size())
                    return 1;
                return 0;
            }
        };
        /*
        Implementation note: Initially, I had used HashSet here, which worked for the 'infect' command, but
        the need for a sorted set on size arose when implementing the limit~ command and I felt like using
        TreeSet where the components are sorted by their sizes.
         */
        components = new TreeSet<>(sizeComparator);
    }

    String process(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine(); //the first line should contain number of vertices
        vertices = new User[Integer.valueOf(line) + 1]; // to use more straightforward indexing where index = user's id
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#"))
                continue;
            StringTokenizer t = new StringTokenizer(line, " ,");
            if (!t.hasMoreTokens())
                continue;
            int[] sids = new int[t.countTokens() - 1];
            int uid = Integer.valueOf(t.nextToken());
            int i = 0;
            while (t.hasMoreTokens())
                sids[i++] = Integer.valueOf(t.nextToken());
            processLine(uid, sids);
        }
        return "Done! Processed: " + (vertices.length-1) + " vertices, formed: " + components.size() + " component(s)";
    }

    public String componentsToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("There are " + components.size() + " components or groups. Details:\n");
        int i = 1;
        for (Component c : components) {
            sb.append(i).append(") ").append(c).append("\n");
            i += 1;
        }
        return sb.toString();
    }

    int getUserVersion(int uid) throws NoSuchUserException {
        if (uid > vertices.length)
            throw new NoSuchUserException("invalid user id: " + uid + ", user ids up to " + vertices.length + " are available");
        return vertices[uid].version();
    }

    /**
     * Returns the number of users that would be infected, should you decide to infect the given user.
     * The strategy is to infect a group completely if the given user is a member of it.
     * @param uid int id of the user to be infected
     * @return int number of users that would be infected
     */
    int predict(int uid) throws NoSuchUserException {
        if (uid > vertices.length)
            throw new NoSuchUserException("invalid user id: " + uid + ", user ids up to " + vertices.length + " are available");
        return this.vertices[uid].getComponent().size();
    }

    void infect(int uid) throws NoSuchUserException {
        if (uid > vertices.length)
            throw new NoSuchUserException("invalid user id: " + uid + ", user ids up to " + vertices.length + " are available");
        Component component = this.vertices[uid].getComponent();
        component.infect();
    }
    Component getComponent(int uid) throws NoSuchUserException {
        if (uid > vertices.length)
            throw new NoSuchUserException("invalid user id: " + uid + ", user ids up to " + vertices.length + " are available");
        return this.vertices[uid].getComponent();
    }

    /**
     * Implements a rather straightforward greedy algorithm to limit the infection to a
     * @param limit
     * @return
     */
    Set<Component> limitApprox(int limit) {
        return null;
    }

    // PRIVATE

    /**
     * Processes a line of the form: 1 2,3,4 from the graph.conf file.
     *
     * @param uid  int user id, the user who coaches a class
     * @param sids int[] ids of the users who are coached by the uid
     */
    private void processLine(int uid, int[] sids) {
        Component group;
        if (vertices[uid] == null) {
            User nu = new User(uid);
            vertices[uid] = nu;
            group = new Component(nu);
            components.add(group);
        } else {
            group = vertices[uid].getComponent();
        }
        for (int sid : sids) {
            if (vertices[sid] == null) {
                User nm = new User(sid);
                vertices[sid] = nm;
                group.addMember(nm);
            } else {
                Component other = vertices[sid].getComponent();
                if (other != group) {
                    group.merge(other);
                    components.remove(other);
                }
            }
        }
//        System.out.println("processed line for: " + uid);
//        System.out.println(this.componentsToString());
    }
}