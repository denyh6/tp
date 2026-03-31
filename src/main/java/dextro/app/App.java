package dextro.app;

import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.exception.ParseException;
import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;
import dextro.parser.Parser;
import dextro.ui.Ui;

public class App {

    private final Parser parser;
    private final StudentDatabase db;
    private final Ui ui;
    private final Storage storage;

    public App(Ui ui, Parser parser, StudentDatabase db, Storage storage) {
        this.ui = ui;
        this.parser = parser;
        this.db = db;
        this.storage = storage;
    }

    public void run() {
        System.out.println("Welcome to Dextro Student Manager!");

        while (true) {
            System.out.print("> ");

            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            try {
                Command command = parser.parse(input);
                CommandResult result = command.execute(db, storage);
                Ui.show(result.getMessage());

                if (result.shouldExit()) {
                    break;
                }

            } catch (ParseException | CommandException | IllegalArgumentException e) {
                Ui.show("Error: " + e.getMessage());
            } catch (Exception e) {
                Ui.show("Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
