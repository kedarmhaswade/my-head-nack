package org.kedar.kai;

import java.util.HashSet;
import java.util.Iterator;

/**
 * A group, or connected subgraph of the Graph, calling it a component is perhaps a mistake, but
 * I am doing that in the interest of time. In a given ComponentBuilder of KA users (for this assignment)
 * each component identifies the transitive relationship between coaches and students. The
 * identifier of a component is a User that identifies that component. When adding component
 * B to A (merging B with A), the identifier of each user in B is changed to the identifier of A.
 * Iteration efficiency is important here (although, this implementation still uses HashSet, whose iteration
 * efficiency is not great -- TODO).
 *
 * @author kedar
 */
class Component implements Iterable<User>, Comparable<Component> {
    private User identifier;
    private HashSet<User> members;

    Component(User identifier) {
        this.identifier = identifier;
        this.members = new HashSet<>();
        this.addMember(identifier);
    }

    int size() {
        return this.members.size();
    }

    User identifier() {
        return this.identifier;
    }

    /**
     * The classic union operation -- the only mutating method this class exposes.
     *
     * @param other the other component whose members are to be merged into this one.
     */
    void merge(Component other) {
        Iterator<User> oi = other.iterator();
        while (oi.hasNext()) {
            User m = oi.next();
            this.addMember(m);
        }
    }

    void addMember(User m) {
        this.members.add(m);
        m.setComponent(this);
    }

    /**
     * Infects each member of this component. A version parameter could be passed for upgrade/downgrade scenarios (TODO)
     */
    void infect() {
        int v = this.identifier.version() + 1; //bumped-up version
        for (User u : this)
            u.infect(v);
    }

    int getVersion() {
        return this.identifier.version();
    }

    @Override
    public Iterator<User> iterator() {
        return members.iterator();
    }

    @Override
    public String toString() {
        return "Identifier user id: " + this.identifier.id + ", number of connected users: " + this.size() + ": " + this.members;
    }

    /**
     * Implements the natural ordering of components.
     * @param that Component whose relative order is to be determined
     * @return -1, 0 or 1
     */
    @Override
    public int compareTo(Component that) {
        int idc = this.identifier.compareTo(that.identifier); //id comparison
        int sc; //member size comparison
        if (this.members.equals(that.members))
            sc = 0;
        else {
            if (this.members.size() < that.members.size())
                sc = -1;
            else
                sc = 1;
        }
        if (idc == 0) //two components have the 'same' identifier
            return sc;
        else
            return idc;
    }

    //Private ...

}
