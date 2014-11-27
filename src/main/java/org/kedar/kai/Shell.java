package org.kedar.kai;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Implements a basic shell to interact with the user. This is of course, not a full-fledged readline-compliant POSIX shell, but
 * a quick attempt to assess the Infection calculations. Currently, only following commands are supported:
 * <ul>
 * <ol>cls:     Clears the screen.</ol>
 * <ol>setup:   Sets up the testbed from a file named graph.conf.</ol>
 * <ol>version: Accepts a user-id and returns the version that user is on. Versions start at 1 and
 * increment by 1 on infection (no semver here ;)). </ol>
 * <ol>infect:  Accepts the starting user id and that user + all users connected to that user are infected. Each infected user's
 * current version is incremented by 1.</ol>
 * <ol>predict: Accepts the starting user id and predicts the number of infected users should the starting user id be
 * infected by the infect command.</ol>
 * <ol>groups: Prints some details of current connected components. A group's id is denoted by the user-id of the user that
 * is its temporary identifier.</ol>
 * <ol>limit~: Accepts a number that denotes the maximum number of users that could be affected. The command then
 * comes up with one possible selection of groups that infects approximately that many users. The printed groups give the
 * 'approximate limited infection'.</ol>
 * <ol>limit=: Accepts a number that denotes the maximum number of users that could be infected. The command then
 * prints 0 if it is not possible and 1, followed by the group-ids that may yield such 'exact limited infection'.
 * The policy implemented is: a connected component is either infected or not. </ol>
 * <ol>help:    Prints this message.</ol>
 * <ol>quit:    Quits the program. </ol>
 * </ul>
 *
 * @author kedar
 */
public class Shell {
    static final String SETUP = "setup";
    static final String VERSION = "version";
    static final String INFECT = "infect";
    static final String PREDICT = "predict";
    static final String GROUPS = "groups";
    static final String LIMIT_EXACT = "limit=";
    static final String LIMIT_APPROX = "limit~";
    static final String HELP = "help";
    static final String QUIT = "quit";
    static final String CLS = "cls";

    public static void main(String[] args) throws IOException {
        Console console = System.console();
        if (console == null)
            System.exit(1);
        PrintWriter writer = console.writer();
        greet(writer);
        printPrompt(writer);
        BufferedReader reader = new BufferedReader(console.reader());
        String cmd;
        ComponentBuilder builder = new ComponentBuilder();
        while ((cmd = reader.readLine()) != null) {
            cmd = cmd.toLowerCase().trim();
            if (QUIT.equals(cmd)) {
                System.exit(0);
            } else if (SETUP.equals(cmd)) {
                builder = new ComponentBuilder();
                BufferedReader filer = new BufferedReader(new FileReader("graph.conf"));
                writer.println(builder.process(filer));
                filer.close(); //TODO improve closing
            } else if ("h".equals(cmd) || HELP.equals(cmd)) {
                printHelp(writer);
            } else if (GROUPS.equals(cmd)) {
                writer.println(builder.componentsToString());
            } else if (cmd.startsWith(VERSION)) {
                try {
                    Scanner sc = new Scanner(cmd).useDelimiter(VERSION + "\\s+");
                    writer.println(builder.getUserVersion(sc.nextInt()));
                } catch (NoSuchElementException | IllegalStateException ex) {
                    writer.println("invalid command, it should be: version <number>, or enter help");
                } catch (NoSuchUserException e) {
                    writer.println(e.getMessage());
                }
            } else if (CLS.equals(cmd)) {
                System.out.println("TODO (should use jline) ...");
                printPrompt(writer);
            } else if (cmd.startsWith(PREDICT)) {
                try {
                    Scanner sc = new Scanner(cmd).useDelimiter(PREDICT + "\\s+");
                    int uid = sc.nextInt();
                    int infected = builder.predict(uid);
                    writer.println("If you infect user: " + uid + ", " + infected + " users will get infected in all");
                } catch (NoSuchElementException | IllegalStateException ex) {
                    writer.println("invalid command, it should be: " + PREDICT + " <number>, or enter help");
                } catch (NoSuchUserException e) {
                    writer.println(e.getMessage());
                }
            } else if (cmd.startsWith(INFECT)) {
                try {
                    Scanner sc = new Scanner(cmd).useDelimiter(INFECT + "\\s+");
                    int uid = sc.nextInt();
                    builder.infect(uid);
                    writer.println("As a result of infecting user: " + uid + ", " + builder.getComponent(uid).size() + " users are infected");
                    writer.println("These users' new version is: " + builder.getComponent(uid).getVersion());
                } catch (NoSuchElementException | IllegalStateException ex) {
                    writer.println("invalid command, it should be: " + INFECT + " <number>, or enter help");
                } catch (NoSuchUserException e) {
                    writer.println(e.getMessage());
                }
            } else if (cmd.startsWith(LIMIT_APPROX)) {
                try {
                    Scanner sc = new Scanner(cmd).useDelimiter(LIMIT_APPROX + "\\s+");
                    int limit = sc.nextInt();
                    Set<Component> infected = builder.limitApprox(limit);
                    if (infected.isEmpty()) {
                        writer.println("Given limit: " + limit + " is too low. No component is infected. Try with higher limit.");
                    } else {
                        writer.println("Given limit: " + limit + " infects the following groups:\n");
                        report(infected, writer);
                    }
                } catch (NoSuchElementException | IllegalStateException ex) {
                    writer.println("invalid command, it should be: " + LIMIT_APPROX + " <number>, or enter help");
                }
            } else if (cmd.startsWith(LIMIT_EXACT)) {

            } else {
                writer.println("I did not get that, here's some help for you to help me :-)");
                printHelp(writer);
            }
            printPrompt(writer);
        }
    }

    private static void report(Set<Component> infected, PrintWriter writer) {
        long sum = 0L;
        for (Component group : infected) {
            int size = group.size();
            writer.printf("Component with identifier: %10d, size: %6d%n", group.identifier().id, size);
            sum += size;
        }
        writer.printf("------ Total: %d%n", sum);
    }

    private static void printPrompt(PrintWriter writer) {
        writer.println();
        writer.print("$> ");
        writer.flush();
    }

    private static void greet(PrintWriter writer) {
        writer.println("Welcome to KA Graph Infection Testbed! I am a rudimentary shell. Following commands are available:");
        printHelp(writer);
    }

    private static void printHelp(PrintWriter writer) {
        writer.println("cls:     Clears the screen.");
        writer.println("groups:  Prints some details of current connected components. A group's id is denoted by the\n" +
                "                user-id of the user that is its identifier.");
        writer.println("help:    Prints this message.");
        writer.println("infect:  Accepts the starting user id and that user + all users connected to that user are\n" +
                "                infected. Each infected user's current version is incremented by 1.");
        writer.println("limit~:  Accepts a number that denotes the maximum number of users that could be affected.\n" +
                "                The command then comes up with one possible selection of groups that infects\n" +
                "                approximately that many users. Printed groups give the 'approximate limited infection'.");
        writer.println("limit=:  Accepts a number that denotes the maximum number of users that could be infected.\n" +
                "                The command then prints 0 if it is not possible and 1, followed by the group-ids that\n" +
                "                may yield such 'limited infection'. The policy implemented is: a connected component\n" +
                "                is either infected or not. (NP-complete?)");
        writer.println("predict: Accepts the starting user id and predicts the number of infected users should the\n" +
                "                starting user id be infected by the infect command.");
        writer.println("quit:    Quits the program.");
        writer.println("setup:   Sets up the testbed from a file named graph.conf.");
        writer.println("version: Accepts a user-id and returns the version that user is on. Versions start at 1 and \n" +
                "         increment by 1 on infection (no semver here ;)).");
    }
}
