package ua.opensvit.data.osd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OsdItem {
    private final List<ProgramItem> programs = new ArrayList<>();

    public void addProgram(ProgramItem programItem) {
        programs.add(programItem);
    }

    public List<ProgramItem> getUnmodifiablePrograms() {
        return Collections.unmodifiableList(programs);
    }
}
