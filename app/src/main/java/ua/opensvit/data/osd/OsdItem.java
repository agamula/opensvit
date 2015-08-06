package ua.opensvit.data.osd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OsdItem {
    private final List<ProgramDurationItem> programs = new ArrayList<>();

    public void addProgram(ProgramDurationItem programDurationItem) {
        programs.add(programDurationItem);
    }

    public List<ProgramDurationItem> getUnmodifiablePrograms() {
        return Collections.unmodifiableList(programs);
    }
}
