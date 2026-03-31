package dextro;

import java.io.IOException;

import dextro.app.App;
import dextro.app.Storage;
import dextro.parser.Parser;
import dextro.model.record.StudentDatabase;
import dextro.ui.Ui;

public class Main {

    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        StudentDatabase db;
        try {
            db = new StudentDatabase(storage.loadStudentList());
        } catch (IOException e) {
            Ui.show(e.getMessage());
            db = new StudentDatabase();
        }

        App app = new App(ui, parser, db, storage);
        app.run();
    }
}
