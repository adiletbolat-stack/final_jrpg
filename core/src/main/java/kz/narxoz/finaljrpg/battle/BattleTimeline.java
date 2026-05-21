package kz.narxoz.finaljrpg.battle;

import java.util.ArrayList;
import java.util.List;

public class BattleTimeline {
    private final List<List<BattleInput>> tracks = new ArrayList<>();

    public BattleTimeline(int trackCount) {
        for (int i = 0; i < trackCount; i++) {
            tracks.add(new ArrayList<>());
        }
    }

    public BattleInput getInput(int track, int frame) {
        List<BattleInput> inputs = tracks.get(track);

        if (frame < 0 || frame >= inputs.size()) {
            return BattleInput.EMPTY;
        }

        return inputs.get(frame);
    }

    public void recordInput(int track, int frame, BattleInput input) {
        List<BattleInput> inputs = tracks.get(track);

        while (inputs.size() <= frame) {
            inputs.add(BattleInput.EMPTY);
        }

        inputs.set(frame, input);
    }
}
