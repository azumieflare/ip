package teemo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Storage class to handle save/load of tasks.
 */
public class Storage {
    private File f;
    /**
     * Constructor for Storage
     *
     * @param filePath Path to the save file.
     */
    public Storage(String filePath) {
        this.f = new File(filePath);
    }

    /**
     * Saves current taskList into a .txt file.
     *
     * @param taskList List of tasks to save.
     */
    public String save(List<Task> taskList) throws IOException {
        FileWriter fw = new FileWriter(f, false); // create a new file
        String text = "";

        for (Task t : taskList) {
            text = text + t.toString() + "\n";
        }

        fw.write(text);
        fw.close();

        return "\tSuccessfully saved!\n";
    }

    /**
     * Loads from .txt file to current taskList.
     *
     * @return Updated taskList.
     */
    public List<Task> load() throws DukeException, IOException {
        if (f.createNewFile()) { // check if save exist
            throw new DukeException();
        }

        List<Task> taskList = new ArrayList<>();
        Scanner s = new Scanner(f);
        String curr;
        String taskType;
        String isMarked;

        Task task;

        while (s.hasNext()) {
            curr = s.nextLine();
            taskType = curr.substring(1, 2);
            isMarked = curr.substring(4, 5);
            task = createTask(curr, taskType, isMarked);
            taskList.add(task);
        }
        return taskList;
    }

    /**
     * Creates a new task.
     * @param curr string value of stored information.
     * @param taskType type of task, to do, deadline or event.
     * @param isMarked whether the task should be marked or not.
     * @return the created task.
     */
    private Task createTask(String curr, String taskType, String isMarked) {
        String taskName;
        LocalDate start;
        LocalDate finish;
        int index;
        Task task;

        index = curr.indexOf("(", curr.lastIndexOf("]"));
        if (index == -1) {
            index = curr.length() + 1;
        }

        taskName = curr.substring(7, index - 1);

        if (taskType.equals("T")) { // To Do
            task = new ToDo(taskName);
        } else if (taskType.equals("D")) { // Deadline
            finish = LocalDate.parse(curr.substring(curr.indexOf("by: ") + 4, curr.lastIndexOf(")")));
            task = new Deadline(taskName, finish);
        } else { // Event, assuming input file is always correct format
            start = LocalDate.parse(curr.substring(curr.indexOf("from: ") + 6, curr.lastIndexOf("to:") - 1));
            finish = LocalDate.parse(curr.substring(curr.indexOf("to: ") + 4, curr.lastIndexOf(")")));
            task = new Event(taskName, start, finish);
        }

        if (isMarked.equals("X")) {
            task.mark();
        }

        return task;
    }
}
