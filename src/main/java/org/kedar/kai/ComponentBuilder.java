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
 *
 * @author kedar
 */
class ComponentBuilder {

    private final Set<Component> components;
    private User[] vertices;

    ComponentBuilder() {
        Comparator<Component> sizeComparator = new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                if (o1.size() < o2.size())
                    return -1;
                if (o1.size() > o2.size())
                    return 1;
                //return 0; //bug!
                return o1.compareTo(o2);
            }
        };
//        components = new HashSet<>();
        components = new TreeSet<>(sizeComparator);
    }

    String process(BufferedReader reader) throws IOException {
        String line;
        line = reader.readLine(); //the first line should contain number of vertices
        vertices = new User[Integer.valueOf(line) + 1]; // to use more straightforward indexing where index = user's id
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#"))
                break;
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
        return "Done! Processed: " + (vertices.length - 1) + " vertices, formed: " + components.size() + " component(s)";
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
     *
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
     * Implements a dynamic programming algorithm to limit the infection to the given limit.
     * If the limit is 100 and the group sizes are 10, 125, 200 ..., then only the first group (size: 10) is infected.
     * The strategy is to never infect a group partially. An alternate strategy could be to infect fewest possible members
     * from the groups that get partially infected to reach the limit.
     *
     * @param limit int indicating upper limit.
     * @return a Set that contains the components that are infected if the limit could be satisfied.
     */
    Set<Component> limitApprox(int limit) {
        Set<Component> set = new HashSet<>();
        /* Implementation note:
            This is similar to the knapsack problem where the objective is to fill the knapsack with as many
            whole items as possible are picked and the items do not repeat => An item can be either picked or not.
            The difference is that the optimization here is that of 'size'/'weight' alone and no item value is involved.
            The algorithm is not guaranteed to return an optimal answer, but that interpretation is pardonable per problem statement.
            Algorithm: Dasgupta-Papadimitriou-Vazirani: pp. 182. The following algorithm is a slight variation of the same.
         */
        // we are unconcerned about all the components that are bigger than the limit, we intend to find that
        // selection of components, whose sizes together are approximately as big as limit
        // first, capture those components in a list
        List<Component> cList = new ArrayList<>();
        for (Component c : components) {
//            System.out.println("iterated component: " + c);
            if (c.size() > limit)
                break;
            cList.add(c); //this list is sorted on the size of components
        }
//        System.out.println(cList);
        int[][] max = new int[limit + 1][cList.size() + 1];
        //max[i][j] denotes the maximum size possible for a limit of i and with groups 1,2,...,j being available
        //goal is the find max[limit][cList.size()] and traverse the limit row backward to get the groups picked
        for (int i = 0; i <= limit; i++)
            max[i][0] = 0; //when there are no groups available, maximum sum is 0, regardless of value of limit
        for (int j = 0; j <= cList.size(); j++)
            max[0][j] = 0; //when limit is 0, no choice of groups can help
        for (int i = 1; i <= limit; i++) {
            for (int j = 1; j <= cList.size(); j++) {
                int size = cList.get(j - 1).size();
                if (size > i) {
                    max[i][j] = max[i][j - 1];
                } else {
                    int m = max[i][j - 1];
                    if (i - size >= 0 && max[i - size][j - 1] + size <= limit)
                        m = max[i - size][j - 1] + size;
                    max[i][j] = m;
                }
            }
        }
//        print2da(max);
        System.out.println("max possible: " + max[limit][cList.size()]);
        //now imagine that our 'knapsack' were of size limit and we were to pick those groups that fit in it ...
        for (int i = limit, j = cList.size(); j >= 1; ) { //process last row
            if (max[i][j] > max[i][j - 1]) { //this item is picked!
                Component picked = cList.get(j - 1);
                set.add(picked);
                int rem = max[i][j] - picked.size();
                while (max[i][j] != rem && j >= 0) //is that j>=0 needed? TODO
                    j -= 1;
            } else
                j -= 1;
        }
        return set;
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
                if (other != group) { //reference comparison!
                    group.merge(other);
                    components.remove(other);
                }
            }
        }
//        System.out.println("processed line for uid: " + uid + ", sids: " + Arrays.toString(sids));
//        System.out.println(this.componentsToString());
    }

    private void print2da(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++)
                System.out.printf("%3d ", a[i][j]);
            System.out.println();
        }
    }
}